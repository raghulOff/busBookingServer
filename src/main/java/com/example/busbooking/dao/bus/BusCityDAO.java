package com.example.busbooking.dao.bus;

import com.example.busbooking.dao.base.CityDAO;
import com.example.busbooking.db.DBConnection;
import com.example.busbooking.dto.base.CityDTO;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BusCityDAO implements CityDAO {

    public static final String get_all_cities_query = "select city_id, city_name from cities order by city_id";
    public static final String add_city_query = "insert into cities (city_name) values (?)";

    public List<CityDTO> getCities() throws Exception {
        List<CityDTO> cities = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(get_all_cities_query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String currentCity = rs.getString("city_name");
                int curr_city_id = rs.getInt("city_id");
                cities.add(new CityDTO(curr_city_id, currentCity));
            }
        } catch (Exception e) {
            throw e;
        }
        return cities;
    }

    public Response addNewCity( CityDTO cityDTO ) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(add_city_query);
            statement.setString(1, cityDTO.getCityName());
            statement.executeUpdate();


        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("city already exist").build();
        }
        return Response.status(Response.Status.CREATED).entity("city added").build();

    }
}
