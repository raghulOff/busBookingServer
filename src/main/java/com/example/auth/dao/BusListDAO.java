package com.example.auth.dao;

import com.example.auth.db.DBConnection;
import com.example.auth.dto.BusDTO;
import com.example.auth.dto.BusSearchResponseDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BusListDAO {
    public static List<BusSearchResponseDTO> getAvailableBuses( String from, String to, String doj) throws Exception {
        List<BusSearchResponseDTO> searchResponseList = new ArrayList<>();
        String query = """
            SELECT 
                s.schedule_id,
                b.bus_number,
                b.bus_type,
                s.departure_time,
                s.arrival_time,
                s.available_seats,
                s.price,
                b.operator_name,
                r.distance_km,
                r.estimated_time
            FROM schedules s
            JOIN routes r ON s.route_id = r.route_id
            JOIN buses b ON s.bus_id = b.bus_id
            JOIN cities c1 ON r.source_city_id = c1.city_id
            JOIN cities c2 ON r.destination_city_id = c2.city_id
            WHERE 
                c1.city_name = ? AND 
                c2.city_name = ? AND 
                s.journey_date = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, from);
            stmt.setString(2, to);
            stmt.setDate(3, Date.valueOf(doj)); // ensure `doj` is in yyyy-MM-dd format

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int scheduleId = rs.getInt("schedule_id");
                    String busNumber = rs.getString("bus_number");
                    String busType = rs.getString("bus_type");
                    String departureTime = rs.getString("departure_time");
                    String arrivalTime = rs.getString("arrival_time");
                    int available_seats = rs.getInt("available_seats");
                    BigDecimal price = rs.getBigDecimal("price");
                    String operatorName = rs.getString("operator_name");
                    int distanceKm = rs.getInt("distance_km");
                    String estimatedTime = rs.getString("estimated_time");

                    searchResponseList.add(new BusSearchResponseDTO(scheduleId, busNumber, busType, departureTime, arrivalTime, available_seats, price, operatorName, distanceKm));

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResponseList;
    }



    public static List<BusDTO> getAllBuses() throws Exception {
        String query = "select bus_id, bus_number, bus_type, total_seats, operator_name from buses";
        List<BusDTO> allBuses = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int busId = rs.getInt("bus_id");
                String busNumber = rs.getString("bus_number");
                String busType = rs.getString("bus_type");
                int total_seats = rs.getInt("total_seats");
                String op_name = rs.getString("operator_name");
                allBuses.add(new BusDTO(busId, busNumber, busType, total_seats, op_name));
            }
        } catch (Exception e) {
            throw e;
        }
        return allBuses;
    }
}
