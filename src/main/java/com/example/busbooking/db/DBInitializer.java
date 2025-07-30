package com.example.busbooking.db;

import com.example.busbooking.dao.base.ScheduleDAO;
import com.example.busbooking.dao.base.VehicleDAO;
import com.example.busbooking.dao.bus.BusSchedulesDAO;
import com.example.busbooking.dao.bus.BusVehiclesDAO;
import com.example.busbooking.dto.base.SchedulesDTO;
import com.example.busbooking.dto.bus.BusVehiclesDTO;
import com.example.busbooking.security.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// NOTE: For demo/testing only. Do NOT use hardcoded admin credentials in production!

public class DBInitializer {

    public static BusVehiclesDTO createSampleBusVehicleDTO() {
        List<BusVehiclesDTO.SeatGridCount> seatGridCounts = new ArrayList<>();

        // UPPER seat grid counts
        seatGridCounts.add(new BusVehiclesDTO.SeatGridCount(1, 5, "UPPER")); // Sleeper
        seatGridCounts.add(new BusVehiclesDTO.SeatGridCount(2, 5, "UPPER")); // Sleeper
        seatGridCounts.add(new BusVehiclesDTO.SeatGridCount(3, 5, "UPPER")); // Sleeper

        // LOWER seat grid counts
        seatGridCounts.add(new BusVehiclesDTO.SeatGridCount(1, 5, "LOWER")); // Sleeper
        seatGridCounts.add(new BusVehiclesDTO.SeatGridCount(2, 10, "LOWER")); // Seater
        seatGridCounts.add(new BusVehiclesDTO.SeatGridCount(3, 10, "LOWER")); // Seater

        List<BusVehiclesDTO.SeatDetails> seatDetails = new ArrayList<>();

        // Column 1: UPPER - 5 Sleeper seats
        for (int row = 1; row <= 5; row++) {
            seatDetails.add(new BusVehiclesDTO.SeatDetails(row, 3, 1, "UPPER")); // Sleeper
        }

        // Column 1: LOWER - 5 Sleeper seats
        for (int row = 1; row <= 5; row++) {
            seatDetails.add(new BusVehiclesDTO.SeatDetails(row, 3, 1, "LOWER")); // Sleeper
        }

        // Column 2: UPPER - 5 Sleeper seats
        for (int row = 1; row <= 5; row++) {
            seatDetails.add(new BusVehiclesDTO.SeatDetails(row, 3, 2, "UPPER")); // Sleeper
        }

        // Column 2: LOWER - 10 Seater seats
        for (int row = 1; row <= 10; row++) {
            seatDetails.add(new BusVehiclesDTO.SeatDetails(row, 1, 2, "LOWER")); // Seater
        }

        // Column 3: UPPER - 5 Sleeper seats
        for (int row = 1; row <= 5; row++) {
            seatDetails.add(new BusVehiclesDTO.SeatDetails(row, 3, 3, "UPPER")); // Sleeper
        }

        // Column 3: LOWER - 10 Seater seats
        for (int row = 1; row <= 10; row++) {
            seatDetails.add(new BusVehiclesDTO.SeatDetails(row, 1, 3, "LOWER")); // Seater
        }

        // Create the DTO
        BusVehiclesDTO busVehicleDTO = new BusVehiclesDTO();
        busVehicleDTO.setVehicleNumber("TN01AB1234");
        busVehicleDTO.setOperatorName("KPN Travels");
        busVehicleDTO.setBusType("Mixed Sleeper-Seater");
        busVehicleDTO.setTotalColumns(3);
        busVehicleDTO.setSeatGridCount(seatGridCounts);
        busVehicleDTO.setSeatDetails(seatDetails);

        return busVehicleDTO;
    }

