package com.example.busbooking.dao.bus;

import com.example.busbooking.dao.base.BookingsDAO;
import com.example.busbooking.db.DBConnection;
import static com.example.busbooking.db.DBConstants.*;
import com.example.busbooking.dto.base.BookingSeatsDTO;
import com.example.busbooking.dto.base.BookingsDTO;
import com.example.busbooking.enums.BookingStatus;
import com.example.busbooking.enums.Role;
import com.example.busbooking.registry.BookingStatusRegistry;
import com.example.busbooking.service.BookingsService;
import com.example.busbooking.service.ScheduleService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * DAO class for handling booking-related database operations,
 */

public class BusBookingsDAO implements BookingsDAO {




    // SQL to fetch all bookings by a specific user
    private static final String get_bookings_query = String.format("""
            select b.booking_id, s.journey_date, b.total_amount, bus.operator_name, sc.city_name as src, dc.city_name as destination,
            s.arrival_time, s.departure_time, lb.location_name as boarding, ld.location_name as dropping
            from %s b
            join %s s on b.schedule_id = s.schedule_id
            join %s bus on s.bus_id = bus.bus_id
            join %s r on s.route_id = r.route_id
            join %s sc on sc.city_id = r.source_city_id
            join %s dc on dc.city_id = r.destination_city_id
            join %s lb on lb.location_id = b.boarding_point_id
            join %s ld on ld.location_id = b.dropping_point_id
            join %s u on u.user_id = b.user_id
            where b.user_id = ? order by b.booking_id desc;
            """, BOOKINGS, SCHEDULES, BUSES, ROUTES, CITIES, CITIES, LOCATIONS, LOCATIONS, USERS);



    // SQL to fetch passenger details for a given booking and user
    private static final String get_passenger_details_query = String.format("""
                     select ss.scheduled_seat_id, s.seat_number, p.passenger_name, p.passenger_age, b.booking_id, bs.status_id
                     from %s bs
                     join %s p on bs.passenger_id = p.passenger_id
                     join %s b on b.booking_id = bs.booking_id
            join %s ss on ss.scheduled_seat_id = bs.scheduled_seat_id
                     join %s s on ss.seat_id = s.seat_id
                     where b.user_id = ? and b.booking_id = ?;""", BOOKING_SEATS, PASSENGER_DETAILS, BOOKINGS, SCHEDULED_SEATS, SEATS);






    /**
     * Retrieves all bookings made by a specific user.
     *
     * @param userId the user whose bookings are to be fetched
     * @param loggedInUserId ID of the currently authenticated user
     * @return HTTP response containing a list of bookings or an error status
     */

