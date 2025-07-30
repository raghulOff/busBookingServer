package com.example.busbooking.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.example.busbooking.db.DBConstants.CITY_LOCATIONS;
import static com.example.busbooking.db.DBConstants.LOCATIONS;

public class LocationService {
    // SQL to add a new location
    private static final String add_new_location_query = String.format("insert into %s (location_name) values (?);", LOCATIONS);
    // SQL to insert the association mapping data between city and location in the CITY_LOCATIONS table.
    private static final String add_new_city_location_query = String.format("insert into %s (city_id, location_id) values (?, ?);", CITY_LOCATIONS);

    /**
     * Add new location into the locations table and return the generated location ID
     * @param locationName name of location
     * @param conn DB Connection
     * @return location ID of the newly generated location
     * @throws Exception if any error occurs.
     */
    public static Integer addLocationReturnGeneratedID ( String locationName, Connection conn ) throws Exception {
        try (PreparedStatement statement = conn.prepareStatement(add_new_location_query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, locationName);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return null;
        }
    }


    /**
     * Insert the mapping of city with location into the city_locations table
     * @param cityId ID of the city.
     * @param locationId ID of the location
     * @param conn DB Connection
     * @throws Exception if any error occurs.
     */
    public static void mapLocationWithCity(int cityId, int locationId, Connection conn) throws Exception {
        try (PreparedStatement statement = conn.prepareStatement(add_new_city_location_query)) {
            statement.setInt(1, cityId);
            statement.setInt(2, locationId);
            statement.executeUpdate();
        }

    }
}