    public static BusVehiclesDTO createSampleBusVehicleDTO2() {
        List<BusVehiclesDTO.SeatGridCount> seatGridCounts = new ArrayList<>();
        // LOWER seat grid counts
        seatGridCounts.add(new BusVehiclesDTO.SeatGridCount(1, 10, "LOWER")); // Sleeper
        seatGridCounts.add(new BusVehiclesDTO.SeatGridCount(2, 10, "LOWER")); // Sleeper
        seatGridCounts.add(new BusVehiclesDTO.SeatGridCount(3, 10, "LOWER")); // Seater
        seatGridCounts.add(new BusVehiclesDTO.SeatGridCount(4, 10, "LOWER")); // Seater

        List<BusVehiclesDTO.SeatDetails> seatDetails = new ArrayList<>();


        // Column 1: LOWER - 5 Sleeper seats
        for (int row = 1; row <= 10; row++) {
            seatDetails.add(new BusVehiclesDTO.SeatDetails(row, 1, 1, "LOWER")); // Sleeper
        }


        // Column 2: LOWER - 10 Seater seats
        for (int row = 1; row <= 10; row++) {
            seatDetails.add(new BusVehiclesDTO.SeatDetails(row, 1, 2, "LOWER")); // Seater
        }

        // Column 3: LOWER - 10 Seater seats
        for (int row = 1; row <= 10; row++) {
            seatDetails.add(new BusVehiclesDTO.SeatDetails(row, 1, 3, "LOWER")); // Seater
        }
        // Column 3: LOWER - 10 Seater seats
        for (int row = 1; row <= 10; row++) {
            seatDetails.add(new BusVehiclesDTO.SeatDetails(row, 1, 4, "LOWER")); // Seater
        }


        // Create the DTO
        BusVehiclesDTO busVehicleDTO = new BusVehiclesDTO();
        busVehicleDTO.setVehicleNumber("TN10BA4321");
        busVehicleDTO.setOperatorName("SRS Travels");
        busVehicleDTO.setBusType("AC Seater");
        busVehicleDTO.setTotalColumns(4);
        busVehicleDTO.setSeatGridCount(seatGridCounts);
        busVehicleDTO.setSeatDetails(seatDetails);

        return busVehicleDTO;
    }


    public static void initialize() throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            stmt.executeUpdate(String.format("""
                        CREATE TABLE IF NOT EXISTS %s (
                            user_id SERIAL PRIMARY KEY, 
                            username VARCHAR(100) UNIQUE NOT NULL,
                            password TEXT NOT NULL
                        );
                    """, DBConstants.USERS));

            stmt.executeUpdate(String.format("""
                        CREATE TABLE IF NOT EXISTS %s (
                            role_id SERIAL PRIMARY KEY,
                            role_name VARCHAR(50) UNIQUE NOT NULL
                        );
                    """, DBConstants.ROLES));

            stmt.executeUpdate(String.format("""
                        CREATE TABLE IF NOT EXISTS %s (
                            user_id INTEGER NOT NULL REFERENCES %s(user_id) ON DELETE CASCADE,
                            role_id INTEGER NOT NULL REFERENCES %s(role_id) ON DELETE CASCADE,
                            PRIMARY KEY(user_id, role_id)
                        );
                    """, DBConstants.USER_ROLES, DBConstants.USERS, DBConstants.ROLES));

