package com.example.auth.dao;

import com.example.auth.db.DBConnection;
import com.example.auth.dto.CityDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityListDAO {
//    public static Map<String, String> getCities() throws Exception {
    public static List<CityDTO> getCities() throws Exception {

        String query = "select city_id, city_name from cities order by city_id";
//        Map<String, String> cities = new HashMap<>();
        List<CityDTO> cities = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String currentCity = rs.getString("city_name");
                int curr_city_id = rs.getInt("city_id");
//                cities.put(currentCity, currentCity);
                cities.add(new CityDTO(curr_city_id, currentCity));
            }
        } catch (Exception e) {
            throw e;
        }
        return cities;
    }
}
