package com.example.busbooking.controller;

import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.bus.BusRouteDAO;
import com.example.busbooking.dao.base.RouteDAO;
import com.example.busbooking.dto.base.RouteDTO;
import com.example.busbooking.model.Role;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/route")
public class RouteController {

    RouteDAO routeDAO = new BusRouteDAO();
    // produces all available bus routes;
    @GET
    @Path("/get-routes")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER, Role.USER})
    public Response getSchedules() throws Exception {
        List<RouteDTO> allRoutes = routeDAO.getRoutes();
        return Response.ok("Got all routes").entity(allRoutes).build();
    }


    // adds a new route
    @POST
    @Path("/add-route")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response addRoute( RouteDTO routeDto) throws Exception {
        return routeDAO.addNewRoute(routeDto);
    }


    // this endpoint deletes a specific route.
    @DELETE
    @Path("/delete-route/{routeId}")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response deleteRoute(@PathParam("routeId") int routeId) throws Exception {
        return routeDAO.deleteRoute(routeId);
    }


    // this endpoint updates values of the existing route in DB.
    @PUT
    @Path("/update-route")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response updateRoute( RouteDTO routeDTO ) throws Exception {
        return routeDAO.updateRoute(routeDTO);
    }


}

