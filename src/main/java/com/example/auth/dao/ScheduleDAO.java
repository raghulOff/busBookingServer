package com.example.auth.dao;

import com.example.auth.db.DBConnection;
import com.example.auth.dto.ScheduleDTO;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
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
    public static Response addNewSchedule( ScheduleDTO scheduleDTO ) throws Exception {
        LocalDate journeyDate = LocalDate.parse(scheduleDTO.getJourneyDate());
        LocalDateTime departure = LocalDateTime.parse(scheduleDTO.getDepartureTime());
        LocalDateTime arrival = LocalDateTime.parse(scheduleDTO.getArrivalTime());
        String query = "insert into schedules (route_id, bus_id, departure_time, arrival_time, available_seats, price, journey_date)\n" +
                "values (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, scheduleDTO.getRouteId());
            statement.setInt(2, scheduleDTO.getBusId());
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(departure));
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(arrival));
            statement.setInt(5, scheduleDTO.getAvailableSeats());
            statement.setDouble(6, scheduleDTO.getPrice());
            statement.setDate(7, java.sql.Date.valueOf(journeyDate));

            statement.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
            return Response.status(Response.Status.CONFLICT).entity("Unable Add New Schedule").build();
        }
        return Response.ok().entity("Schedule created").build();
    }

}
