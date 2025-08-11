package com.example.busbooking.service;

import com.example.busbooking.dto.base.BookingSeatsDTO;
import com.example.busbooking.enums.BookingStatus;
import com.example.busbooking.enums.Role;
import com.example.busbooking.enums.ScheduledSeatStatus;
import com.example.busbooking.registry.BookingStatusRegistry;
import com.example.busbooking.registry.ScheduledSeatStatusRegistry;
import jakarta.ws.rs.BadRequestException;

import java.sql.*;

import static com.example.busbooking.db.DBConstants.*;

/**
 * BookSeatService handles seat booking operations including:
 * - Creating a new booking record
 * - Adding passenger details
 * - Assigning booked seats to passengers
 * - Verifying seat availability
 */

public class BookingsService {
    // QUERY TO CHECK IF THE SEAT IS BOOKED OR NOT
    private static final String check_seat_status_query = String.format("""
            select status_id from %s where scheduled_seat_id = ?;
            """, SCHEDULED_SEATS);
    // query to insert a new booking in bookings table
    private static final String booking_insert_query = String.format("insert into %s\n" +
            "(user_id, schedule_id, total_amount, boarding_point_id, dropping_point_id)\n" +
            "values (?, ?, ?, ?, ?);", BOOKINGS);

    // inserts the passenger data into passenger_details table
    private static final String passenger_insert_query = String.format("insert into %s (passenger_name, passenger_age)\n" +
            "values (?, ?);", PASSENGER_DETAILS);

    // this inserts and maps every passenger with seat id and booking id;
    private static final String booking_seats_insert_query = String.format("insert into %s (booking_id, scheduled_seat_id, passenger_id, status_id)\n" +
            "values (?, ?, ?, ?);", BOOKING_SEATS);

    // checks if a seat is available or not
    private static final String get_schedule_id_from_scheduledSeatId_query = String.format(
            "select schedule_id from %s where scheduled_seat_id = ?", SCHEDULED_SEATS);

    // updates a seat from available to booked (where status is set to true);
    private static final String seat_update_query = String.format("update %s set status_id = ? where scheduled_seat_id = ?;",
            SCHEDULED_SEATS);


    // query to get the user id who booked a scheduled seat
    private static final String get_seat_user_query = String.format("""
            select b.user_id
            from %s bs
            join %s b on b.booking_id = bs.booking_id
            where bs.scheduled_seat_id = ? and bs.status_id = 1;
            """, BOOKING_SEATS, BOOKINGS);

    // query to make the seat available again in the seats table.
    private static final String update_seats_status_user_id_query = String.format("update %s set status_id = ? where scheduled_seat_id = ?", SCHEDULED_SEATS);

    // query to cancel the seat of a booked id in the booking_seats table (where status is set to false)
    private static final String update_booking_seats_status_query = String.format("update %s set status_id = ? where scheduled_seat_id = ?",
            BOOKING_SEATS);


    /**
     * Adds a new booking entry into the BOOKINGS table.
     *
     * @param conn            Database connection (external transaction control)
     * @param bookingSeatsDTO Booking data containing user ID, schedule, amount, etc.
     * @return bookingId      The newly generated booking ID
     * @throws SQLException If SQL operation fails
     */

    public static int addNewBooking( Connection conn, BookingSeatsDTO bookingSeatsDTO ) throws SQLException {
        int bookingId = 0;

        // Add new booking into BOOKINGS table and return the generated booking ID.
        try (PreparedStatement book_statement = conn.prepareStatement(booking_insert_query, Statement.RETURN_GENERATED_KEYS)) {

            book_statement.setInt(1, bookingSeatsDTO.getUserId());
            book_statement.setInt(2, bookingSeatsDTO.getScheduleId());
            book_statement.setInt(3, bookingSeatsDTO.getPayableAmount());
            book_statement.setInt(4, bookingSeatsDTO.getBoardingPointId());
            book_statement.setInt(5, bookingSeatsDTO.getDroppingPointId());

            book_statement.executeUpdate();

            try (ResultSet rs = book_statement.getGeneratedKeys()) {
                if (rs.next()) {
                    bookingId = rs.getInt(1);
                }
            }
        }

        return bookingId;
    }


