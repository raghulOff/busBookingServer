package com.example.busbooking.dao.base;

import com.example.busbooking.dto.base.VehiclesDTO;
import jakarta.ws.rs.core.Response;


/**
 * Generic DAO interface for vehicle-related operations.

 * This interface abstracts the basic CRUD operations that can be performed
 * on various types of vehicles (e.g., buses, trains, flights) by accepting
 * a generic DTO type that extends {@link VehiclesDTO}.
 *
 * @param <T> A type that extends VehiclesDTO, representing the structure of vehicle data.
 */

public interface VehicleDAO<T extends VehiclesDTO> {


    /**
     * Retrieves all vehicles available in the system.
     *
     * @return A Response containing a list of all vehicles, or an appropriate error response.
     * @throws Exception If there is a database or internal error.
     */
    Response getAll() throws Exception;

    /**
     * Deletes a vehicle based on the provided vehicle ID.
     *
     * @param id The ID of the vehicle to be deleted.
     * @return A Response indicating success or failure of the delete operation.
     * @throws Exception If deletion fails due to foreign key constraints or invalid ID.
     */
    Response delete(int id) throws Exception;

    /**
     * Updates an existing vehicle with new details.
     *
     * @param vehicleDTO The vehicle DTO containing updated vehicle information.
     * @return A Response indicating the outcome of the update operation.
     * @throws Exception If update fails due to validation or database error.
     */
    Response update( T vehicleDTO ) throws Exception;

    /**
     * Adds a new vehicle to the system.
     *
     * @param vehicleDTO The DTO containing information about the new vehicle.
     * @return A Response indicating whether the insertion was successful or not.
     * @throws Exception If insertion fails due to invalid data or database issues.
     */
    Response addNew( T vehicleDTO ) throws Exception;
}
