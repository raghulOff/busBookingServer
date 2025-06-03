package com.example.auth.controller;

import com.example.auth.dao.ManagementDAO;
import com.example.auth.dto.BusDTO;
import com.example.auth.dto.CityDTO;
import com.example.auth.dto.RouteDTO;
import com.example.auth.dto.ScheduleDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;

@Path("/manage")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ManagementController {
    @POST
    @Path("/add-bus")
    public Response addNewBus( BusDTO busDto ) throws Exception {
        return ManagementDAO.addNewBus(busDto);
    }

    @POST
    @Path("/add-schedule")
    public Response addSchedule( ScheduleDTO scheduleDto) throws Exception {
        return ManagementDAO.addNewSchedule(scheduleDto);
    }

    @POST
    @Path("/add-route")
    public Response addRoute( RouteDTO routeDto) throws SQLException {
        return ManagementDAO.addNewRoute(routeDto);
    }

    @POST
    @Path("/add-city")
    public Response addCity( CityDTO cityDto) {
        return ManagementDAO.addNewCity(cityDto);
    }

}