package com.example.busbooking.dao.base;

import com.example.busbooking.dto.base.RoutesDTO;
import jakarta.ws.rs.core.Response;


/**
 * Interface for defining CRUD operations related to route management.
 * This abstraction allows various implementations (e.g., BusRouteDAO, TrainRouteDAO)
 * to interact with routes such as fetching, creating, updating, and deleting them.
 */

public interface RouteDAO {

    /**
     * Retrieves all the available routes from the database.
     *
     * @return A Response object containing the list of routes or an error status.
     * @throws Exception If a database or internal server error occurs.
     */
    Response getRoutes() throws Exception;

    /**
     * Adds a new route to the DB.
     *
     * @param routesDTO A DTO containing route details such as source, destination, distance, and estimated time.
     * @return A Response indicating whether the route was successfully added.
     * @throws Exception If an error occurs during insertion or validation.
     */
    Response addNewRoute( RoutesDTO routesDTO ) throws Exception;

    /**
     * Deletes an existing route by its route ID.
     *
     * @param routeId The unique ID of the route to be deleted.
     * @return A Response indicating whether the deletion was successful or failed.
     * @throws Exception If deletion fails due to constraint violations or invalid ID.
     */
    Response deleteRoute( int routeId ) throws Exception;


    /**
     * Updates an existing route with new details.
     *
     * @param routesDTO A DTO containing updated route information.
     * @return A Response indicating the success or failure of the update.
     * @throws Exception If the route doesn't exist or an internal error occurs.
     */
    Response updateRoute( RoutesDTO routesDTO ) throws Exception;
}
