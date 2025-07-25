package com.example.busbooking.service;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.example.busbooking.db.DBConstants.*;

public class CitiesService {

    // SQL to delete an existing city
    public static final String delete_city_query = String.format("delete from %s where city_id = ?", CITIES);
    // SQL to check if a city exists.
    private static final String check_city_exists_query = String.format("select city_id from %s where city_id = ?", CITIES);
    // SQL to check if a city is linked to a route.
    private static final String check_city_linked_to_route_query = String.format("""
            select * from %s where source_city_id = ? or destination_city_id = ?
            limit 1;
            """, ROUTES);
    // SQL delete a city linked location from city_locations table
    private static final String delete_city_location_query = String.format("""
            delete from %s where city_id = ?;
            """, CITY_LOCATIONS);


    /**
     * Function to check with the city ID if a city exists or not.
     * @param cityId ID of the city
     * @param conn DB connection
     * @return true if the city exists, if not return false
     * @throws Exception if any error
     */

    public static boolean isCityExists( int cityId, Connection conn ) throws Exception {

        try (PreparedStatement checkCityExistStatement = conn.prepareStatement(check_city_exists_query);) {

            checkCityExistStatement.setInt(1, cityId);
            try (ResultSet rs = checkCityExistStatement.executeQuery()) {
                // Return false if the city is not found.
                if (!rs.next()) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Checks whether the city with the given city ID is assigned to any route
     * (either as a source or destination).
     * Deleting a city that is linked to a route will cause a foreign key violation.
     * @param cityId ID of the city
     * @param conn DB Connection
     * @return true if the city is linked to a route, else return false
     * @throws Exception throws exception if any error.
     */

    public static boolean isCityAssignedWithRoute( int cityId, Connection conn ) throws Exception {
        try (PreparedStatement checkCityLinkedToRouteStatement = conn.prepareStatement(check_city_linked_to_route_query)) {
            checkCityLinkedToRouteStatement.setInt(1, cityId);
            checkCityLinkedToRouteStatement.setInt(2, cityId);
            try (ResultSet cityLinkedRouteRs = checkCityLinkedToRouteStatement.executeQuery()) {

                // If city is assigned with a route then return true;
                if (cityLinkedRouteRs.next()) {
                    return true;
                }
            }
        }
        return false;
    }


    private static final String delete_locations_associated_with_city = String.format("""
            DELETE FROM %s
            WHERE location_id IN (
                SELECT location_id
                FROM %s
                WHERE city_id = ?
            );
            """, LOCATIONS, CITY_LOCATIONS);

    /**
     * Two operations:
     * Delete the mapping of a city <-> location from city_locations table.
     * Delete the locations linked with the city.
     *
     * @param cityId ID of the city
     * @param conn DB Connection
     * @throws Exception throws exception if any error.
     */
    public static void deleteCityLinkedWithLocationByCityId( int cityId, Connection conn ) throws Exception {
        try (PreparedStatement deleteCityLocationStatement = conn.prepareStatement(delete_city_location_query);
             PreparedStatement deleteLocationStatement = conn.prepareStatement(delete_locations_associated_with_city);) {

            // deletes the locations linked with the city
            deleteLocationStatement.setInt(1, cityId);
            deleteLocationStatement.executeUpdate();


            // deletes the mapped city <-> location
            deleteCityLocationStatement.setInt(1, cityId);
            deleteCityLocationStatement.executeUpdate();

        }
    }


    /**
     * Delete the city by city ID from cities table.
     *
     * @param cityId ID of the city
     * @param conn DB Connection
     * @throws Exception throws exception if any error.
     */
    public static void deleteCityByCityId( int cityId, Connection conn ) throws Exception {
        try (PreparedStatement statement = conn.prepareStatement(delete_city_query)) {
            statement.setInt(1, cityId);
            statement.executeUpdate();
        }
    }


}