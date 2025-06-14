package com.example.busbooking.controller;


import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.base.LocationDAO;
import com.example.busbooking.dao.bus.BusLocationDAO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/location")
public class LocationController {
    private final LocationDAO locationDAO = new BusLocationDAO();

    // based on the type given (boarding/dropping) it produces the available boarding/dropping points for the given schedule.
    @GET
    @Path("/get-schedule-locations/{id}/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({1,2,3})
    public Response getScheduleLocations(@PathParam("id") int ScheduleId, @PathParam("type") int type) throws Exception {
        // type = 1 boarding
        // type = 0 dropping
        return locationDAO.getScheduleLocations(ScheduleId, type);
    }
}