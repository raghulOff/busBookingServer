package com.example.busbooking.dao.city;


import com.example.busbooking.db.DBConnection;
import com.example.busbooking.dto.base.CitiesDTO;
import com.example.busbooking.service.CitiesService;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.busbooking.db.DBConstants.CITIES;
import static com.example.busbooking.db.DBConstants.UNIQUE_VIOLATION;

/**
 * DAO class for managing CRUD operations related to cities.
 */
public class CitiesDAO {

    // SQL to get all the cities
    private static final String get_all_cities_query = String.format("select city_id, city_name from %s order by city_id", CITIES);

    // SQL to add a new city
    private static final String add_city_query = String.format("insert into %s (city_name) values (?)", CITIES);


    /**
     * Retrieves all cities from the database.
     *
     * @return Response containing a list of all cities or an error status
     * @throws Exception if database connection or query fails
     */

    public Response getCities() throws Exception {

        // List to store all the cities with their respective ID.
        List<CitiesDTO> cities = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            // GET all the cities
            PreparedStatement statement = conn.prepareStatement(get_all_cities_query);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String currentCity = rs.getString("city_name");
                int curr_city_id = rs.getInt("city_id");

                // Adding in the `cities` list
                cities.add(new CitiesDTO(curr_city_id, currentCity));
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok("Cities are retrieved").entity(cities).build();

    }

    /**
     * Adds a new city to the database.
     *
     * @param citiesDTO DTO containing the new city's name
     * @return Response indicating creation success or conflict if city exists
     * @throws Exception if database error occurs
     */

    public Response addNewCity( CitiesDTO citiesDTO ) throws Exception {

        try (Connection conn = DBConnection.getConnection()) {

            // ADD a new city
            PreparedStatement statement = conn.prepareStatement(add_city_query);
            statement.setString(1, citiesDTO.getCityName());
            statement.executeUpdate();


        } catch (SQLException e) {
            if (e.getSQLState().equals(UNIQUE_VIOLATION)) {
                return Response.status(Response.Status.CONFLICT).entity("City already exist.").build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error.").build();
        }
        return Response.status(Response.Status.CREATED).entity("City added.").build();

    }


    /**
     * Deletes a city by its ID after verifying it exists.
     * If the city is linked to other entities via foreign key, deletion fails.
     *
     * @param cityId ID of the city to delete
     * @return Response indicating success, failure, or constraint violation
     * @throws Exception if SQL or connection error occurs
     */

    public Response delete( int cityId ) throws Exception {

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Check if the city exists.
            if (!CitiesService.isCityExists(cityId, conn)) {
                return Response.status(Response.Status.NOT_FOUND).entity("City doesn't exist.").build();
            }

            // Check if the city is assigned with a route.
            if (CitiesService.isCityAssignedWithRoute(cityId, conn)) {
                return Response.status(Response.Status.FORBIDDEN).entity("This city has been assigned with a route. Please modify the route first.").build();
            }

            // Delete mapping of city location from city_locations table
            // Delete locations linked with the city
            CitiesService.deleteCityLinkedWithLocationByCityId(cityId, conn);

            // DELETE the city
            CitiesService.deleteCityByCityId(cityId, conn);


            conn.commit();

        } catch (SQLException sqlException) {

            System.out.println(sqlException.getMessage());
            DBConnection.rollbackConnection(conn);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error occurred.").build();

        } catch (Exception e) {
            DBConnection.rollbackConnection(conn);
            throw e;

        } finally {
            DBConnection.closeConnection(conn);
        }

        return Response.ok("City deleted").build();
    }
}
