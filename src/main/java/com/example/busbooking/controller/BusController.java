package com.example.busbooking.controller;

import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.BusDAO;
import com.example.busbooking.dto.BusDTO;
import com.example.busbooking.dto.BusSearchRequestDTO;
import com.example.busbooking.dto.BusSearchResponseDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/bus")
public class BusController {



    // this endpoint return buses with details based on the (from, to, doj) input values from user.
    @POST
    @Path("/get-buses")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({1,2,3})
    public Response getBuses( BusSearchRequestDTO busSearch ) throws Exception {
        List<BusSearchResponseDTO> busSearchList = BusDAO.getAvailableBuses(busSearch.getFrom(), busSearch.getTo(), busSearch.getDoj());
        return Response.ok(busSearchList).build();
    }



    // this endpoint produces all available buses.
    @GET
    @Path("/get-all-buses")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({1,2,3})
    public Response getAllBuses() throws Exception {
        List<BusDTO> allBuses = BusDAO.getAllBuses();
        return Response.ok("got all buses").entity(allBuses).build();
    }


    // deletes a specific bus
    @DELETE
    @Path("/delete-bus/{busId}")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({1,2})
    public Response deleteBus(@PathParam("busId") int busId) throws Exception {
            return BusDAO.deleteBus(busId);
    }


    // updates values of existing bus.
    @PUT
    @Path("/update-bus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({1,2})
    public Response updateBus(BusDTO busDTO) throws Exception {
        return BusDAO.updateBus(busDTO);
    }


    // adds a new bus
    @POST
    @Path("/add-bus")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({1,2})
    public Response addNewBus( BusDTO busDto ) throws Exception {
        return BusDAO.addNewBus(busDto);
    }

}
