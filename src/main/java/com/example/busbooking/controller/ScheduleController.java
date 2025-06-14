package com.example.busbooking.controller;

import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.bus.BusScheduleDAO;
import com.example.busbooking.dao.base.ScheduleDAO;
import com.example.busbooking.dto.base.ScheduleDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/schedule")
public class ScheduleController {

    private final ScheduleDAO scheduleDAO = new BusScheduleDAO();

    // this get endpoint produces all available schedule details.
    @GET
    @Path("/get-schedules")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({1,2,3})
    public Response getSchedules() throws Exception {
        List<ScheduleDTO> allSchedules = scheduleDAO.getSchedules();
        return Response.ok("Got all schedules").entity(allSchedules).build();
    }

    // this post endpoint adds a new schedule in the DB
    @POST
    @Path("/add-schedule")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({1,2})
    public Response addSchedule( ScheduleDTO scheduleDto) throws Exception {
        return scheduleDAO.addNewSchedule(scheduleDto);
    }


    // this endpoint produces details of a specific schedule (i.e. seats available, seat number, row, col etc.)
    @GET
    @Path("/{scheduleId}/details")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({1,2,3})
    public Response getScheduleDetails(@PathParam("scheduleId") int scheduleId) {
        return scheduleDAO.getScheduleDetails(scheduleId);
    }
}