package com.example.busbooking.controller;

import com.example.busbooking.annotation.PermissionsAllowed;
import com.example.busbooking.dao.base.BookingsDAO;
import com.example.busbooking.dao.bus.BusBookingsDAO;
import com.example.busbooking.dto.base.BookingSeatsDTO;
import com.example.busbooking.enums.Permission;
import com.example.busbooking.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;


/**
 * Controller responsible for managing seat bookings,
 * retrieving booking history, and canceling tickets.

 * All endpoints are secured using {@link PermissionsAllowed} based on user roles.
 */
@Path("/bookings")
public class BookingsController {
    private final BookingsDAO bookingsDAO = new BusBookingsDAO();

    @Context
    HttpServletRequest request;

    /**
     * Books seats only for the logged-in user. Each booking includes passenger details.
     * Only users with the USER role are allowed to perform this operation.
     *
     * @param bookingSeatsDTO details of the booking.
     * @return HTTP 200 with a success message or HTTP 400/500 for failure.
     * @throws Exception if an internal error occurs during booking
     */
    @POST
    @Path("/book-seats")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @PermissionsAllowed({Permission.BOOK_SEATS})

    public Response bookSeats(@Valid BookingSeatsDTO bookingSeatsDTO ) throws Exception {
        // only the logged-in user is allowed to book seat for their own.
        int loggedInUserId = (int) request.getAttribute("userId");
        return bookingsDAO.bookSeat(bookingSeatsDTO, loggedInUserId);
    }


    /**
     * Retrieves all bookings made by a specific user.
     * Accessible by the user or by ADMIN/DEVELOPER roles.
     *
     * @param userId the ID of the user whose bookings are to be fetched
     * @return a JSON array of booking records
     * @throws Exception if the user is unauthorized or an internal error occurs
     */
    @GET
    @Path("/get-user-bookings/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermissionsAllowed({Permission.GET_USER_BOOKING_HISTORY})

    public Response getUserBookings( @PathParam("userId") int userId ) throws Exception {
        // Enforce that the logged-in user can only access their own data,
        // unless they are an admin or developer
        int loggedInUserId = (int) request.getAttribute("userId");
        return bookingsDAO.getAllBookings(userId, loggedInUserId);
    }


    /**
     * Cancels a booked ticket for a specific passenger seat.
     * Can be accessed by the passenger or by admin/developer roles.
     *
     * @param seat a map containing passenger ticket details
     * @return HTTP 200 with success message if cancellation succeeds, otherwise an error
     */
    @PUT
    @Path("/cancel-passenger-ticket")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @PermissionsAllowed({Permission.CANCEL_PASSENGER_TICKET})

    public Response cancelPassengerTicket( Map<String, Integer> seat ) throws Exception {

        int loggedInUserId = (int) request.getAttribute("userId");
        int loggedInRoleId = (int) request.getAttribute("roleId");

        return bookingsDAO.cancelTicket(seat, loggedInRoleId, loggedInUserId);
    }


}