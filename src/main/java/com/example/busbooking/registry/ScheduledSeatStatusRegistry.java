package com.example.busbooking.registry;

import com.example.busbooking.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import static com.example.busbooking.db.DBConstants.*;

public class ScheduledSeatStatusRegistry {
    // SQL to retrieve the scheduled seat statuses from the bookings table
    private static final String GET_SCHEDULED_SEAT_STATUSES_QUERY = String.format("""
            select status_id, status_code from %s;
            """, SCHEDULED_SEAT_STATUSES);

    // Map to store scheduled seat status details with key as ID / CODE
    private static final Map<Integer, ScheduledSeatStatusRegistry.ScheduledSeatStatus> idToStatus = new HashMap<>();
    private static final Map<String , ScheduledSeatStatusRegistry.ScheduledSeatStatus> codeToStatus = new HashMap<>();

    public static class ScheduledSeatStatus {
        private Integer statusId;
        private String statusCode;

        public Integer getStatusId() {
            return statusId;
        }

        public void setStatusId( Integer statusId ) {
            this.statusId = statusId;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode( String statusCode ) {
            this.statusCode = statusCode;
        }
        public ScheduledSeatStatus(Integer statusId, String statusCode) {
            this.statusCode = statusCode;
            this.statusId = statusId;
        }

    }

    /**
     * loads the scheduled seat statuses from the DB table and adds into the maps.
     * @throws Exception if any error.
     */
    public static void loadFromDB() throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(GET_SCHEDULED_SEAT_STATUSES_QUERY)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Integer status_id = rs.getInt("status_id");
                    String status_code = rs.getString("status_code");

                    ScheduledSeatStatusRegistry.ScheduledSeatStatus scheduledSeatStatus = new ScheduledSeatStatusRegistry.ScheduledSeatStatus(status_id, status_code);
                    idToStatus.put(status_id, scheduledSeatStatus);
                    codeToStatus.put(status_code, scheduledSeatStatus);

                }
            }

        }
    }


    public static ScheduledSeatStatusRegistry.ScheduledSeatStatus getById( Integer id) {
        return idToStatus.get(id);
    }
    public static ScheduledSeatStatusRegistry.ScheduledSeatStatus getByCode( String code) {
        return codeToStatus.get(code);
    }

}