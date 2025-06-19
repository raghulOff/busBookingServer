package com.example.busbooking.controller;

import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.base.BookingsDAO;
import com.example.busbooking.dao.bus.BusBookingsDAO;
import com.example.busbooking.dto.base.BookSeatDTO;
import com.example.busbooking.model.Role;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;


@Path("/bookings")
public class BookController {
    private final BookingsDAO bookingsDAO = new BusBookingsDAO();


    // this stores the booking data with passenger details for one booking session.

    @POST
    @Path("/book-seats")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.USER})
    public Response bookSeats( BookSeatDTO bookSeatDTO ) throws Exception {

        return bookingsDAO.bookSeat(bookSeatDTO);
    }

    @GET
    @Path("/get-user-bookings/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER, Role.USER})
    public Response getUserBookings(@PathParam("id") int userId) throws Exception {
        return bookingsDAO.getAllBookings(userId);
    }


    @PUT
    @Path("/cancel-passenger-ticket")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.USER})
    public Response cancelPassengerTicket( Map<String, Integer> seat) {
        return bookingsDAO.cancelTicket(seat.get("seatId"));
    }


}