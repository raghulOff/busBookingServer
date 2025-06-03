package com.example.auth.controller;


import com.example.auth.dao.CityListDAO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/city")
public class CityController {
    @GET
    @Path("/get-cities")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCities() throws Exception {
        Map<String, String> cities = CityListDAO.getCities();
        return Response.ok("city is given").entity(cities).build();
    }
}