package com.example.busbooking.db;

import com.example.busbooking.dao.base.VehicleDAO;
import com.example.busbooking.dao.bus.BusVehicleDAO;
import com.example.busbooking.dto.bus.BusVehicleDTO;
import com.example.busbooking.security.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



// NOTE: For demo/testing only. Do NOT use hardcoded admin credentials in production!

public class DBInitializer {

    public static BusVehicleDTO createSampleBusVehicleDTO() {
        List<BusVehicleDTO.SeatGridCount> seatGridCounts = new ArrayList<>();

        // UPPER seat grid counts
        seatGridCounts.add(new BusVehicleDTO.SeatGridCount(1, 5, "UPPER")); // Sleeper
        seatGridCounts.add(new BusVehicleDTO.SeatGridCount(2, 5, "UPPER")); // Sleeper
        seatGridCounts.add(new BusVehicleDTO.SeatGridCount(3, 5, "UPPER")); // Sleeper

        // LOWER seat grid counts
        seatGridCounts.add(new BusVehicleDTO.SeatGridCount(1, 5, "LOWER")); // Sleeper
        seatGridCounts.add(new BusVehicleDTO.SeatGridCount(2, 10, "LOWER")); // Seater
        seatGridCounts.add(new BusVehicleDTO.SeatGridCount(3, 10, "LOWER")); // Seater

        List<BusVehicleDTO.SeatDetails> seatDetails = new ArrayList<>();

        // Column 1: UPPER - 5 Sleeper seats
        for (int row = 1; row <= 5; row++) {
            seatDetails.add(new BusVehicleDTO.SeatDetails(row, 3, 1, "UPPER")); // Sleeper
        }

        // Column 1: LOWER - 5 Sleeper seats
        for (int row = 1; row <= 5; row++) {
            seatDetails.add(new BusVehicleDTO.SeatDetails(row, 3, 1, "LOWER")); // Sleeper
        }

        // Column 2: UPPER - 5 Sleeper seats
        for (int row = 1; row <= 5; row++) {
            seatDetails.add(new BusVehicleDTO.SeatDetails(row, 3, 2, "UPPER")); // Sleeper
        }

        // Column 2: LOWER - 10 Seater seats
        for (int row = 1; row <= 10; row++) {
            seatDetails.add(new BusVehicleDTO.SeatDetails(row, 1, 2, "LOWER")); // Seater
        }

        // Column 3: UPPER - 5 Sleeper seats
        for (int row = 1; row <= 5; row++) {
            seatDetails.add(new BusVehicleDTO.SeatDetails(row, 3, 3, "UPPER")); // Sleeper
        }

        // Column 3: LOWER - 10 Seater seats
        for (int row = 1; row <= 10; row++) {
            seatDetails.add(new BusVehicleDTO.SeatDetails(row, 1, 3, "LOWER")); // Seater
        }

        // Create the DTO
        BusVehicleDTO busVehicleDTO = new BusVehicleDTO();
        busVehicleDTO.setVehicleNumber("TN01AB1234");
        busVehicleDTO.setOperatorName("KPN Travels");
        busVehicleDTO.setBusType("Mixed Sleeper-Seater");
        busVehicleDTO.setTotalColumns(3);
        busVehicleDTO.setSeatGridCount(seatGridCounts);
        busVehicleDTO.setSeatDetails(seatDetails);

        return busVehicleDTO;
    }



