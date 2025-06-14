package com.example.busbooking.controller;

import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.bus.BusVehicleDAO;
import com.example.busbooking.dao.base.VehicleDAO;
import com.example.busbooking.dto.bus.BusSearchRequestDTO;
import com.example.busbooking.dto.bus.BusSearchResponseDTO;
import com.example.busbooking.dto.bus.BusVehicleDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/bus")
public class BusController {



    private VehicleDAO vehicleDAO = new BusVehicleDAO();

    // this endpoint return buses with details based on the (from, to, doj) input values from user.
    @POST
    @Path("/get-buses")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({1,2,3})
    public Response getBuses( BusSearchRequestDTO busSearch ) throws Exception {
        List<BusSearchResponseDTO> busSearchList = BusVehicleDAO.getAvailableBuses(busSearch.getFrom(), busSearch.getTo(), busSearch.getDoj());
        return Response.ok(busSearchList).build();
    }



    // this endpoint produces all available buses.
    @GET
    @Path("/get-all-buses")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({1,2,3})
    public Response getAllBuses() throws Exception {
        List<BusVehicleDTO> allBuses = vehicleDAO.getAll();
        return Response.ok("got all buses").entity(allBuses).build();
    }


    // deletes a specific bus
    @DELETE
    @Path("/delete-bus/{busId}")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({1,2})
    public Response deleteBus(@PathParam("busId") int busId) throws Exception {
            return vehicleDAO.delete(busId);
    }


    // updates values of existing bus.
    @PUT
    @Path("/update-bus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({1,2})
    public Response updateBus( BusVehicleDTO busDTO) throws Exception {
        return vehicleDAO.update(busDTO);
    }


    // adds a new bus
    @POST
    @Path("/add-bus")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({1,2})
    public Response addNewBus( BusVehicleDTO busVehicleDTO ) throws Exception {
        return vehicleDAO.addNew(busVehicleDTO);
    }

}