    public Response getAllBookings( int userId, int loggedInUserId ) throws Exception {


        // Check if the logged-in user and booking history requested user are the same
        if (loggedInUserId != userId) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("the logged in user is not allowed to review the requested user bookings.").build();
        }
        List<BookingsDTO> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(get_bookings_query)) {

            statement.setInt(1, userId);

            // List to store all the booking's data.
            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    String journeyDate = rs.getString("journey_date");
                    double totalAmount = rs.getDouble("total_amount");
                    String operatorName = rs.getString("operator_name");
                    String sourceCity = rs.getString("src");
                    String destinationCity = rs.getString("destination");
                    String arrivalTime = rs.getString("arrival_time");
                    String departureTime = rs.getString("departure_time");
                    String boardingLocation = rs.getString("boarding");
                    String droppingLocation = rs.getString("dropping");
                    int bookingId = rs.getInt("booking_id");

                    // Retrieves passenger details of each booking.
                    List<Map<String, Object>> passengerDetails = getPassengerDetails(userId, bookingId);

                    // All the details of each booking including passenger details.
                    bookings.add(new BookingsDTO(journeyDate, totalAmount, operatorName, sourceCity, destinationCity,
                            arrivalTime, departureTime, boardingLocation, droppingLocation, passengerDetails));
                }
            }
        }
        return Response.ok(bookings).build();

    }


    /**
     * Returns all passenger details under a specific booking.
     *
     * @param userId ID of the user
     * @param bookingId ID of the booking
     * @return list of passenger detail maps
     * @throws Exception if the query fails
     */

    public List<Map<String, Object>> getPassengerDetails( int userId, int bookingId ) throws Exception {

        try (Connection conn = DBConnection.getConnection();
             // GET passenger details with (user ID, booking ID)
             PreparedStatement statement = conn.prepareStatement(get_passenger_details_query)) {

            statement.setInt(1, userId);
            statement.setInt(2, bookingId);


            // List to store all the passengers with passenger details
            List<Map<String, Object>> passengerDetails = new ArrayList<>();


            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {

                    // Map to store the passenger details
                    Map<String, Object> passenger = new HashMap<>();

                    String seatNumber = rs.getString("seat_number");
                    String passengerName = rs.getString("passenger_name");
                    int passengerAge = rs.getInt("passenger_age");
                    int scheduledSeatId = rs.getInt("scheduled_seat_id");
                    int status_id = rs.getInt("status_id");

                    passenger.put("seatNumber", seatNumber);
                    passenger.put("passengerName", passengerName);
                    passenger.put("passengerAge", passengerAge);
                    passenger.put("scheduledSeatId", scheduledSeatId);
                    passenger.put("status_id", status_id);


                    passengerDetails.add(passenger);
                }
            } catch (SQLException sqlException) {
                System.out.println("Sql exception while fetching passenger details.");
                System.out.println(sqlException.getMessage());
            }

            return passengerDetails;

        }

    }


    /**
     * Handles the seat booking process.
     *
     * @param bookingSeatsDTO seat and passenger data
     * @param loggedInUserId ID of the authenticated user
     * @return HTTP response indicating success or failure
     */

    public Response bookSeat( BookingSeatsDTO bookingSeatsDTO, int loggedInUserId ) throws Exception {

        // Input parameter check
        if (bookingSeatsDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
        }

        // Check logged-in user and requested data user are the same.
        if (loggedInUserId != bookingSeatsDTO.getUserId()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("The logged in user is not allowed to perform this function.").build();
        }

        Connection conn = null;

        try {

            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Validate if the schedule is active first.
            if (!ScheduleService.validateScheduleIsActive(bookingSeatsDTO.getScheduleId(), conn)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Schedule is inactive or schedule not found.").build();
            }

            // adds a new booking to the bookings table and returns back the generated booking id;
            int bookingId = BookingsService.addNewBooking(conn, bookingSeatsDTO);

            // traversing through all the passenger details consumed from the client
            for (BookingSeatsDTO.PassengerDetailsDTO passengerDetail : bookingSeatsDTO.getPassengerDetails()) {


                // check if there is any mismatch in schedule ID from user and schedule ID of scheduledSeatId;
                Integer scheduleId = BookingsService.getScheduleIdFromScheduledSeatId(passengerDetail.getScheduledSeatId(), conn);
                if (scheduleId == null || !scheduleId.equals(bookingSeatsDTO.getScheduleId())) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Mismatch with schedule ID and scheduled seat ID.").build();
                }

                // checks if the seat is already booked or not
                if (BookingsService.checkSeatStatus(passengerDetail.getScheduledSeatId(), conn)) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Seat is not available.").build();
                }

                // add a new passenger in the passenger_details table and returns back the generated passenger id;
                int passengerId = BookingsService.addNewPassenger(conn, passengerDetail);

                // inserting into the booking_seats table to map with passenger and seats;
                BookingsService.insertBookingSeats(conn, bookingId, passengerId, passengerDetail);

                // changes the seat status in the seats table to booked (true)
                BookingsService.markSeatAsBooked(passengerDetail.getScheduledSeatId(), conn);
            }

            conn.commit();

            return Response.ok("Seats are booked.").build();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            // roll back connection
            DBConnection.rollbackConnection(conn);
            throw e;
        } finally {
            // roll back connection
            DBConnection.closeConnection(conn);

        }
    }


    /**
     * Cancels a booked seat for a user or admin.
     *
     * @param seat map containing `scheduledSeatId`
     * @param loggedInRoleId role ID of the user making the request
     * @param loggedInUserId ID of the authenticated user
     * @return HTTP response indicating success or error
     */

    public Response cancelTicket( Map<String, Integer> seat, int loggedInRoleId, int loggedInUserId ) throws Exception {

        // NULL parameter check.
        if (seat == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input.").build();
        }

        Integer scheduledSeatId = seat.get("scheduledSeatId");

        if (scheduledSeatId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input.").build();
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Check if the seat is already cancelled or not.
            if (!BookingsService.checkSeatStatus(scheduledSeatId, conn)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Already cancelled. Invalid request.").build();

            }

            // Check if the user authenticated and user of the seat owned are the same.
            if (!BookingsService.checkIfUserCanCancelSeat(scheduledSeatId, loggedInRoleId, loggedInUserId, conn)) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("This user cannot cancel this ticket.").build();
            }


            // Determine by whom the ticket is cancelled
            int bookingStatus = 0;
            if (loggedInRoleId != Role.USER.getId()) {
                bookingStatus = BookingStatusRegistry.getByCode(BookingStatus.CANCELLED_BY_ADMIN.name()).getStatusId();
            } else {
                bookingStatus = BookingStatusRegistry.getByCode(BookingStatus.CANCELLED_BY_USER.name()).getStatusId();
            }

            // BOOKING_SEATS -> The status is set to false which denotes the seat is cancelled for the booking ID.
            BookingsService.cancelBookedSeatStatus(scheduledSeatId, bookingStatus, conn);

            // SCHEDULED_SEATS -> Resets the seat status to false which denotes the scheduled seat is available again.
            BookingsService.markScheduledSeatAsAvailable(scheduledSeatId, conn);

            conn.commit();

            return Response.ok("Ticket cancelled.").build();

        } catch (BadRequestException badRequestException) {

            System.out.println(badRequestException.getMessage());
            DBConnection.rollbackConnection(conn);

            return Response.status(Response.Status.BAD_REQUEST).entity(badRequestException.getMessage()).build();
        }
        catch (SQLException e) {

            System.out.println(e.getMessage());
            DBConnection.rollbackConnection(conn);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error.").build();

        } catch (Exception e) {

            System.out.println(e.getMessage());
            DBConnection.rollbackConnection(conn);

            throw e;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}