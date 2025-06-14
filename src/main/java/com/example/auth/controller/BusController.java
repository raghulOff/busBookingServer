package com.example.auth.controller;

import com.example.auth.dao.BusDAO;
import com.example.auth.dto.BusDTO;
import com.example.auth.dto.BusSearchRequestDTO;
import com.example.auth.dto.BusSearchResponseDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/bus")
public class BusController {
    @POST
    @Path("/get-buses")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuses( BusSearchRequestDTO busSearch ) throws Exception {
        List<BusSearchResponseDTO> busSearchList = BusDAO.getAvailableBuses(busSearch.getFrom(), busSearch.getTo(), busSearch.getDoj());
        return Response.ok(busSearchList).build();
    }
    @GET
    @Path("/get-all-buses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBuses() throws Exception {
        List<BusDTO> allBuses = BusDAO.getAllBuses();
        return Response.ok("got all buses").entity(allBuses).build();
    }

    @DELETE
    @Path("/delete-bus/{busId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteBus(@PathParam("busId") int busId) throws Exception {
            return BusDAO.deleteBus(busId);
    }

    @PUT
    @Path("/update-bus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateBus(BusDTO busDTO) throws Exception {
        return BusDAO.updateBus(busDTO);
    }

    @POST
    @Path("/add-bus")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewBus( BusDTO busDto ) throws Exception {
        return BusDAO.addNewBus(busDto);
    }

}
