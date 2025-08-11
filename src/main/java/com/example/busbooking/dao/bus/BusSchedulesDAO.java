package com.example.busbooking.dao.bus;

import com.example.busbooking.dao.base.ScheduleDAO;
import com.example.busbooking.db.DBConnection;
import com.example.busbooking.dto.base.SchedulesDTO;
import com.example.busbooking.dto.bus.BusSchedulesDetailsDTO;
import com.example.busbooking.enums.BookingStatus;
import com.example.busbooking.enums.ScheduleStatus;
import com.example.busbooking.registry.BookingStatusRegistry;
import com.example.busbooking.registry.ScheduleStatusRegistry;
import com.example.busbooking.service.ScheduleService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.busbooking.db.DBConstants.*;

/**
 * DAO class responsible for handling all database operations related to bus schedules,
 * including adding, updating, deleting, and retrieving schedule and seat data.
 */

public class BusSchedulesDAO implements ScheduleDAO {

    // SQL to get all schedules with details
    private static final String select_all_schedules_query = String.format("select schedule_id, route_id, status_id, bus_id, departure_time, arrival_time, price, journey_date\n" + "from %s", SCHEDULES);


    // SQL to get details of a specific schedule.
    private static final String get_schedule_details_query = String.format("""
            SELECT r.route_id, b.bus_id, s.price, sc.city_name as source_city, dc.city_name as destination_city, b.bus_type, b.operator_name,
            s.schedule_id, b.bus_number, s.departure_time, s.arrival_time, s.journey_date, b.total_columns
            FROM %s s
            JOIN %s b ON s.bus_id = b.bus_id
            JOIN %s r on s.route_id = r.route_id
            JOIN %s sc on r.source_city_id = sc.city_id
            JOIN %s dc on r.destination_city_id = dc.city_id
            WHERE s.schedule_id = ? and s.status_id = 1;
            """, SCHEDULES, BUSES, ROUTES, CITIES, CITIES);


    // SQL to retrieve scheduled seat details like seat ID, seat type, row no, col no etc.
    private static final String get_seat_details_query = String.format("""
            select s.row_number, sg.col_number, ss_statuses.status_code, sg.pos, s.seat_type_id, s.seat_number, ss.scheduled_seat_id, st.seat_type_name
            from %s s
            join %s sg on s.column_id = sg.column_id
            join %s st on st.seat_type_id = s.seat_type_id
            join %s ss on ss.seat_id = s.seat_id
            join %s ss_statuses on ss_statuses.status_id = ss.status_id
            where ss.schedule_id = ? order by ss.scheduled_seat_id;
            """, SEATS, SEAT_GRID_COLUMNS, SEAT_TYPE, SCHEDULED_SEATS, SCHEDULED_SEAT_STATUSES);


    // SQL to check if a schedule exist.
    private static final String check_schedule_exist_query = String.format("select * from %s where schedule_id = ?", SCHEDULES);

    // SQL to update the schedule details.
    private static final String update_schedule_query = String.format("""
            update %s set
            route_id = ?,
            bus_id = ?,
            departure_time = ?,
            arrival_time = ?,
            price = ?,
            journey_date = ?
            where schedule_id = ?
            """, SCHEDULES);


    // SQL to change the booking seats status to be cancelled.
    private static final String update_booking_seats_status_id_query = String.format("""
                        UPDATE %s
                        SET status_id = ?
                        WHERE booking_id IN (
                                SELECT booking_id
                                FROM bookings
                                WHERE schedule_id = ?
                        ) and status_id = ?;
            """, BOOKING_SEATS);


    // SQL to update the status of the schedule to be cancelled
    private static final String update_schedule_status_query = String.format("""
            UPDATE %s SET status_id = ? WHERE schedule_id = ?
            """, SCHEDULES);


