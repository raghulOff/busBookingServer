package com.example.busbooking.controller;

import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.bus.BusVehiclesDAO;
import com.example.busbooking.dao.base.VehicleDAO;
import com.example.busbooking.dto.bus.BusSearchRequestDTO;
import com.example.busbooking.dto.bus.BusVehiclesDTO;
import com.example.busbooking.model.Role;
import com.example.busbooking.service.AddBusService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


/**
 * Controller for managing bus operations like adding, updating, deleting,
 * and fetching bus data.
 */

@Path("/bus")
public class BusController {



    private final VehicleDAO<BusVehiclesDTO> vehicleDAO = new BusVehiclesDAO();



    /**
     * Searches for available buses based on user input source, destination,
     * journey date.
     *
     * @param busSearch the search filter data (from, to, doj).
     * @return a list of matching available buses
     * @throws Exception if any database or service error occurs
     */
    @POST
    @Path("/get-buses")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER, Role.USER})
    public Response getBuses( BusSearchRequestDTO busSearch ) throws Exception {
        return BusVehiclesDAO.getAvailableBuses(busSearch);
    }



    /**
     * Retrieves all bus records from the system.
     *
     * @return a list of all buses stored in the database
     * @throws Exception if retrieval fails
     */
    @GET
    @Path("/get-all-buses")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response getAllBuses() throws Exception {
        return vehicleDAO.getAll();
    }


    /**
     * Deletes a bus based on the bus ID.
     *
     * @param busId the ID of the bus to be deleted
     * @return plain text message indicating success or failure
     * @throws Exception if deletion fails due to constraints or missing bus
     */
    @DELETE
    @Path("/delete-bus/{busId}")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response deleteBus(@PathParam("busId") int busId) throws Exception {
        return vehicleDAO.delete(busId);
    }


    /**
     * Updates the details of an existing bus.
     *
     * @param busDTO the bus details with updated values
     * @return plain text response indicating update status
     * @throws Exception if the bus does not exist or update fails
     */
    @PUT
    @Path("/update-bus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response updateBus( BusVehiclesDTO busDTO) throws Exception {
        return vehicleDAO.update(busDTO);
    }


    /**
     * Adds a new bus.
     *
     * @param busVehicleDTO contains all necessary information about the new bus
     * @return plain text response indicating success or failure
     * @throws Exception if insertion fails due to validation or DB error
     */
    @POST
    @Path("/add-bus")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response addNewBus( BusVehiclesDTO busVehicleDTO ) throws Exception {
        return vehicleDAO.addNew(busVehicleDTO);
    }



    /**
     * Retrieves the list of seat types (e.g., sleeper, semi-sleeper, seater).
     *
     * @return a JSON array of seat type options
     * @throws Exception if retrieval fails
     */
    @GET
    @Path("/seat-type")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN, Role.DEVELOPER})
    public Response getSeatTypes () throws Exception {
        return AddBusService.getSeatTypes();
    }




}
