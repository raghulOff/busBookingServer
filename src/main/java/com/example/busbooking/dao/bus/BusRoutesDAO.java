package com.example.busbooking.dao.bus;

import com.example.busbooking.dao.base.RouteDAO;
import com.example.busbooking.db.DBConnection;
import com.example.busbooking.dto.base.RoutesDTO;
import jakarta.ws.rs.core.Response;
import org.postgresql.util.PGInterval;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.busbooking.db.DBConstants.*;


/**
 * DAO implementation for handling route-related operations like
 * adding, retrieving, updating, and deleting routes.
 */
public class BusRoutesDAO implements RouteDAO {

    public static final String check_route_schedule_exist_query = String.format("""
            select schedule_id
            from %s where route_id = ?;
            """, SCHEDULES);
    // SQL to get all the available routes stores in the DB
    private static final String get_all_routes_query = String.format("""
            SELECT\s
                r.route_id,\
                c1.city_name AS source_city,
                c2.city_name AS destination_city,
                c1.city_id AS source_city_id,
                c2.city_id AS destination_city_id,
                r.distance_km,
                r.estimated_time
            FROM %s r
            JOIN %s c1 ON r.source_city_id = c1.city_id
            JOIN %s c2 ON r.destination_city_id = c2.city_id;
            """, ROUTES, CITIES, CITIES);
    // SQL to add a new route to DB
    private static final String add_route_query = String.format("""
            insert into %s (source_city_id, destination_city_id, distance_km, estimated_time)
                            values ((select city_id from %s where city_name = ?),
            \t\t\t\t(select city_id from %s where city_name = ?),
            \t\t\t\t?, ?)""", ROUTES, CITIES, CITIES);
    // Query to delete an existing route.
    private static final String delete_route_query = String.format("delete from %s where route_id=?", ROUTES);
    // Query to update an existing route.
    private static final String update_route_query = String.format("""
            UPDATE %s
            SET
                source_city_id = (SELECT city_id FROM %s WHERE city_name = ?),
                destination_city_id = (SELECT city_id FROM %s WHERE city_name = ?),
                distance_km = ?,
                estimated_time = ?
            WHERE route_id = ?;""", ROUTES, CITIES, CITIES);
    // Check if a route exists with the route ID
    private static final String check_route_exists_query = String.format("select route_id from %s where route_id = ?", ROUTES);


    /**
     * Retrieves all the routes available in the system.
     *
     * @return Response containing a list of all routes
     * @throws Exception if a DB error occurs
     */

