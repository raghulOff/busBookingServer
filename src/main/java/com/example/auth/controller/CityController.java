package com.example.auth.controller;


import com.example.auth.dao.CityDAO;
import com.example.auth.dto.CityDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/city")
public class CityController {
    @GET
    @Path("/get-cities")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCities() throws Exception {
        List<CityDTO> cities = CityDAO.getCities();
        return Response.ok("city is given").entity(cities).build();

    }

    @POST
    @Path("/add-city")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addCity( CityDTO cityDto) {
        return CityDAO.addNewCity(cityDto);
    }
}