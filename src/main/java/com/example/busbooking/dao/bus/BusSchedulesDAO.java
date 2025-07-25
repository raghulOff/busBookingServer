package com.example.busbooking.dao.bus;

import com.example.busbooking.dao.base.ScheduleDAO;
import com.example.busbooking.db.DBConnection;
import com.example.busbooking.dto.base.SchedulesDTO;
import com.example.busbooking.dto.bus.BusSchedulesDetailsDTO;
import com.example.busbooking.model.BookingStatus;
import com.example.busbooking.model.ScheduleStatus;
import com.example.busbooking.service.ScheduleService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
            select s.row_number, sg.col_number, ss.status, sg.pos, s.seat_type_id, s.seat_number, ss.scheduled_seat_id, st.seat_type_name
            from %s s
            join %s sg on s.column_id = sg.column_id
            join %s st on st.seat_type_id = s.seat_type_id
            join %s ss on ss.seat_id = s.seat_id
            where ss.schedule_id = ? order by ss.scheduled_seat_id;
            """, SEATS, SEAT_GRID_COLUMNS, SEAT_TYPE, SCHEDULED_SEATS);

    // SQL to get all the seats associated with the bus ID from seat_grid_columns table
    // For each column of a bus ID in the SEAT_GRID_COLUMNS table their respective rows are in the SEATS table.
    private static final String get_seat_id_query = String.format("""
            select seat_id
            from %s s
            join %s sg on s.column_id = sg.column_id
            where sg.bus_id = ?
            """, SEATS, SEAT_GRID_COLUMNS);

    // SQL to insert the seats of a bus in the SCHEDULED SEATS table
    // This helps to keep track of seat status in particular to a schedule.
    private static final String insert_scheduled_seats_query = String.format("""
            insert into %s (seat_id, schedule_id, status)
            values (?, ?, false);
            """, SCHEDULED_SEATS);


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

    // Query to delete a scheduled seat.
    private static final String delete_scheduled_seats = String.format("""
            delete from %s where schedule_id = ?
            """, SCHEDULED_SEATS);

    // Query to get bus ID associated with a schedule ID
    private static final String get_schedule_id_bus_id_query = String.format("select schedule_id, bus_id from %s where schedule_id = ?;", SCHEDULES);

    // SQL to insert location stops (BOARDING/DROPPING)
    private static final String insert_stops_query = String.format("insert into %s (schedule_id, location_id, stop_type_id) values (?, ?, ?)", STOPS);

    // SQL to get source and destination ID from route ID
    private static final String get_source_destination_from_route_id_query = String.format("select source_city_id, destination_city_id from %s where route_id = ?", ROUTES);

    // SQL to get the respective city of a location.
    private static final String get_city_from_location_id_query = String.format("select city_id from %s where location_id = ?", CITY_LOCATIONS);

    // SQL to cancel the booking seats status to be cancelled.
    private static final String update_booking_seats_status_id_query = String.format("""
                        UPDATE %s
                        SET status_id = ?
                        WHERE booking_id IN (
                                SELECT booking_id
                                FROM bookings
                                WHERE schedule_id = ?
                        ) and status_id = 1;
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
        if (ScheduleService.checkValidScheduleValues(schedulesDTO)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
        }

        // Null check
        if (schedulesDTO.getBoardingPointIds() == null || schedulesDTO.getDroppingPointIds() == null
                || schedulesDTO.getBoardingPointIds().isEmpty() || schedulesDTO.getDroppingPointIds().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
        }

        LocalDate journeyDate;
        LocalDateTime departure;
        LocalDateTime arrival;
        try {
            journeyDate = LocalDate.parse(schedulesDTO.getJourneyDate());
            departure = LocalDateTime.parse(schedulesDTO.getDepartureTime());
            arrival = LocalDateTime.parse(schedulesDTO.getArrivalTime());
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input." + e.getMessage()).build();
        }

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Adding new schedule

            Integer scheduleId = ScheduleService.createScheduleWithGeneratedId(schedulesDTO, journeyDate, departure, arrival, conn);

            // Set the schedule ID.
            schedulesDTO.setScheduleId(scheduleId);

            // This function adds scheduled seats in the scheduled_seats table.
            addScheduledSeats(schedulesDTO, conn);

            // This function adds the stops for BOARDING / DROPPING points.
            addStops(conn, scheduleId, "BOARDING", schedulesDTO.getBoardingPointIds(), schedulesDTO.getRouteId());
            addStops(conn, scheduleId, "DROPPING", schedulesDTO.getDroppingPointIds(), schedulesDTO.getRouteId());

            conn.commit();

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
     * Inserts stop details (either boarding or dropping) for a schedule.
     *
     * @param connection  JDBC connection
     * @param scheduleId  the schedule's ID
     * @param type        "BOARDING" or "DROPPING"
     * @param locationIds list of location IDs to be inserted
     * @param routeId     route ID to validate city associations
     * @throws Exception if stop cities don't match route cities
     */

    private void addStops( Connection connection, int scheduleId, String type, List<Integer> locationIds, int routeId ) throws Exception {

        Integer required_city_id = null;
        Integer stop_type = ScheduleService.getStopTypeId(type);

        // Any stop location going to be added in the schedule must be a part of the source city or destination city based on the
        // type (BOARDING / DROPPING)

        try (PreparedStatement statement = connection.prepareStatement(get_source_destination_from_route_id_query);) {
            statement.setInt(1, routeId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    if (type.equals("BOARDING")) {
                        required_city_id = rs.getInt("source_city_id");
                    } else {
                        required_city_id = rs.getInt("destination_city_id");
                    }
                }
            }
        }


        try (PreparedStatement stopStatement = connection.prepareStatement(insert_stops_query);
             PreparedStatement cityStatement = connection.prepareStatement(get_city_from_location_id_query);) {

            // For each location ID, add the location in the stops table.
            for (Integer locationId : locationIds) {

                cityStatement.setInt(1, locationId);
                try (ResultSet rs = cityStatement.executeQuery()) {

                    // If the associated city of a location is not the same as required city then throws exception
                    if (rs.next()) {
                        if (required_city_id != null && rs.getInt("city_id") != required_city_id) {
                            throw new BadRequestException();
                        }
                    }
                }

                stopStatement.setInt(1, scheduleId);
                stopStatement.setInt(2, locationId);
                stopStatement.setInt(3, stop_type);
                stopStatement.addBatch();
            }
            stopStatement.executeBatch();
        }
    }

    /**
     * Retrieves detailed information about a specific schedule,
     * including seat layout and route/city info.
     *
     * @param scheduleId the ID of the schedule to retrieve
     * @return Response containing BusSchedulesDetailsDTO or error if not found
     */

    public Response getScheduleDetails( int scheduleId ) {

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
                                seatRs.getString("seat_number"), seatRs.getString("status"),
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

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Something went wrong").build();
        }
    }

    private static final String check_schedule_status_query = String.format("""
            select status_id from %s where schedule_id = ?
            """, SCHEDULES);


    /**
     * Cancels a schedule if it exists in the database.
     *
     * @param scheduleId the ID of the schedule to cancel
     * @return Response indicating success or failure
     */


    public Response cancelSchedule( int scheduleId ) throws Exception {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement updateScheduleStatement = conn.prepareStatement(update_schedule_status_query);
             PreparedStatement updateBookingSeatsStatusStatement = conn.prepareStatement(update_booking_seats_status_id_query);
             PreparedStatement checkScheduleStatusStatement = conn.prepareStatement(check_schedule_status_query)) {

            // Check if schedule is already cancelled
            checkScheduleStatusStatement.setInt(1, scheduleId);

            try (ResultSet checkScheduleStatusRs = checkScheduleStatusStatement.executeQuery()) {
                if (checkScheduleStatusRs.next()) {
                    if (checkScheduleStatusRs.getInt(1) != ScheduleStatus.ACTIVE.getId()) { // 1 refers to active state
                        return Response.status(Response.Status.BAD_REQUEST).entity("Schedule already cancelled.").build();
                    }
                } else {
                    // If the schedule doesn't exist.
                    return Response.status(Response.Status.BAD_REQUEST).entity("Schedule doesn't exist.").build();
                }
            }


            // Change the status_id in the bookings_seats table to 3 (cancelled by admin)
            updateBookingSeatsStatusStatement.setInt(1, BookingStatus.CANCELLED_BY_ADMIN.getId());
            updateBookingSeatsStatusStatement.setInt(2, scheduleId);
            updateBookingSeatsStatusStatement.executeUpdate();

            // Change the status_id in the schedules table to 2 (cancelled)
            updateScheduleStatement.setInt(1, ScheduleStatus.CANCELLED.getId());
            updateScheduleStatement.setInt(2, scheduleId);
            updateScheduleStatement.executeUpdate();

            return Response.ok("Schedule Cancelled.").build();
        }
    }



    // Query to check if a booking exist for a particular schedule.
    public static final String check_booking_schedule_exist_query = String.format("""
            select b.booking_id
            from %s b
            where b.schedule_id = ?;
            """, BOOKINGS);

    /**
     * Updates an existing schedule and resets seat mapping
     * if the associated bus has changed.
     *
     * @param schedulesDTO updated schedule data
     * @return Response indicating success or failure
     * @throws Exception on SQL or rollback failure
     */
    public Response updateSchedule( SchedulesDTO schedulesDTO ) throws Exception {

        // Check for valid user input parameters
        if (ScheduleService.checkValidScheduleValues(schedulesDTO)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
        }

        LocalDate journeyDate;
        LocalDateTime departure;
        LocalDateTime arrival;
        try {
            journeyDate = LocalDate.parse(schedulesDTO.getJourneyDate());
            departure = LocalDateTime.parse(schedulesDTO.getDepartureTime());
            arrival = LocalDateTime.parse(schedulesDTO.getArrivalTime());
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input." + e.getMessage()).build();
        }


        Connection conn = null;
        PreparedStatement busIdStatement = null, statement = null, deleteStatement = null, checkBookingStatement = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);


            // Check if any booking exist for this schedule. If exist then this schedule cannot be updated.
            checkBookingStatement = conn.prepareStatement(check_booking_schedule_exist_query);
            checkBookingStatement.setInt(1, schedulesDTO.getScheduleId());

            try (ResultSet bookingRs = checkBookingStatement.executeQuery()) {
                if (bookingRs.next()) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("The schedule cannot be updated as bookings have already been made for it.").build();
                }
            }


            // GET the BUS associated with the schedule ID
            busIdStatement = conn.prepareStatement(get_schedule_id_bus_id_query);
            busIdStatement.setInt(1, schedulesDTO.getScheduleId());

            Integer existingBusId;
            try (ResultSet busIdRs = busIdStatement.executeQuery()) {
                existingBusId = null;

                // If the schedule ID is not found, then return.
                if (busIdRs.next()) {
                    existingBusId = busIdRs.getInt("bus_id");
                } else {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
                }
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


            // Suppose the BUS is updated by the user, then the previous bus scheduled seats should be removed
            // and the new bus scheduled seats should be added.

            if (!existingBusId.equals(schedulesDTO.getBusId())) {
                // removes the existing scheduled seats because once the bus changes, new set of seats should be added in the scheduled_seats table.
                deleteStatement = conn.prepareStatement(delete_scheduled_seats);
                deleteStatement.setInt(1, schedulesDTO.getScheduleId());
                deleteStatement.executeUpdate();

                // adds the new scheduled seats;
                addScheduledSeats(schedulesDTO, conn);
            }

            conn.commit();
        } catch (Exception e) {

            DBConnection.rollbackConnection(conn);
            return Response.status(Response.Status.CONFLICT).entity("Unable To Update New Schedule").build();

        } finally {

            DBConnection.closePreparedStatement(busIdStatement);
            DBConnection.closePreparedStatement(statement);
            DBConnection.closePreparedStatement(deleteStatement);

        }
        return Response.ok().entity("Schedule updated.").build();
    }


    /**
     * Adds all seat entries for a bus into the scheduled_seats table for a specific schedule
     * This links each physical seat with the given schedule, initializing them as unbooked
     *
     * @param schedulesDTO schedule data with bus ID and generated schedule ID
     * @param conn         JDBC connection
     * @throws Exception in case of SQL errors
     */

    private void addScheduledSeats( SchedulesDTO schedulesDTO, Connection conn ) throws Exception {
        PreparedStatement statement = null, insertScheduledSeatsStatement = null;
        try {
            // Fetch all seat IDs associated with the bus
            statement = conn.prepareStatement(get_seat_id_query);
            statement.setInt(1, schedulesDTO.getBusId());
            try (ResultSet rs = statement.executeQuery()) {

                // Prepare to insert those seats into the scheduled_seats table for the current schedule
                insertScheduledSeatsStatement = conn.prepareStatement(insert_scheduled_seats_query);

                while (rs.next()) {
                    insertScheduledSeatsStatement.setInt(1, rs.getInt("seat_id"));
                    insertScheduledSeatsStatement.setInt(2, schedulesDTO.getScheduleId());
                    insertScheduledSeatsStatement.addBatch();
                }
            }
            insertScheduledSeatsStatement.executeBatch();

        } finally {

            DBConnection.closePreparedStatement(statement);
            DBConnection.closePreparedStatement(insertScheduledSeatsStatement);

        }
    }
}