            stmt.executeUpdate(String.format("""
                        CREATE TABLE IF NOT EXISTS %s (
                            city_id SERIAL PRIMARY KEY,
                            city_name VARCHAR(100) UNIQUE NOT NULL
                        );
                    """, DBConstants.CITIES));


            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        bus_id SERIAL PRIMARY KEY,
                        bus_number VARCHAR(20) NOT NULL UNIQUE,
                        operator_name VARCHAR(100) NOT NULL,
                        bus_type VARCHAR(50) NOT NULL,
                        total_columns INTEGER NOT NULL CHECK (total_columns > 0)
                    );
                    """, DBConstants.BUSES));
            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        seat_type_id SERIAL PRIMARY KEY,
                        seat_type_name VARCHAR(50) NOT NULL UNIQUE
                    );
                    """, DBConstants.SEAT_TYPE));

            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        column_id SERIAL PRIMARY KEY,
                        bus_id INTEGER NOT NULL REFERENCES %s(bus_id) ON DELETE CASCADE,
                        col_number INTEGER NOT NULL CHECK (col_number > 0),
                        total_rows INTEGER NOT NULL CHECK (total_rows > 0),
                        pos VARCHAR(10) NOT NULL CHECK (pos IN ('UPPER', 'LOWER')),
                    
                        UNIQUE (bus_id, col_number, pos)
                    );
                    """, DBConstants.SEAT_GRID_COLUMNS, DBConstants.BUSES));


            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        seat_id SERIAL PRIMARY KEY,
                        column_id INTEGER NOT NULL REFERENCES %s(column_id) ON DELETE CASCADE,
                        row_number INTEGER NOT NULL CHECK (row_number > 0),
                        seat_type_id INTEGER NOT NULL REFERENCES %s(seat_type_id),
                        seat_number VARCHAR(10),
                    
                        UNIQUE (column_id, row_number)
                    );
                    """, DBConstants.SEATS, DBConstants.SEAT_GRID_COLUMNS, DBConstants.SEAT_TYPE));

            stmt.executeUpdate(String.format("""
                        CREATE TABLE IF NOT EXISTS %s (
                            route_id SERIAL PRIMARY KEY,
                            source_city_id INT REFERENCES %s(city_id),
                            destination_city_id INT REFERENCES %s(city_id),
                            distance_km INT,
                            estimated_time INTERVAL,
                            UNIQUE (source_city_id, destination_city_id, distance_km, estimated_time)
                        );
                    """, DBConstants.ROUTES, DBConstants.CITIES, DBConstants.CITIES));

            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        status_id SERIAL PRIMARY KEY,
                        status_code VARCHAR(20) UNIQUE NOT NULL,
                        description TEXT
                    );
                    """, DBConstants.SCHEDULE_STATUSES));

            stmt.executeUpdate("""
                    INSERT INTO schedule_statuses (status_code) VALUES
                    ('ACTIVE'),
                    ('CANCELLED'),
                    ('DELAYED') ON CONFLICT DO NOTHING;
                    """);

            stmt.executeUpdate(String.format("""
                        CREATE TABLE IF NOT EXISTS %s (
                            schedule_id SERIAL PRIMARY KEY,
                            route_id INT REFERENCES %s(route_id),
                            bus_id INT REFERENCES %s(bus_id),
                            departure_time TIMESTAMP NOT NULL,
                            arrival_time TIMESTAMP NOT NULL,
                            price NUMERIC(10, 2),
                            journey_date DATE,
                            status_id INT REFERENCES schedule_statuses(status_id) DEFAULT 1
                        );
                    """, DBConstants.SCHEDULES, DBConstants.ROUTES, DBConstants.BUSES));

            stmt.executeUpdate(String.format("""
                    CREATE UNIQUE INDEX IF NOT EXISTS unique_active_schedule
                    ON %s (route_id, bus_id, journey_date)
                    WHERE status_id = 1;
                    """, DBConstants.SCHEDULES));

            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        passenger_id SERIAL PRIMARY KEY,
                        passenger_name VARCHAR(100) NOT NULL,
                        passenger_age INT CHECK (passenger_age > 0)
                    );
                    """, DBConstants.PASSENGER_DETAILS));


            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF
                     NOT EXISTS %s (
                        location_id SERIAL PRIMARY KEY,
                        location_name VARCHAR(100) NOT NULL,
                        UNIQUE (location_name)
                    );
                    """, DBConstants.LOCATIONS));


            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        booking_id SERIAL PRIMARY KEY,
                        user_id INT REFERENCES %s(user_id) ON DELETE CASCADE,
                        schedule_id INT REFERENCES %s(schedule_id) ON DELETE CASCADE,
                        booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        total_amount NUMERIC(10, 2),
                        boarding_point_id INT REFERENCES %s(location_id) ON DELETE CASCADE,
                        dropping_point_id INT REFERENCES %s(location_id) ON DELETE CASCADE
                    );
                    """, DBConstants.BOOKINGS, DBConstants.USERS, DBConstants.SCHEDULES, DBConstants.LOCATIONS, DBConstants.LOCATIONS));

            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        status_id SERIAL PRIMARY KEY,
                        status_code VARCHAR(30) UNIQUE NOT NULL
                    )
                    """, DBConstants.SCHEDULED_SEAT_STATUSES));

            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        scheduled_seat_id SERIAL PRIMARY KEY,
                        seat_id INT REFERENCES %s(seat_id) ON DELETE CASCADE,
                        schedule_id INT REFERENCES %s(schedule_id) ON DELETE CASCADE,
                        status_id INT REFERENCES %s(status_id) DEFAULT 1
                    )
                    """, DBConstants.SCHEDULED_SEATS, DBConstants.SEATS, DBConstants.SCHEDULES, DBConstants.SCHEDULED_SEAT_STATUSES));

