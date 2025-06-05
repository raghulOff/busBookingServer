package com.example.auth.db;

import com.example.auth.security.PasswordUtil;

import java.sql.Connection;
import java.sql.Statement;


// NOTE: For demo/testing only. Do NOT use hardcoded admin credentials in production!

public class DBInitializer {

    public static void initialize() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS users (
                            user_id SERIAL PRIMARY KEY, 
                            username VARCHAR(100) UNIQUE NOT NULL,
                            password TEXT NOT NULL
                        );
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS roles (
                            role_id SERIAL PRIMARY KEY,
                            role_name VARCHAR(50) UNIQUE NOT NULL
                        );
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS user_roles (
                            user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                            role_id INTEGER NOT NULL REFERENCES roles(role_id) ON DELETE CASCADE,
                            PRIMARY KEY(user_id, role_id)
                        );
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS cities (
                            city_id SERIAL PRIMARY KEY,
                            city_name VARCHAR(100) UNIQUE NOT NULL
                        );
                    """);


            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS buses (
                            bus_id SERIAL PRIMARY KEY,
                            bus_number VARCHAR(20) UNIQUE NOT NULL,
                            bus_type VARCHAR(30),
                            total_seats INT NOT NULL,
                            operator_name VARCHAR(100)
                        );
                    """);


            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS routes (
                            route_id SERIAL PRIMARY KEY,
                            source_city_id INT REFERENCES cities(city_id),
                            destination_city_id INT REFERENCES cities(city_id),
                            distance_km INT,
                            estimated_time INTERVAL,
                            UNIQUE (source_city_id, destination_city_id)
                        );
                    """);


            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS schedules (
                            schedule_id SERIAL PRIMARY KEY,
                            route_id INT REFERENCES routes(route_id),
                            bus_id INT REFERENCES buses(bus_id),
                            departure_time TIMESTAMP NOT NULL,
                            arrival_time TIMESTAMP NOT NULL,
                            available_seats INT,
                            price NUMERIC(10, 2),
                            journey_date DATE,
                            UNIQUE (route_id, bus_id, journey_date)
                        );
                    """);


            stmt.executeUpdate("""
                        INSERT INTO roles (role_name)
                        VALUES ('ADMIN'), ('DEVELOPER'), ('USER')
                        ON CONFLICT (role_name) DO NOTHING;
                    """);

            String hashedAdminPassword = PasswordUtil.hashPassword("admin123");
            String query = "insert into users (username, password) values ('admin','" + hashedAdminPassword + "')" +
                    "    ON CONFLICT (username) DO NOTHING;";
            stmt.executeUpdate(query);


            String hashUserPassword = PasswordUtil.hashPassword("user");

            stmt.executeUpdate("insert into users (username, password) values ('user', '" + hashUserPassword + "')" +
                    "    ON CONFLICT (username) DO NOTHING;");


            stmt.executeUpdate("""
                    insert into user_roles (user_id, role_id) values (1, 1)
                    ON CONFLICT (user_id, role_id) DO NOTHING;
                    """);

            stmt.executeUpdate("insert into user_roles (user_id, role_id) values (2, 3)" +
                    "    ON CONFLICT (user_id, role_id) DO NOTHING;\n");


            stmt.executeUpdate("""
                        INSERT INTO cities (city_name) VALUES
                            ('Chennai'), ('Bangalore'), ('Hyderabad'), ('Coimbatore'), ('Madurai')
                        ON CONFLICT (city_name) DO NOTHING;
                    """);

            stmt.executeUpdate("""
                        INSERT INTO buses (bus_number, bus_type, total_seats, operator_name) VALUES
                            ('TN01AB1234', 'AC Sleeper', 40, 'KPN Travels'),
                            ('TN02XY5678', 'Non-AC Sleeper', 40, 'SRS Travels'),
                            ('TN03CD4321', 'AC Seater', 45, 'Parveen Travels'),
                            ('KA01GH7654', 'Non-AC Seater', 50, 'VRL Logistics'),
                            ('MH01JK2468', 'AC Sleeper', 40, 'National Travels')
                        ON CONFLICT (bus_number) DO NOTHING;
                    """);

            stmt.executeUpdate("""
                        INSERT INTO routes (source_city_id, destination_city_id, distance_km, estimated_time)
                        SELECT c1.city_id, c2.city_id, d.distance_km, d.estimated_time
                        FROM 
                            (VALUES
                                ('Chennai', 'Bangalore', 350, '06:00'::interval),
                                ('Chennai', 'Hyderabad', 630, '11:00'::interval),
                                ('Madurai', 'Chennai', 460, '08:00'::interval),
                                ('Coimbatore', 'Chennai', 500, '09:00'::interval)
                            ) AS d(source, dest, distance_km, estimated_time)
                        JOIN cities c1 ON c1.city_name = d.source
                        JOIN cities c2 ON c2.city_name = d.dest
                        ON CONFLICT (source_city_id, destination_city_id) DO NOTHING;
                    """);


            stmt.executeUpdate("""
                        INSERT INTO schedules (route_id, bus_id, departure_time, arrival_time, available_seats, price, journey_date)
                        VALUES 
                            (1, 1, '2025-06-06 08:00:00', '2025-06-06 14:00:00', 40, 600.00, '2025-06-06'),
                            (2, 2, '2025-06-06 18:00:00', '2025-06-07 05:00:00', 40, 850.00, '2025-06-06'),
                            (3, 3, '2025-06-06 07:00:00', '2025-06-06 15:00:00', 45, 750.00, '2025-06-06'),
                            (4, 4, '2025-06-06 06:30:00', '2025-06-06 15:30:00', 50, 720.00, '2025-06-06')
                        ON CONFLICT (route_id, bus_id, journey_date) DO NOTHING;
                    """);

            System.out.println("Database initialization completed.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Database initialization failed.");
        }
    }
}
