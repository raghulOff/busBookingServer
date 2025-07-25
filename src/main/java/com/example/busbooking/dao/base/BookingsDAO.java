package com.example.busbooking.dao.base;

import com.example.busbooking.dto.base.BookingSeatsDTO;
import jakarta.ws.rs.core.Response;

import java.util.Map;

/**
 * BookingsDAO defines the contract for handling bookings across different vehicle types (bus, train, flight).
 * Any class implementing this interface should provide concrete implementations for booking, retrieving,
 * and cancelling tickets.
 */

public interface BookingsDAO {

    /**
     * Retrieves all bookings made by a particular user.
     *
     * @param userId           The ID of the user whose bookings are being requested.
     * @param loggedInUserId   The ID of the currently logged-in user (used for authorization).
     * @return A Response object containing a list of bookings or an appropriate error message.
     */
    Response getAllBookings( int userId, int loggedInUserId ) throws Exception;

    /**
     * Books seats for a specific schedule and user.
     *
     * @param bookingSeatsDTO   Contains booking information including schedule ID, seat details, and passengers.
     * @param loggedInUserId    The ID of the currently logged-in user (used for validation and ownership).
     * @return A Response indicating whether the booking was successful or failed.
     */
    Response bookSeat( BookingSeatsDTO bookingSeatsDTO, int loggedInUserId ) throws Exception;

    /**
     * Cancels a specific ticket (i.e., seat booking) based on booking/seat/passenger ID combination.
     *
     * @param seat              A map contains the scheduled seat ID
     * @param loggedInRoleId    The role ID of the user requesting cancellation (used to restrict access to admins/users).
     * @param loggedInUserId    The ID of the user making the cancellation request.
     * @return A Response indicating success or failure of cancellation.
     */
    Response cancelTicket( Map<String, Integer> seat, int loggedInRoleId, int loggedInUserId ) throws Exception;
}
