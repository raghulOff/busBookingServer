package com.example.busbooking.controller;

import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.RouteDAO;
import com.example.busbooking.dto.RouteDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;

@Path("/route")
public class RouteController {

    // produces all available bus routes;
    @GET
    @Path("/get-routes")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({1,2,3})
    public Response getSchedules() throws Exception {
        List<RouteDTO> allRoutes = RouteDAO.getRoutes();
        return Response.ok("Got all routes").entity(allRoutes).build();
    }


    // adds a new route
    @POST
    @Path("/add-route")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({1,2})
    public Response addRoute( RouteDTO routeDto) throws SQLException {
        return RouteDAO.addNewRoute(routeDto);
    }


    // this endpoint deletes a specific route.
    @DELETE
    @Path("/delete-route/{routeId}")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({1,2})
    public Response deleteRoute(@PathParam("routeId") int routeId) throws Exception {
        return RouteDAO.deleteRoute(routeId);
    }


    // this endpoint updates values of the existing route in DB.
    @PUT
    @Path("/update-route")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({1,2})
    public Response updateRoute( RouteDTO routeDTO ) throws Exception {
        return RouteDAO.updateRoute(routeDTO);
    }


}