    /**
     * Inserts a passenger into the PASSENGER_DETAILS table.
     *
     * @param conn            Database connection
     * @param passengerDetail Passenger info (name, age)
     * @return passengerId    Newly generated passenger ID
     * @throws SQLException If SQL insert fails
     */

    public static int addNewPassenger( Connection conn, BookingSeatsDTO.PassengerDetailsDTO passengerDetail ) throws Exception {
        int passengerId = 0;

        // Add new passenger and return the generated passenger ID
        try (PreparedStatement passengerStatement = conn.prepareStatement(passenger_insert_query, Statement.RETURN_GENERATED_KEYS)) {
            passengerStatement.setString(1, passengerDetail.getPassengerName());
            passengerStatement.setInt(2, passengerDetail.getPassengerAge());

            passengerStatement.executeUpdate();

            try (ResultSet rsPassenger = passengerStatement.getGeneratedKeys()) {
                if (rsPassenger.next()) {
                    passengerId = rsPassenger.getInt(1);
                }
            }
        }

        return passengerId;
    }


    /**
     * Maps a passenger, seat, and booking using the BOOKING_SEATS table.
     *
     * @param conn            Database connection
     * @param bookingId       ID of the booking
     * @param passengerId     ID of the passenger
     * @param passengerDetail DTO containing scheduledSeatId
     * @throws SQLException If SQL insert fails
     */

    public static void insertBookingSeats( Connection conn, int bookingId, int passengerId, BookingSeatsDTO.PassengerDetailsDTO passengerDetail ) throws Exception {

        // Inserting booking ID, scheduled seat ID, passenger ID into BOOKING_SEATS table.
        try (PreparedStatement seatsStatement = conn.prepareStatement(booking_seats_insert_query)) {
            seatsStatement.setInt(1, bookingId);
            seatsStatement.setInt(2, passengerDetail.getScheduledSeatId());
            seatsStatement.setInt(3, passengerId);
            seatsStatement.setInt(4, BookingStatusRegistry.getByCode(BookingStatus.BOOKED.name()).getStatusId()); // 1 refers to BOOKED state in the BOOKING_STATUSES table.

            seatsStatement.executeUpdate();
        }
    }


    /**
     * Retrieve the schedule ID from the scheduled seat ID
     *
     * @param scheduledSeatId scheduled seat ID
     * @param conn            DB Connection
     * @return schedule ID
     * @throws Exception if any error
     */
    public static Integer getScheduleIdFromScheduledSeatId( int scheduledSeatId, Connection conn ) throws Exception {
        Integer schedule_id = null;
        // To get schedule_id from scheduled_seat_id

        try (PreparedStatement getScheduleIdStatement = conn.prepareStatement(get_schedule_id_from_scheduledSeatId_query)) {

            getScheduleIdStatement.setInt(1, scheduledSeatId);

            try (ResultSet scheduleIdRs = getScheduleIdStatement.executeQuery()) {

                if (scheduleIdRs.next()) {
                    schedule_id = scheduleIdRs.getInt("schedule_id");
                }
            }
        }
        return schedule_id;

    }


    /**
     * Checks whether a scheduled seat is available for the given schedule.
     * (i.e. Checks if the scheduled seat is already booked or blocked (status_id != 1))
     * (1 -> AVAILABLE, 2 -> BOOKED, 3 -> BLOCKED)
     *
     * @param scheduledSeatId Seat ID to validate
     * @param conn            DB connection
     * @return true if the seat is not available (i.e. BOOKED / BLOCKED) or false if the seat is available
     * @throws Exception If any error.
     */

