package com.example.auth.controller;

import com.example.auth.dao.RouteDAO;
import com.example.auth.dto.RouteDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.model.Routed;

import java.sql.SQLException;
import java.util.List;

@Path("/route")
public class RouteController {
    @GET
    @Path("/get-routes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSchedules() throws Exception {
        List<RouteDTO> allRoutes = RouteDAO.getRoutes();
        return Response.ok("Got all routes").entity(allRoutes).build();
    }

    @POST
    @Path("/add-route")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRoute( RouteDTO routeDto) throws SQLException {
        return RouteDAO.addNewRoute(routeDto);
    }

    @DELETE
    @Path("/delete-route/{routeId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteRoute(@PathParam("routeId") int routeId) throws Exception {
        return RouteDAO.deleteRoute(routeId);
    }

    @PUT
    @Path("/update-route")
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateRoute( RouteDTO routeDTO ) throws Exception {
        return RouteDAO.updateRoute(routeDTO);
    }


}

