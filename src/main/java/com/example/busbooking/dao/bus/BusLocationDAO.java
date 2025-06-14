package com.example.busbooking.dao.bus;

import com.example.busbooking.dao.base.LocationDAO;
import com.example.busbooking.db.DBConnection;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusLocationDAO implements LocationDAO {
    public static final String get_schedule_board_drop_points_query = """
                select l.location_id as location_id, l.location_name as location_name
                from stops s\s
                join locations l on l.location_id = s.location_id
                where (s.schedule_id = ? and s.type = ?)
                """;
    public Response getScheduleLocations( int scheduleId, int type ) {

        // type = 1 boarding
        // type = 0 dropping
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(get_schedule_board_drop_points_query);
            statement.setInt(1, scheduleId);
            String loc_type = (type > 0) ? "BOARDING" : "DROPPING";
            statement.setString(2, loc_type);
            ResultSet rs = statement.executeQuery();

            List<Map<String, Object>> stopLocations = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> stop = new HashMap<>();
                stop.put("locationId", rs.getInt("location_id"));
                stop.put("locationName", rs.getString("location_name"));
                stopLocations.add(stop);
            }

            return Response.ok(stopLocations).build();


        } catch (Exception e) {

            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

}