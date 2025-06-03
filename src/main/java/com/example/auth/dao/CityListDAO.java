package com.example.auth.dao;

import com.example.auth.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityListDAO {
    public static Map<String, String> getCities() throws Exception {
        String query = "select city_name from cities order by city_id";
        Map<String, String> cities = new HashMap<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String currentCity = rs.getString("city_name");
                cities.put(currentCity, currentCity);
            }
        } catch (Exception e) {
            throw e;
        }
        return cities;
    }
}
