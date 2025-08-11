package com.example.busbooking.controller;


import com.example.busbooking.annotation.PermissionsAllowed;
import com.example.busbooking.dao.location.LocationsDAO;
import com.example.busbooking.enums.Permission;
import com.example.busbooking.enums.Role;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;


/**
 * Controller for handling location-related operations such as
 * fetching boarding/dropping points and adding new locations.

 * This is used during scheduling and booking flows where cities or
 * schedules require associated boarding/dropping points.
 */

@Path("/location")
public class LocationsController {
    private final LocationsDAO locationsDAO = new LocationsDAO();

    /**
     * Retrieves the list of boarding or dropping points associated with a given schedule.
     * The type (boarding/dropping) is specified via `boardDropType`:
     *   1 -> boarding points
     *   0 -> dropping points
     *
     * @param scheduleId ID of the bus schedule
     * @param boardDropType 1 for boarding points, 0 for dropping points
     * @return a list of location details in JSON format
     * @throws Exception if the schedule ID is invalid or DB access fails
     */
    @GET
    @Path("/get-schedule-locations/{scheduleId}/{board-drop-type}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermissionsAllowed({Permission.GET_SCHEDULE_STOP_POINTS})

    public Response getScheduleLocations(@PathParam("scheduleId") int scheduleId, @PathParam("board-drop-type") int boardDropType) throws Exception {
        return locationsDAO.getScheduleLocations(scheduleId, boardDropType);
    }




    /**
     * Retrieves all locations (boarding or dropping) associated with a specific city.
     *
     * @param cityId ID of the city whose locations are to be fetched
     * @return JSON array of locations belonging to the city
     * @throws Exception if the city does not exist or fetch fails
     */
    @GET
    @Path("/{cityId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermissionsAllowed({Permission.GET_CITY_LOCATIONS})

    public Response getCityLocations(@PathParam("cityId") int cityId) throws Exception {
        return locationsDAO.getCityLocations(cityId);
    }



    /**
     * Adds a new boarding or dropping point for a specific city or schedule.
     * Only admins are allowed to insert new locations.
     *
     * @param location a map containing location name, city ID, type (boarding/dropping), etc.
     * @return plain text response indicating success or failure
     * @throws Exception if validation fails or the insert operation fails
     */
    @POST
    @Path("/add-location")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @PermissionsAllowed({Permission.ADD_NEW_LOCATION})

    public Response addNewLocation( Map<String, String> location ) throws Exception {
        return locationsDAO.addNewLocation(location);
    }
}