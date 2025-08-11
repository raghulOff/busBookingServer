package com.example.busbooking.registry;

import com.example.busbooking.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import static com.example.busbooking.db.DBConstants.*;


public class BookingStatusRegistry {
    // SQL to retrieve the booking statuses from the bookings table
    private static final String GET_BOOKING_STATUSES_QUERY = String.format("""
            select status_id, status_code from %s;
            """, BOOKING_STATUSES);

    // Map to store booking status details with key as ID / CODE
    private static final Map<Integer, BookingStatus> idToStatus = new HashMap<>();
    private static final Map<String , BookingStatus> codeToStatus = new HashMap<>();

    public static class BookingStatus {
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
        public BookingStatus(Integer statusId, String statusCode) {
            this.statusCode = statusCode;
            this.statusId = statusId;
        }

    }

    /**
     * loads the booking statuses from the DB table and adds into the maps.
     * @throws Exception if any error
     */
    public static void loadFromDB() throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(GET_BOOKING_STATUSES_QUERY)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Integer status_id = rs.getInt("status_id");
                    String status_code = rs.getString("status_code");

                    BookingStatus bs = new BookingStatus(status_id, status_code);
                    idToStatus.put(status_id, bs);
                    codeToStatus.put(status_code, bs);

                }
            }

        }
    }


    public static BookingStatus getById(Integer id) {
        return idToStatus.get(id);
    }
    public static BookingStatus getByCode(String code) {
        return codeToStatus.get(code);
    }


}
