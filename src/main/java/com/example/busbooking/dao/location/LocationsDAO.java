package com.example.busbooking.dao.location;

import com.example.busbooking.db.DBConnection;
import com.example.busbooking.service.LocationService;
import com.example.busbooking.service.ScheduleService;
import jakarta.ws.rs.core.Response;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.busbooking.db.DBConstants.*;


/**
 * DAO class to manage location-related operations such as
 * retrieving schedule stop points, adding new locations, and
 * fetching city-wise location mappings.
 */
public class LocationsDAO {

    // SQL to get the BOARDING/DROPPING points of a particular schedule.
    private static final String get_schedule_board_drop_points_query = String.format("""
            select l.location_id as location_id, l.location_name as location_name
            from %s s\s
            join %s l on l.location_id = s.location_id
            where (s.schedule_id = ? and s.stop_type_id = ?)
            """, STOPS, LOCATIONS);




    // Query to return ID and name of locations associated with a city
    private static final String get_city_locations_query = String.format("""
            select l.location_id, l.location_name
            from %s cl
            join %s l on l.location_id = cl.location_id
            where cl.city_id = ?;
            """, CITY_LOCATIONS, LOCATIONS);



    /**
     * Retrieves boarding or dropping locations for a given schedule.
     *
     * @param scheduleId ID of the schedule
     * @param type 1 for boarding, 0 for dropping
     * @return Response containing list of locations or error message
     */

    public Response getScheduleLocations( int scheduleId, int type ) throws Exception {

        // type = 1 BOARDING
        // type = 0 DROPPING
        if (!(type == 1 || type == 0)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
        }


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(get_schedule_board_drop_points_query);) {

            String stop_type_code = (type > 0) ? "BOARDING" : "DROPPING";

            // Get the stop_type_id using stop_type_code
            Integer stop_type_id = ScheduleService.getStopTypeId(stop_type_code);


            // GET the location points based on the type.

            statement.setInt(1, scheduleId);
            statement.setInt(2, stop_type_id);

            List<Map<String, Object>> stopLocations;
            try (ResultSet rs = statement.executeQuery()) {

                // List to store all the requested location points.
                stopLocations = new ArrayList<>();


                while (rs.next()) {
                    // Map to store the location ID and location Name
                    Map<String, Object> stop = new HashMap<>();
                    stop.put("locationId", rs.getInt("location_id"));
                    stop.put("locationName", rs.getString("location_name"));

                    stopLocations.add(stop);
                }
            }

            return Response.ok(stopLocations).build();


        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error.").build();

        }
    }


    /**
     * Adds a new location and maps it to a city.
     *
     * @param locations Map containing cityId and locationName
     * @return Response indicating success or failure
     * @throws Exception if a rollback or connection issue occurs
     */

    public Response addNewLocation( Map<String, String> locations ) throws Exception {
        // Null check
        if (locations == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input.").build();
        }

        String cityIdStr = (locations.get("cityId"));
        String locationName = locations.get("locationName");

        // Check for valid parameters
        if (cityIdStr == null || locationName == null || cityIdStr.isEmpty() || locationName.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input.").build();
        }


        Integer cityId = null;
        // Check for valid parameters
        try {
            cityId = Integer.parseInt(cityIdStr);
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid cityId format.").build();
        }

        Connection conn = null;
        PreparedStatement locationStatement = null;
        PreparedStatement cityLocationStatement = null;


        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Add new location and return the generated Location ID
            Integer locationId = LocationService.insertLocationDetails(locationName, conn);

            if (locationId == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error.").build();
            }

            LocationService.mapLocationWithCity(cityId, locationId, conn);

            conn.commit();
        } catch (SQLException exception) {

            if (exception.getSQLState().equals(UNIQUE_VIOLATION)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Location already exist.").build();
            }
            DBConnection.rollbackConnection(conn);
            System.out.println(exception.getMessage());

            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input.").build();

        } catch (Exception e) {

            DBConnection.rollbackConnection(conn);
            throw e;

        } finally {
            DBConnection.closePreparedStatement(locationStatement);
            DBConnection.closePreparedStatement(cityLocationStatement);
            DBConnection.closeConnection(conn);

        }
        return Response.ok("Location added").build();
    }



    /**
     * Fetches all location IDs and names for a given city ID.
     *
     * @param cityId ID of the city
     * @return Response with list of location details or an error
     * @throws Exception if a connection or SQL error occurs
     */

    public Response getCityLocations( int cityId ) throws Exception {

        // List to store details of all the locations associated with a city.
        List<Map<String, Object>> locations = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             // GET the locations associated with a city.
            PreparedStatement locationStatement = conn.prepareStatement(get_city_locations_query);) {
            locationStatement.setInt(1, cityId);
            try (ResultSet rs = locationStatement.executeQuery()) {

                while (rs.next()) {
                    // Map to store the location ID and location Name.
                    Map<String, Object> location = new HashMap<>();
                    location.put("locationId", rs.getInt("location_id"));
                    location.put("locationName", rs.getString("location_name"));

                    locations.add(location);
                }
            }
        }

        return Response.ok("Locations retrieved.").entity(locations).build();
    }
}