package com.example.busbooking.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ResetDatabase {
    public static void main( String[] args ) {
        String url = "jdbc:postgresql://localhost:5432/postgres"; // not BusBookingDB!
        String user = "postgres";
        String password = "zoho";
//        String dbName = "BusBookingDB";


        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

//             1. Force disconnect other users (important!)
            stmt.execute("""
                        SELECT pg_terminate_backend(pg_stat_activity.pid)
                        FROM pg_stat_activity
                        WHERE pg_stat_activity.datname = 'BusBookingDB'
                          AND pid <> pg_backend_pid();
                    """);
            // 2. Drop if exists
            stmt.executeUpdate("""
                        DROP DATABASE IF EXISTS "BusBookingDB"
                    """);

            // 3. Create new
            stmt.executeUpdate("""
                        CREATE DATABASE "BusBookingDB"
                    """);

            System.out.println("Database dropped and recreated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
