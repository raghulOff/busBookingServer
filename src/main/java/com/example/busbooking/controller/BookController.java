package com.example.busbooking.controller;

import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.BookSeatDAO;
import com.example.busbooking.dao.ScheduleDAO;
import com.example.busbooking.dto.BookSeatDTO;
import com.example.busbooking.dto.ScheduleDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/book")
public class BookController {



    // this stores the booking data with passenger details for one booking session.

    @POST
    @Path("/book-seats")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({3})
    public Response bookSeats( BookSeatDTO bookSeatDTO ) throws Exception {

        return BookSeatDAO.bookSeat(bookSeatDTO);
    }
}