    public Response getRoutes() throws Exception {

        // List of RoutesDTO to store all the routes data
        List<RoutesDTO> allRoutes = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(get_all_routes_query);
        ) {
            // GET all routes
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int route_id = rs.getInt("route_id");
                    String src = rs.getString("source_city");
                    String destination = rs.getString("destination_city");
                    int distance = rs.getInt("distance_km");
                    String est_time = rs.getString("estimated_time");
                    int sourceCityId = rs.getInt("source_city_id");
                    int destinationCityId = rs.getInt("destination_city_id");

                    allRoutes.add(new RoutesDTO(route_id, src, destination, distance, est_time, sourceCityId, destinationCityId));
                }
            }
        }
        return Response.ok("Got all routes").entity(allRoutes).build();
    }

    /**
     * Adds a new route to the database.
     *
     * @param routesDTO DTO containing route data
     * @return Response indicating creation status
     * @throws SQLException if the insert fails
     */
    public Response addNewRoute( RoutesDTO routesDTO ) throws Exception {

        // Check for valid parameters
        if (checkValidRouteDTOValues(routesDTO)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Input").build();
        }


        String source = routesDTO.getSource();
        String destination = routesDTO.getDestination();
        Integer distanceKm = routesDTO.getDistanceKm();
        String estimatedTime = routesDTO.getEstimatedTime();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(add_route_query);
        ) {

            statement.setString(1, source);
            statement.setString(2, destination);
            statement.setInt(3, distanceKm);
            statement.setObject(4, new PGInterval(estimatedTime));

            statement.executeUpdate();

        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("Can't create route").build();
        }
        return Response.status(Response.Status.CREATED).entity("Route added").build();
    }

    /**
     * Deletes an existing route from the database.
     *
     * @param routeId route ID to delete
     * @return Response indicating deletion status
     * @throws Exception if the route doesn't exist or deletion fails
     */

    public Response deleteRoute( int routeId ) throws Exception {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkRouteExistStatement = conn.prepareStatement(check_route_exists_query);
             PreparedStatement statement = conn.prepareStatement(delete_route_query);) {

            // Check if a route is assigned with a schedule. If so then the route cannot be deleted.
            if (isRouteAssignedToSchedule(conn, routeId)) {
                return Response.status(Response.Status.CONFLICT).entity("Cannot delete route. This route is assigned to an existing schedule.").build();
            }

            // Check if the route exists
            checkRouteExistStatement.setInt(1, routeId);
            ResultSet rs = checkRouteExistStatement.executeQuery();

            // Return NOT_FOUND if the route is not found.
            if (!rs.next()) {
                return Response.status(Response.Status.NOT_FOUND).entity("No route for this route_id exist.").build();
            }

            // DELETE the route.
            statement.setInt(1, routeId);
            statement.executeUpdate();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An internal error occurred while deleting the route.")
                    .build();
        }
        return Response.ok("Delete success").build();
    }

    /**
     * Updates details of an existing route.
     *
     * @param routesDTO DTO containing updated route data
     * @return Response indicating update status
     * @throws Exception if update fails
     */

    public Response updateRoute( RoutesDTO routesDTO ) throws Exception {

        // Check for valid parameters.
        if (checkValidRouteDTOValues(routesDTO)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Input").build();
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkRouteExistStatement = conn.prepareStatement(check_route_exists_query);
             PreparedStatement statement = conn.prepareStatement(update_route_query);) {

            // Check if a route is assigned with a schedule. If so then the route cannot be updated.
            if (isRouteAssignedToSchedule(conn, routesDTO.getRouteId())) {
                return Response.status(Response.Status.CONFLICT).entity("Cannot update route. This route is assigned to an existing schedule.").build();
            }

            // Check if the route exists.

            checkRouteExistStatement.setInt(1, routesDTO.getRouteId());

            ResultSet rs = checkRouteExistStatement.executeQuery();

            // Return if the route is not found.
            if (!rs.next()) {
                return Response.status(Response.Status.NOT_FOUND).entity("No route for this route_id exist.").build();
            }

            // UPDATE the route.

            statement.setString(1, routesDTO.getSource());
            statement.setString(2, routesDTO.getDestination());
            statement.setInt(3, routesDTO.getDistanceKm());
            statement.setObject(4, new PGInterval(routesDTO.getEstimatedTime()));
            statement.setInt(5, routesDTO.getRouteId());

            statement.executeUpdate();
        }
        return Response.ok("Update success").build();
    }


    /**
     * Validates if a route is assigned to a schedule
     *
     * @param conn    DB connection
     * @param routeId route ID to check if it is assigned with a schedule
     * @return true if route is assigned with a schedule else false
     * @throws Exception if any error
     */
    private boolean isRouteAssignedToSchedule( Connection conn, int routeId ) throws Exception {
        try (PreparedStatement checkRouteScheduleExistStatement = conn.prepareStatement(check_route_schedule_exist_query);) {
            checkRouteScheduleExistStatement.setInt(1, routeId);
            try (ResultSet routeScheduleRs = checkRouteScheduleExistStatement.executeQuery()) {
                return routeScheduleRs.next();
            }
        }
    }


    /**
     * Validates the route DTO for nulls, empty values, and logical errors.
     *
     * @param routesDTO DTO to validate
     * @return true if invalid; false otherwise
     */

    private boolean checkValidRouteDTOValues( RoutesDTO routesDTO ) {

        // NULL check
        if (routesDTO == null) {
            return true;
        }


        // RouteDTO VALIDATION
        String source = routesDTO.getSource();
        String destination = routesDTO.getDestination();
        Integer distanceKm = routesDTO.getDistanceKm();
        String estimatedTime = routesDTO.getEstimatedTime();

        return source == null || destination == null || estimatedTime == null || source.isEmpty() || destination.isEmpty() || estimatedTime.isEmpty() || distanceKm == null || distanceKm < 0 || source.equals(destination);
    }
}