package com.example.busbooking.controller;

import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.bus.BusSchedulesDAO;
import com.example.busbooking.dao.base.ScheduleDAO;
import com.example.busbooking.dto.base.SchedulesDTO;
import com.example.busbooking.model.Role;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Controller responsible for handling operations related to bus schedules.
 * Includes endpoints to create, read, update, and delete schedule entries.

 * Access is controlled using {@link RolesAllowedCustom}, allowing certain roles to
 * access or modify the data. */

@Path("/schedule")
public class SchedulesController {

    private final ScheduleDAO scheduleDAO = new BusSchedulesDAO();


    /**
     * Retrieves all available bus schedules.
     *
     * @return a JSON array of all schedules
     * @throws Exception if data retrieval fails
     */
    @GET
    @Path("/get-schedules")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response getSchedules() throws Exception {
        return scheduleDAO.getSchedules();
    }

    /**
     * Adds a new schedule to the DB
     *
     * @param schedulesDto the schedule details to be added
     * @return plain text response indicating success or failure
     * @throws Exception if validation fails or insertion is unsuccessful
     */
    @POST
    @Path("/add-schedule")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response addSchedule( SchedulesDTO schedulesDto ) throws Exception {
        return scheduleDAO.addNewSchedule(schedulesDto);
    }


    /**
     * Retrieves detailed information about a specific schedule only if the schedule is active.
     *
     * @param scheduleId the schedule ID of the schedule
     * @return a JSON object containing detailed schedule info
     */
    @GET
    @Path("/{scheduleId}/details")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER, Role.USER})
    public Response getScheduleDetails( @PathParam("scheduleId") int scheduleId ) throws Exception {
        return scheduleDAO.getScheduleDetails(scheduleId);
    }


    /**
     * Cancels a schedule by its ID.
     *
     * @param scheduleId the ID of the schedule to cancel
     * @return plain text message indicating the result
     */

    @PUT
    @Path("/{scheduleId}")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.ADMIN})
    public Response cancelSchedule( @PathParam("scheduleId") int scheduleId ) throws Exception {
        return scheduleDAO.cancelSchedule(scheduleId);
    }



    /**
     * Updates an existing schedule’s details
     *
     * @param schedulesDTO the updated schedule data
     * @return plain text status message
     * @throws Exception if the update fails
     */
    @PUT
    @Path("/update")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN})
    public Response updateSchedule( SchedulesDTO schedulesDTO ) throws Exception {
        return scheduleDAO.updateSchedule(schedulesDTO);
    }


}