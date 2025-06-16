package com.example.busbooking.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GenerateSeats {
    // When a schedule is created, seats are allocated for that bus.
    public static void generateSeatsForSchedule( int scheduleId, int totalSeats, Connection conn, String insert_seat_query ) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(insert_seat_query);

        int seatsPerRow = 4;
        int rows = (int) Math.ceil(totalSeats / (double) seatsPerRow);

        for (int i = 0; i < rows; i++) {
            char rowChar = (char) ('A' + i);
            for (int j = 1; j <= seatsPerRow; j++) {
                int seatIndex = i * seatsPerRow + (j - 1);
                if (seatIndex >= totalSeats) break;

                String seatNumber = rowChar + String.valueOf(j);

                statement.setInt(1, scheduleId);
                statement.setString(2, seatNumber);
                statement.setInt(3, i + 1);
                statement.setInt(4, j);
                statement.addBatch();
            }
        }

        statement.executeBatch();
        statement.close();
    }
}
