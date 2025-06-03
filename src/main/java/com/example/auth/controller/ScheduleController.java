package com.example.auth.controller;

import com.example.auth.dao.ScheduleListDAO;
import com.example.auth.dto.ScheduleDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/schedule")
public class ScheduleController {
    @GET
    @Path("/get-schedules")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSchedules() throws Exception {
        List<ScheduleDTO> allSchedules = ScheduleListDAO.getSchedules();
        return Response.ok("Got all schedules").entity(allSchedules).build();
    }
}
