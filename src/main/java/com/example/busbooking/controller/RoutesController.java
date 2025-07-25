package com.example.busbooking.controller;

import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.bus.BusRoutesDAO;
import com.example.busbooking.dao.base.RouteDAO;
import com.example.busbooking.dto.base.RoutesDTO;
import com.example.busbooking.model.Role;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;




/**
 * Controller responsible for managing bus routes.
 * Handles creation, retrieval, update, and deletion of routes.
 */
@Path("/route")
public class RoutesController {

    RouteDAO routeDAO = new BusRoutesDAO();


    /**
     * Retrieves all available bus routes.
     *
     * @return JSON array of route objects
     * @throws Exception if data retrieval fails
     */
    @GET
    @Path("/get-routes")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response getRoutes() throws Exception {
        return routeDAO.getRoutes();
    }


    /**
     * Adds a new route.
     *
     * @param routesDto route details to be saved
     * @return plain text message indicating success or failure
     * @throws Exception if the route already exists or validation fails
     */
    @POST
    @Path("/add-route")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response addRoute( RoutesDTO routesDto ) throws Exception {
        return routeDAO.addNewRoute(routesDto);
    }


    /**
     * Deletes an existing route by its route ID.
     *
     * @param routeId the route ID of the route to be deleted
     * @return plain text confirmation of deletion
     * @throws Exception if the route cannot be found or is in use
     */
    @DELETE
    @Path("/delete-route/{routeId}")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response deleteRoute( @PathParam("routeId") int routeId ) throws Exception {
        return routeDAO.deleteRoute(routeId);
    }


    /**
     * Updates an existing route's details.
     *
     * @param routesDTO the updated route information
     * @return plain text response indicating update status
     * @throws Exception if the route does not exist or update fails
     */
    @PUT
    @Path("/update-route")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response updateRoute( RoutesDTO routesDTO ) throws Exception {
        return routeDAO.updateRoute(routesDTO);
    }
}