    public static boolean checkSeatStatus( int scheduledSeatId, Connection conn ) throws Exception {

        // To check seat status
        try (PreparedStatement statement = conn.prepareStatement(check_seat_status_query)) {

            statement.setInt(1, scheduledSeatId);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    int status_id = rs.getInt("status_id");
                    if (status_id == ScheduledSeatStatusRegistry.getByCode(ScheduledSeatStatus.AVAILABLE.name()).getStatusId()) {
                        return false;
                    } else return true;
                }
            }
        }

        // Default return if no rows found
        return true;
    }


    /**
     * Change the status of the scheduled seat to booked
     *
     * @param scheduledSeatId scheduled seat ID
     * @param conn            DB Connection
     * @throws Exception if any error
     */
    public static void markSeatAsBooked( int scheduledSeatId, Connection conn ) throws Exception {
        try (PreparedStatement updateSeatStatement = conn.prepareStatement(seat_update_query);) {
            updateSeatStatement.setInt(1, ScheduledSeatStatusRegistry.getByCode(ScheduledSeatStatus.BOOKED.name()).getStatusId()); // 2 refers to booked state.
            updateSeatStatement.setInt(2, scheduledSeatId);
            updateSeatStatement.executeUpdate();
        }
    }


    /**
     * Validates if
     * -> The cancellation is initiated by ADMIN, then the seat is cancelled without restriction.
     * -> The cancellation is by user, then the authenticated user and the user holding the seat ticket is verified.
     *
     * @param scheduledSeatId scheduled seat ID
     * @param loggedInRoleId  role ID of the logged-in user
     * @param loggedInUserId  user ID of the logged-in user
     * @param conn            DB Connection
     * @return true if the user can cancel the seat, else false
     * @throws Exception if any error
     */

    public static boolean checkIfUserCanCancelSeat( int scheduledSeatId, int loggedInRoleId, int loggedInUserId, Connection conn ) throws Exception {
        try (PreparedStatement seatUserStatement = conn.prepareStatement(get_seat_user_query)) {
            seatUserStatement.setInt(1, scheduledSeatId);
            try (ResultSet rs = seatUserStatement.executeQuery()) {

                if (rs.next()) {
                    // If the logged-in user role is USER then user booked the seat and the user logged in using user ID.
                    if (loggedInRoleId == Role.USER.getId()) {
                        int userId = Integer.parseInt(rs.getString("user_id"));
                        if (userId == loggedInUserId) {
                            return true;
                        }
                    } else return true; // For roles other than USER, cancellation is allowed since only authorized roles can access this endpoint via annotations.

                } else {
                    throw new BadRequestException("Scheduled seat is blocked or scheduled seat doesn't exist.");
                }
            }
        }
        return false;
    }


    /**
     * Change the status of the scheduled seat to false, which reverts the seat to be available again.
     *
     * @param scheduledSeatId scheduled seat ID
     * @param conn            DB Connection
     * @throws Exception if any error occurs
     */
    public static void markScheduledSeatAsAvailable( int scheduledSeatId, Connection conn ) throws Exception {
        try (PreparedStatement statement = conn.prepareStatement(update_seats_status_user_id_query)) {
            statement.setInt(1, ScheduledSeatStatusRegistry.getByCode(ScheduledSeatStatus.AVAILABLE.name()).getStatusId());
            statement.setInt(2, scheduledSeatId);
            statement.executeUpdate();
        }
    }


    /**
     * Cancel the ticket of the seat booked by user in the booking_seats table.
     * The cancellation can be done by USER, ADMIN etc.
     *
     * @param scheduledSeatId The scheduled seat ID
     * @param bookingStatus   The status of the booking (eg. 1 BOOKED, 2 CANCELLED BY USER, 3 CANCELLED BY ADMIN)
     * @param conn            DB Connection
     * @throws Exception if any error
     */

    public static void cancelBookedSeatStatus( int scheduledSeatId, int bookingStatus, Connection conn ) throws Exception {
        try (PreparedStatement statement = conn.prepareStatement(update_booking_seats_status_query)) {
            statement.setInt(1, bookingStatus);
            statement.setInt(2, scheduledSeatId);
            statement.executeUpdate();
        }
    }

}