    public static void initialize() throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();


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
                        bus_number VARCHAR(20) NOT NULL UNIQUE,
                        operator_name VARCHAR(100) NOT NULL,
                        bus_type VARCHAR(50) NOT NULL,
                        total_columns INTEGER NOT NULL CHECK (total_columns > 0)
                    );
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS seat_type (
                        seat_type_id SERIAL PRIMARY KEY,
                        seat_type_name VARCHAR(50) NOT NULL UNIQUE
                    );
                    """);

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS seat_grid_columns (
                        column_id SERIAL PRIMARY KEY,
                        bus_id INTEGER NOT NULL REFERENCES buses(bus_id) ON DELETE CASCADE,
                        col_number INTEGER NOT NULL CHECK (col_number > 0),
                        total_rows INTEGER NOT NULL CHECK (total_rows > 0),
                        pos VARCHAR(10) NOT NULL CHECK (pos IN ('UPPER', 'LOWER')),
                    
                        UNIQUE (bus_id, col_number, pos)
                    );
                    """);


            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS seats (
                        seat_id SERIAL PRIMARY KEY,
                        column_id INTEGER NOT NULL REFERENCES seat_grid_columns(column_id) ON DELETE CASCADE,
                        row_number INTEGER NOT NULL CHECK (row_number > 0),
                        seat_type_id INTEGER NOT NULL REFERENCES seat_type(seat_type_id),
                        seat_number VARCHAR(10),
                        status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
                    
                        UNIQUE (column_id, row_number)
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
                            price NUMERIC(10, 2),
                            journey_date DATE,
                            UNIQUE (route_id, bus_id, journey_date)
                        );
                    """);

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS passenger_details (
                        passenger_id SERIAL PRIMARY KEY,
                        passenger_name VARCHAR(100) NOT NULL,
                        passenger_age INT CHECK (passenger_age > 0)
                    );
                    """);


            stmt.executeUpdate("""
                    CREATE TABLE IF
                     NOT EXISTS locations (
                        location_id SERIAL PRIMARY KEY,
                        location_name VARCHAR(100) NOT NULL,
                        UNIQUE (location_name)
                    );
                    """);
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS bookings (
                        booking_id SERIAL PRIMARY KEY,
                        user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
                        schedule_id INT REFERENCES schedules(schedule_id) ON DELETE CASCADE,
                        booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        total_amount NUMERIC(10, 2),
                        boarding_point_id INT REFERENCES locations(location_id) ON DELETE CASCADE,
                        dropping_point_id INT REFERENCES locations(location_id) ON DELETE CASCADE,
                        trip_status VARCHAR(20) DEFAULT 'UPCOMING'
                    );
                    """);

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS booking_seats (
                        booking_seat_id SERIAL PRIMARY KEY,
                        booking_id INT REFERENCES bookings(booking_id) ON DELETE CASCADE,
                        seat_id INT REFERENCES seats(seat_id) ON DELETE CASCADE,
                        passenger_id INT REFERENCES passenger_details(passenger_id),
                        UNIQUE (booking_id, seat_id),
                        status VARCHAR(20)
                    );
                    """);


            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS city_locations (
                        city_id INT REFERENCES cities(city_id) ON DELETE CASCADE,
                        location_id INT REFERENCES locations(location_id) ON DELETE CASCADE,
                        PRIMARY KEY (city_id, location_id),
                        UNIQUE (city_id, location_id)
                    );
                    
                    """);


            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS stops (
                        stop_id SERIAL PRIMARY KEY,
                        schedule_id INT REFERENCES schedules(schedule_id) ON DELETE CASCADE,
                        city_id INT REFERENCES cities(city_id),
                        location_id INT REFERENCES locations(location_id) ON DELETE CASCADE,
                        type VARCHAR(10) CHECK (type IN ('BOARDING', 'DROPPING')),
                        time TIME,
                        UNIQUE(schedule_id, city_id, location_id, type)
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

            stmt.executeUpdate("insert into seat_type (seat_type_name) values ('SEATER'), ('SEMI_SLEEPER'), ('SLEEPER')" +
                    " ON CONFLICT DO NOTHING;");


            stmt.executeUpdate("""
                        INSERT INTO cities (city_name) VALUES
                            ('Chennai'), ('Bangalore'), ('Hyderabad'), ('Coimbatore'), ('Madurai')
                        ON CONFLICT (city_name) DO NOTHING;
                    """);


            try {

            VehicleDAO busVehicleDAO = new BusVehicleDAO();
            busVehicleDAO.addNew(createSampleBusVehicleDTO());
            } catch (Exception e) {
//                System.err.println(e);
//                System.out.println("duplicate bus value bro when initializing DB, No problem");
            }




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
                        INSERT INTO schedules (route_id, bus_id, departure_time, arrival_time, price, journey_date)
                        VALUES
                            (1, 1, '2025-06-06 08:00:00', '2025-06-06 14:00:00', 600.00, '2025-06-06'),
                            (2, 1, '2025-06-06 18:00:00', '2025-06-07 05:00:00', 850.00, '2025-06-06'),
                            (3, 1, '2025-06-06 07:00:00', '2025-06-06 15:00:00', 750.00, '2025-06-06'),
                            (4, 1, '2025-06-06 06:30:00', '2025-06-06 15:30:00', 720.00, '2025-06-06')
                        ON CONFLICT (route_id, bus_id, journey_date) DO NOTHING;
                    """);


            PreparedStatement locationStmt = conn.prepareStatement(
                    "insert into locations (location_name) values ('Gandhipuram'), ('Lakshmi Mills'), ('Guindy'), ('Tambaram'), ('Arappalayam'), ('Maattuthavani')\n" +
                            "ON CONFLICT (location_name) DO NOTHING;");
            locationStmt.executeUpdate();

            PreparedStatement cityLocStmt = conn.prepareStatement(
                    "insert into city_locations values (1, 1), (1, 2), (4, 3), (4, 4), (5, 5), (5, 6) ON CONFLICT (city_id, location_id) DO NOTHING;");
            cityLocStmt.executeUpdate();


            PreparedStatement stopStmt = conn.prepareStatement(
                    """
                               INSERT INTO stops (schedule_id, city_id, location_id, type, time) VALUES\s
                               (2, 1, 1, 'BOARDING', '6:00:00'),
                               (2, 4, 4, 'DROPPING', '12:00:00'),
                               (2, 1, 2, 'BOARDING', '12:00:00'),
                               (2, 1, 3, 'DROPPING', '12:00:00'),
                               (1, 5, 5, 'BOARDING', '12:00:00'),
                               (1, 5, 6, 'BOARDING', '23:00:00'),
                               (1, 1, 3, 'DROPPING', '12:00:00'),
                               (1, 1, 4, 'DROPPING', '13:00:00')
                               ON CONFLICT (schedule_id, city_id, location_id, type) DO NOTHING;
                            """
            );

            stopStmt.executeUpdate();

            System.out.println("Database initialization completed.");


        } catch (Exception e) {
            e.printStackTrace();

            System.err.println("Database initialization failed.");
        }
    }
}