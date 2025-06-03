package com.example.auth.controller;

import com.example.auth.dao.BusListDAO;
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
        List<BusSearchResponseDTO> busSearchList = BusListDAO.getAvailableBuses(busSearch.getFrom(), busSearch.getTo(), busSearch.getDoj());
        return Response.ok(busSearchList).build();
    }
    @GET
    @Path("/get-all-buses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBuses() throws Exception {
        List<BusDTO> allbuses = BusListDAO.getAllBuses();
        return Response.ok("got all buses").entity(allbuses).build();
    }
}
