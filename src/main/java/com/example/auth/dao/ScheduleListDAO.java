package com.example.auth.dao;

import com.example.auth.db.DBConnection;
import com.example.auth.dto.ScheduleDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ScheduleListDAO {
    public static List<ScheduleDTO> getSchedules() throws Exception {
        String query = "select schedule_id, route_id, bus_id, departure_time, arrival_time, available_seats, price, journey_date\n" +
                "from schedules";

        List<ScheduleDTO> allSchedules = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int schedule_id = rs.getInt("schedule_id");
                int routeid = rs.getInt("route_id");
                int busid = rs.getInt("bus_id");
                String dep_time = rs.getString("departure_time");
                String arr_time = rs.getString("arrival_time");
                int av_seats = rs.getInt("available_seats");
                double price = rs.getDouble("price");
                String journeyDate = rs.getString("journey_date");
                allSchedules.add(new ScheduleDTO(schedule_id, routeid, busid, dep_time, arr_time, av_seats, price, journeyDate));
            }
        } catch (Exception e) {
            throw e;
        }
        return allSchedules;
    }
}