    /**
     * Fetches all available schedules from the DB.
     *
     * @return Returns a list of all schedules with details
     * @throws Exception if DB error occurs.
     */
    public Response getSchedules() throws Exception {

        // List to store all schedules.
        List<SchedulesDTO> allSchedules = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(select_all_schedules_query);) {

            // Select all schedules
            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    int schedule_id = rs.getInt("schedule_id");
                    int routeId = rs.getInt("route_id");
                    int busId = rs.getInt("bus_id");
                    String dep_time = rs.getString("departure_time");
                    String arr_time = rs.getString("arrival_time");
                    double price = rs.getDouble("price");
                    String journeyDate = rs.getString("journey_date");
                    int status_id = rs.getInt("status_id");

                    allSchedules.add(new SchedulesDTO(schedule_id, routeId, busId, dep_time, arr_time, price, journeyDate, status_id));
                }
            }
        }

        return Response.ok("Got all schedules").entity(allSchedules).build();
    }

    /**
     * Adds a new schedule to the database including associated stops and seats.
     *
     * @param schedulesDTO the schedule details to be inserted
     * @return Response indicating success or failure
     * @throws Exception if any SQL or transaction error occurs
     */
    public Response addNewSchedule( SchedulesDTO schedulesDTO ) throws Exception {

        // Check for valid schedule values consumed from the client.
        Map<String, String> validScheduleValues = ScheduleService.checkValidScheduleValues(schedulesDTO);
        if (validScheduleValues.get("allowed").equals("false")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validScheduleValues.get("message")).build();
        }

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Adding new schedule
            Integer scheduleId = ScheduleService.insertSchedule(schedulesDTO, conn);

            // Set the schedule ID.
            schedulesDTO.setScheduleId(scheduleId);

            // This function adds scheduled seats in the scheduled_seats table.
            ScheduleService.addScheduledSeats(schedulesDTO, conn);

            // This function adds the stops for BOARDING / DROPPING points.
            ScheduleService.addStops(conn, scheduleId, "BOARDING", schedulesDTO.getBoardingPointIds(), schedulesDTO.getRouteId());
            ScheduleService.addStops(conn, scheduleId, "DROPPING", schedulesDTO.getDroppingPointIds(), schedulesDTO.getRouteId());

            conn.commit();

        } catch (BadRequestException badRequestException) {

            return Response.status(Response.Status.BAD_REQUEST).entity(badRequestException.getMessage()).build();

        } catch (SQLException sqlException) {

            // BAD REQUEST response if the schedule already exists.
            if (sqlException.getSQLState().equals(UNIQUE_VIOLATION)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Schedule already exists.").build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error.").build();

        } catch (Exception e) {

            DBConnection.rollbackConnection(conn);
            System.out.println(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unable To Add New Schedule").build();

        } finally {

            DBConnection.closeConnection(conn);

        }
        return Response.ok().entity("Schedule created").build();
    }


    /**
     * Retrieves detailed information about a specific schedule,
     * including seat layout and route/city info.
     *
     * @param scheduleId the ID of the schedule to retrieve
     * @return Response containing BusSchedulesDetailsDTO or error if not found
     */

    public Response getScheduleDetails( int scheduleId ) throws Exception {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement scheduleStatement = conn.prepareStatement(get_schedule_details_query);
             PreparedStatement seatStatement = conn.prepareStatement(get_seat_details_query);) {

            // GET all the details of a particular schedule

            scheduleStatement.setInt(1, scheduleId);

            BusSchedulesDetailsDTO sd;
            try (ResultSet rs = scheduleStatement.executeQuery()) {

                // Return NOT_FOUND if scheduled is not found.
                if (!rs.next()) {
                    return Response.status(Response.Status.NOT_FOUND).entity("Schedule not found or schedule has been cancelled.").build();
                }

                // GET all the seat details for a schedule.
                seatStatement.setInt(1, scheduleId);

                List<BusSchedulesDetailsDTO.SeatDTO> seats;
                try (ResultSet seatRs = seatStatement.executeQuery()) {
                    // List to store the seat details
                    seats = new ArrayList<>();

                    // Adding the seats.
                    while (seatRs.next()) {
                        seats.add(new BusSchedulesDetailsDTO.SeatDTO(seatRs.getString("seat_type_name"),
                                seatRs.getInt("seat_type_id"), seatRs.getString("pos"),
                                seatRs.getString("seat_number"), seatRs.getString("status_code"),
                                seatRs.getInt("row_number"), seatRs.getInt("col_number"),
                                seatRs.getInt("scheduled_seat_id")));
                    }
                }


                // Constructing schedule details object to return complete information about the schedule.
                sd = new BusSchedulesDetailsDTO(rs.getInt("route_id"),
                        rs.getInt("bus_id"), rs.getInt("schedule_id"),
                        rs.getString("bus_number"), rs.getString("departure_time"),
                        rs.getString("arrival_time"), seats, rs.getString("source_city"),
                        rs.getString("destination_city"), rs.getString("operator_name"),
                        rs.getString("bus_type"), rs.getDouble("price"), rs.getString("journey_date"),
                        rs.getInt("total_columns"));
            }

            return Response.ok(sd).build();

        }
    }


    /**
     * Cancels a schedule if it exists in the database.
     *
     * @param scheduleId the ID of the schedule to cancel
     * @return Response indicating success or failure
     */


    public Response cancelSchedule( int scheduleId ) throws Exception {
        Connection conn = null;
        PreparedStatement updateScheduleStatement = null, updateBookingSeatsStatusStatement = null;
        try {

            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            updateScheduleStatement = conn.prepareStatement(update_schedule_status_query);
            updateBookingSeatsStatusStatement = conn.prepareStatement(update_booking_seats_status_id_query);

            // Check if schedule is already cancelled
            if (!ScheduleService.validateScheduleIsActive(scheduleId, conn)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Schedule already cancelled / Schedule doesn't exist.").build();
            }

            // Change the scheduled seats statuses to BLOCKED after cancelling the schedule
            ScheduleService.updateScheduledSeatsStatusToBlocked(scheduleId, conn);

            // Change the status_id in the bookings_seats table to 3 (cancelled by admin)
            updateBookingSeatsStatusStatement.setInt(1, BookingStatusRegistry.getByCode(BookingStatus.CANCELLED_BY_ADMIN.name()).getStatusId());
            updateBookingSeatsStatusStatement.setInt(2, scheduleId);
            updateBookingSeatsStatusStatement.setInt(3, BookingStatusRegistry.getByCode(BookingStatus.BOOKED.name()).getStatusId());
            updateBookingSeatsStatusStatement.executeUpdate();

            // Change the status_id in the schedules table to 2 (cancelled)
            updateScheduleStatement.setInt(1, ScheduleStatusRegistry.getByCode(ScheduleStatus.CANCELLED.name()).getStatusId());
            updateScheduleStatement.setInt(2, scheduleId);
            updateScheduleStatement.executeUpdate();

            conn.commit();
            return Response.ok("Schedule Cancelled.").build();

        } catch (Exception e) {
            e.printStackTrace();

            DBConnection.rollbackConnection(conn);
            System.out.println(e.getMessage());
            throw e;

        } finally {

            DBConnection.closeConnection(conn);
            DBConnection.closePreparedStatement(updateScheduleStatement);
            DBConnection.closePreparedStatement(updateBookingSeatsStatusStatement);

        }
    }


    /**
     * Updates an existing schedule and resets seat mapping
     * if the associated bus has changed.
     *
     * @param schedulesDTO updated schedule data
     * @return Response indicating success or failure
     * @throws Exception on SQL or rollback failure
     */
    public Response updateSchedule( SchedulesDTO schedulesDTO ) throws Exception {

        // Check for valid schedule values consumed from the client.
        Map<String, String> validScheduleValues = ScheduleService.checkValidScheduleValues(schedulesDTO);
        if (validScheduleValues.get("allowed").equals("false")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validScheduleValues.get("message")).build();

        }

        LocalDate journeyDate;
        LocalDateTime departure;
        LocalDateTime arrival;
        try {
            journeyDate = LocalDate.parse(schedulesDTO.getJourneyDate());
            departure = LocalDateTime.parse(schedulesDTO.getDepartureTime());
            arrival = LocalDateTime.parse(schedulesDTO.getArrivalTime());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input.").build();
        }


        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);


            if (ScheduleService.hasBookingsForSchedule(schedulesDTO.getScheduleId(), conn)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("The schedule cannot be updated as bookings have already been made for it.").build();
            }

            // Update all the schedule details.
            statement = conn.prepareStatement(update_schedule_query);
            statement.setInt(1, schedulesDTO.getRouteId());
            statement.setInt(2, schedulesDTO.getBusId());
            statement.setTimestamp(3, Timestamp.valueOf(departure));
            statement.setTimestamp(4, Timestamp.valueOf(arrival));
            statement.setDouble(5, schedulesDTO.getPrice());
            statement.setDate(6, Date.valueOf(journeyDate));
            statement.setInt(7, schedulesDTO.getScheduleId());
            statement.executeUpdate();


            // GET the BUS associated with the schedule ID
            Integer existingBusId = ScheduleService.getBusIdByScheduleId(schedulesDTO.getScheduleId(), conn);

            if (existingBusId == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid bus ID").build();
            }

            // Suppose the BUS is updated by the user, then the previous bus scheduled seats should be removed
            // and the new bus scheduled seats should be added.
            ScheduleService.updateScheduledSeatsIfBusChanged(existingBusId, schedulesDTO.getBusId(), schedulesDTO, conn);


            conn.commit();
        } catch (Exception e) {

            DBConnection.rollbackConnection(conn);
            return Response.status(Response.Status.CONFLICT).entity("Unable To Update New Schedule").build();

        } finally {

            DBConnection.closePreparedStatement(statement);

        }
        return Response.ok().entity("Schedule updated.").build();
    }


}