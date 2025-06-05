package com.example.auth.controller;

import com.example.auth.dao.ScheduleDAO;
import com.example.auth.dto.ScheduleDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/schedule")
public class ScheduleController {
    @GET
    @Path("/get-schedules")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSchedules() throws Exception {
        List<ScheduleDTO> allSchedules = ScheduleDAO.getSchedules();
        return Response.ok("Got all schedules").entity(allSchedules).build();
    }

    @POST
    @Path("/add-schedule")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSchedule( ScheduleDTO scheduleDto) throws Exception {
        return ScheduleDAO.addNewSchedule(scheduleDto);
    }
}