//            status BOOLEAN DEFAULT false

            stmt.executeUpdate(String.format(
                    """
                            CREATE TABLE IF NOT EXISTS %s (
                                status_id SERIAL PRIMARY KEY,
                                status_code VARCHAR(30) UNIQUE NOT NULL
                            );
                            """
                    , DBConstants.BOOKING_STATUSES));

            stmt.executeUpdate(
                    String.format(
                            """
                                    INSERT INTO %s (status_code) VALUES
                                    ('BOOKED'),
                                    ('CANCELLED_BY_USER'),
                                    ('CANCELLED_BY_ADMIN'),
                                    ('REFUNDED') ON CONFLICT DO NOTHING;
                                    """, DBConstants.BOOKING_STATUSES
                    )
            );


            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        booking_seat_id SERIAL PRIMARY KEY,
                        booking_id INT REFERENCES %s(booking_id) ON DELETE CASCADE,
                        scheduled_seat_id INT REFERENCES %s(scheduled_seat_id) ON DELETE CASCADE,
                        passenger_id INT REFERENCES %s(passenger_id),
                        UNIQUE (booking_id, scheduled_seat_id),
                        status_id INT REFERENCES booking_statuses(status_id) DEFAULT 1
                    );
                    """, DBConstants.BOOKING_SEATS, DBConstants.BOOKINGS, DBConstants.SCHEDULED_SEATS, DBConstants.PASSENGER_DETAILS));



            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        city_id INT REFERENCES %s(city_id) ON DELETE CASCADE,
                        location_id INT REFERENCES %s(location_id) ON DELETE CASCADE,
                        PRIMARY KEY (city_id, location_id)
                    );
                    
                    """, DBConstants.CITY_LOCATIONS, DBConstants.CITIES, DBConstants.LOCATIONS));

            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        stop_type_id SERIAL PRIMARY KEY,
                        stop_type_code VARCHAR(30) UNIQUE NOT NULL
                    )
                    """, DBConstants.STOP_TYPE));


            stmt.executeUpdate(String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        stop_id SERIAL PRIMARY KEY,
                        schedule_id INT REFERENCES %s(schedule_id) ON DELETE CASCADE,
                        location_id INT REFERENCES %s(location_id) ON DELETE CASCADE,
                        stop_type_id INT REFERENCES %s(stop_type_id),
                        UNIQUE(schedule_id, location_id, stop_type_id)
                    );
                    """, DBConstants.STOPS, DBConstants.SCHEDULES, DBConstants.LOCATIONS, DBConstants.STOP_TYPE));


            stmt.executeUpdate(String.format("""
                    INSERT INTO %s (status_code) VALUES ('AVAILABLE'), ('BOOKED'), ('BLOCKED') ON CONFLICT DO NOTHING
                    """, DBConstants.SCHEDULED_SEAT_STATUSES));

            stmt.executeUpdate(String.format("""
                    INSERT INTO %s (stop_type_code) VALUES ('BOARDING'), ('DROPPING') ON CONFLICT DO NOTHING
                    """, DBConstants.STOP_TYPE));


            stmt.executeUpdate(String.format("""
                        INSERT INTO %s (role_name)
                        VALUES ('ADMIN'), ('DEVELOPER'), ('USER')
                        ON CONFLICT (role_name) DO NOTHING;
                    """, DBConstants.ROLES));


            String hashedAdminPassword = PasswordUtil.hashPassword("admin123");

            String query = String.format("insert into %s (username, password) values ('admin', '%s') ON CONFLICT (username) DO NOTHING;", DBConstants.USERS, hashedAdminPassword);

            stmt.executeUpdate(query);


            String hashUserPassword = PasswordUtil.hashPassword("user");


            stmt.executeUpdate(String.format("insert into %s (username, password) values ('user', '%s') ON CONFLICT (username) DO NOTHING;", DBConstants.USERS, hashUserPassword));


            stmt.executeUpdate(String.format("""
                    insert into %s (user_id, role_id) values (1, 1)
                    ON CONFLICT (user_id, role_id) DO NOTHING;
                    """, DBConstants.USER_ROLES));

            stmt.executeUpdate(String.format("insert into %s (user_id, role_id) values (2, 3)" +
                    "    ON CONFLICT (user_id, role_id) DO NOTHING;\n", DBConstants.USER_ROLES));

            stmt.executeUpdate(String.format("insert into %s (seat_type_name) values ('SEATER'), ('SEMI_SLEEPER'), ('SLEEPER')" +
                    " ON CONFLICT DO NOTHING;", DBConstants.SEAT_TYPE));


            stmt.executeUpdate(String.format("""
                        INSERT INTO %s (city_name) VALUES
                            ('Chennai'), ('Bangalore'), ('Hyderabad'), ('Coimbatore'), ('Madurai')
                        ON CONFLICT (city_name) DO NOTHING;
                    """, DBConstants.CITIES));


            try {

                VehicleDAO<BusVehiclesDTO> busVehicleDAO = new BusVehiclesDAO();
                busVehicleDAO.addNew(createSampleBusVehicleDTO());
                busVehicleDAO.addNew(createSampleBusVehicleDTO2());
            } catch (Exception e) {
//                System.err.println(e);
//                System.out.println("duplicate bus value bro when initializing DB, No problem");
            }


            stmt.executeUpdate(String.format("""
                        INSERT INTO %s (source_city_id, destination_city_id, distance_km, estimated_time)
                        SELECT c1.city_id, c2.city_id, d.distance_km, d.estimated_time
                        FROM
                            (VALUES
                                ('Chennai', 'Bangalore', 350, '06:00'::interval),
                                ('Chennai', 'Hyderabad', 630, '11:00'::interval),
                                ('Madurai', 'Chennai', 460, '08:00'::interval),
                                ('Coimbatore', 'Chennai', 500, '09:00'::interval)
                            ) AS d(source, dest, distance_km, estimated_time)
                        JOIN %s c1 ON c1.city_name = d.source
                        JOIN %s c2 ON c2.city_name = d.dest
                        ON CONFLICT (source_city_id, destination_city_id, distance_km, estimated_time) DO NOTHING;
                    """, DBConstants.ROUTES, DBConstants.CITIES, DBConstants.CITIES));


            PreparedStatement locationStmt = conn.prepareStatement(String.format(
                    "insert into %s (location_name) values ('Gandhipuram'), ('Lakshmi Mills'), ('Guindy'), ('Tambaram'), ('Arappalayam'), ('Maattuthavani'), " +
                            "('Charminar'), ('Golconda Fort'), ('Electronic City'), ('Whitefield')\n" +
                            "ON CONFLICT (location_name) DO NOTHING;", DBConstants.LOCATIONS));
            locationStmt.executeUpdate();

            PreparedStatement cityLocStmt = conn.prepareStatement(String.format(
                    "insert into %s values (1, 3), (1, 4), (4, 1), (4, 2), (5, 5), (5, 6), (3, 7), (3, 8), (2, 9), (2, 10) ON CONFLICT (city_id, location_id) DO NOTHING;", DBConstants.CITY_LOCATIONS));
            cityLocStmt.executeUpdate();


            try {
                SchedulesDTO schedulesDTO = new SchedulesDTO();
                schedulesDTO.setRouteId(1);
                schedulesDTO.setBusId(1);
                schedulesDTO.setDepartureTime("2025-06-06T08:00");
                schedulesDTO.setArrivalTime("2025-06-06T14:00");
                schedulesDTO.setPrice(600.00);
                schedulesDTO.setJourneyDate("2025-06-06");
                schedulesDTO.setBoardingPointIds(Arrays.asList(5, 6));
                schedulesDTO.setDroppingPointIds(Arrays.asList(3, 4));
                ScheduleDAO scheduleDAO = new BusSchedulesDAO();
                scheduleDAO.addNewSchedule(schedulesDTO);

//                ------------------------------

                schedulesDTO.setRouteId(2);
                schedulesDTO.setBusId(2);
                schedulesDTO.setDepartureTime("2025-06-06T18:00:00");
                schedulesDTO.setArrivalTime("2025-06-07T05:00:00");
                schedulesDTO.setPrice(850.00);
                schedulesDTO.setJourneyDate("2025-06-06");
                schedulesDTO.setBoardingPointIds(Arrays.asList(1, 2));
                schedulesDTO.setDroppingPointIds(Arrays.asList(3, 4));
                scheduleDAO.addNewSchedule(schedulesDTO);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }


            System.out.println("Database initialization completed.");


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Database initialization failed.");
        }
    }
}