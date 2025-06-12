package com.example.busbooking.controller;


import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.CityDAO;
import com.example.busbooking.dto.CityDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/city")
public class CityController {

    // returns all available cities
    @GET
    @Path("/get-cities")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({1,2,3})
    public Response getCities() throws Exception {
        List<CityDTO> cities = CityDAO.getCities();
        return Response.ok("city is given").entity(cities).build();

    }

    // adds a new city
    @POST
    @Path("/add-city")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({1,2})
    public Response addCity( CityDTO cityDto) {
        return CityDAO.addNewCity(cityDto);
    }
}