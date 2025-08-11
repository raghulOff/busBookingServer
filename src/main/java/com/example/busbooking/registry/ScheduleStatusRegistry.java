package com.example.busbooking.registry;

import com.example.busbooking.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import static com.example.busbooking.db.DBConstants.SCHEDULE_STATUSES;

public class ScheduleStatusRegistry {
    // SQL to retrieve the schedule statuses from the bookings table
    private static final String GET_SCHEDULE_STATUSES_QUERY = String.format("""
            select status_id, status_code from %s;
            """, SCHEDULE_STATUSES);

    // Map to store schedule status details with key as ID / CODE
    private static final Map<Integer, ScheduleStatusRegistry.ScheduleStatus> idToStatus = new HashMap<>();
    private static final Map<String, ScheduleStatusRegistry.ScheduleStatus> codeToStatus = new HashMap<>();

    /**
     * loads the schedule statuses from the DB table and adds into the maps.
     * @throws Exception if any error
     */
    public static void loadFromDB() throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(GET_SCHEDULE_STATUSES_QUERY)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Integer status_id = rs.getInt("status_id");
                    String status_code = rs.getString("status_code");

                    ScheduleStatusRegistry.ScheduleStatus scheduleStatus = new ScheduleStatusRegistry.ScheduleStatus(status_id, status_code);
                    idToStatus.put(status_id, scheduleStatus);
                    codeToStatus.put(status_code, scheduleStatus);

                }
            }

        }
    }

    public static ScheduleStatusRegistry.ScheduleStatus getById( Integer id ) {
        return idToStatus.get(id);
    }

    public static ScheduleStatusRegistry.ScheduleStatus getByCode( String code ) {
        return codeToStatus.get(code);
    }

    public static class ScheduleStatus {
        private Integer statusId;
        private String statusCode;

        public ScheduleStatus( Integer statusId, String statusCode ) {
            this.statusCode = statusCode;
            this.statusId = statusId;
        }

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

    }

}
