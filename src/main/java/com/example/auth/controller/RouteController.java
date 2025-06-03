package com.example.auth.controller;

import com.example.auth.dao.RouteListDAO;
import com.example.auth.dto.RouteDTO;
import com.example.auth.dto.ScheduleDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/route")
public class RouteController {
    @GET
    @Path("/get-routes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSchedules() throws Exception {
        List<RouteDTO> allRoutes = RouteListDAO.getRoutes();
        return Response.ok("Got all routes").entity(allRoutes).build();
    }
}

