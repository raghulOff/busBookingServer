package com.example.auth.dao;

import com.example.auth.db.DBConnection;
import com.example.auth.dto.RouteDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RouteListDAO {
    public static List<RouteDTO> getRoutes() throws Exception {
        String query = "SELECT \n" +
                "    r.route_id," +
                "    c1.city_name AS source_city,\n" +
                "    c2.city_name AS destination_city,\n" +
                "    r.distance_km,\n" +
                "    r.estimated_time\n" +
                "FROM routes r\n" +
                "JOIN cities c1 ON r.source_city_id = c1.city_id\n" +
                "JOIN cities c2 ON r.destination_city_id = c2.city_id;\n";
        List<RouteDTO> allroutes = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int route_id = rs.getInt("route_id");
                String src = rs.getString("source_city");
                String dest = rs.getString("destination_city");
                int distance = rs.getInt("distance_km");
                String est_time = rs.getString("estimated_time");
                allroutes.add(new RouteDTO(route_id, src, dest, distance, est_time));
            }
        } catch (Exception e) {
            throw e;
        }
        return allroutes;
    }
}