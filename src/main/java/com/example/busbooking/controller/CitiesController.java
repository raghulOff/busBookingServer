package com.example.busbooking.controller;


import com.example.busbooking.annotation.RolesAllowedCustom;

import com.example.busbooking.dao.city.CitiesDAO;
import com.example.busbooking.dto.base.CitiesDTO;
import com.example.busbooking.model.Role;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;




/**
 * Controller for managing city-related operations such as listing cities,
 * adding new city, and deleting existing cities.

 * Access is controlled using {@link RolesAllowedCustom}, allowing certain roles to
 * access or modify the data.
 */

@Path("/city")
public class CitiesController {


    private final CitiesDAO citiesDAO = new CitiesDAO();

    /**
     * Fetches all cities available.
     *
     * @return a JSON array of cities.
     * @throws Exception if there’s a database or data access issue
     */
    @GET
    @Path("/get-cities")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER, Role.USER})
    public Response getCities() throws Exception {
        return citiesDAO.getCities();
    }

    /**
     * Deletes a city from DB with city ID.
     *
     * @param cityId the ID of the city to delete
     * @return a plain text confirmation or error message
     * @throws Exception if the city doesn't exist or deletion fails
     */
    @DELETE
    @Path("/delete-city/{cityId}")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response deleteCity( @PathParam("cityId") int cityId ) throws Exception {
        return citiesDAO.delete(cityId);
    }


    /**
     * Adds a new city.
     *
     * @param citiesDto contains details of the city to add
     * @return a plain text message indicating success or failure
     * @throws Exception if the city already exists
     */
    @POST
    @Path("/add-city")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response addCity( CitiesDTO citiesDto ) throws Exception {
        return citiesDAO.addNewCity(citiesDto);
    }
}