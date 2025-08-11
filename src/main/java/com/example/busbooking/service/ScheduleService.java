package com.example.busbooking.service;

import com.example.busbooking.db.DBConnection;
import com.example.busbooking.dto.base.SchedulesDTO;
import com.example.busbooking.enums.ScheduleStatus;
import com.example.busbooking.enums.ScheduledSeatStatus;
import com.example.busbooking.registry.ScheduleStatusRegistry;
import com.example.busbooking.registry.ScheduledSeatStatusRegistry;
import jakarta.ws.rs.BadRequestException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.busbooking.db.DBConstants.*;

public class ScheduleService {

    // Query to check if a booking exist for a particular schedule.
    public static final String check_booking_schedule_exist_query = String.format("""
            select b.booking_id
            from %s b
            where b.schedule_id = ?;
            """, BOOKINGS);
    // SQL to add a new schedule.
    private static final String add_schedule_query = String.format("insert into %s (route_id, bus_id, departure_time, arrival_time, price, journey_date)\n" + "values (?, ?, ?, ?, ?, ?)", SCHEDULES);
    // SQL to get the stop_type_id from stop_type_code
    private static final String get_stop_type_id_query = "SELECT stop_type_id FROM stop_type WHERE stop_type_code = ?";
    // SQL to insert location stops (BOARDING/DROPPING)
    private static final String insert_stops_query = String.format("insert into %s (schedule_id, location_id, stop_type_id) values (?, ?, ?)", STOPS);
    // SQL to get source and destination ID from route ID
    private static final String get_source_destination_from_route_id_query = String.format("select source_city_id, destination_city_id from %s where route_id = ?", ROUTES);
    // SQL to get the respective city of a location.
    private static final String get_city_from_location_id_query = String.format("select city_id from %s where location_id = ?", CITY_LOCATIONS);
    // Query to get bus ID associated with a schedule ID
    private static final String get_schedule_id_bus_id_query = String.format("select schedule_id, bus_id from %s where schedule_id = ?;", SCHEDULES);
    // Query to delete a scheduled seat.
    private static final String delete_scheduled_seats = String.format("""
            delete from %s where schedule_id = ?
            """, SCHEDULED_SEATS);
    // SQL to get all the seats associated with the bus ID from seat_grid_columns table
    // For each column of a bus ID in the SEAT_GRID_COLUMNS table their respective rows are in the SEATS table.
    private static final String get_seat_id_query = String.format("""
            select seat_id
            from %s s
            join %s sg on s.column_id = sg.column_id
            where sg.bus_id = ?
            """, SEATS, SEAT_GRID_COLUMNS);
    // SQL to insert the seats of a bus in the SCHEDULED SEATS table
    // This helps to keep track of seat status in particular to a schedule.
    private static final String insert_scheduled_seats_query = String.format("""
            insert into %s (seat_id, schedule_id)
            values (?, ?);
            """, SCHEDULED_SEATS);
    // SQL to change the scheduled seat status to another.
    private static final String update_scheduled_seat_status_query = String.format("""
            UPDATE %s SET status_id = ? where schedule_id = ?
            """, SCHEDULED_SEATS);
    // SQL TO check the status of schedule
    private static final String check_schedule_status_query = String.format("""
            select status_id from %s where schedule_id = ?
            """, SCHEDULES);

    /**
     * Function converts the stop type code (BOARDING, DROPPING etc.) to its stop type id
     *
     * @param stopTypeCode Type code (BOARDING, DROPPING etc.)
     * @return the type code id
     * @throws Exception if any error
     */
    public static Integer getStopTypeId( String stopTypeCode ) throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(get_stop_type_id_query)) {
            stmt.setString(1, stopTypeCode.toUpperCase());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stop_type_id");
                }
            }
        }
        return null;
    }


    /**
     * Validate valid schedule details
     *
     * @param schedulesDTO schedule details
     * @return a map with allowed = (true / false) and message || true denotes valid schedule values, while false denotes invalid schedule values.
     */
    public static Map<String, String> checkValidScheduleValues( SchedulesDTO schedulesDTO ) {




        Map<String, String > result = new HashMap<>();
        result.put("allowed", "false");

        // NULL check
        if (schedulesDTO == null) {
            result.put("message", "scheduleDTO is null");
            return result;
        }


        String journeyDate = schedulesDTO.getJourneyDate();
        String departureTime = schedulesDTO.getDepartureTime();
        String arrivalTime = schedulesDTO.getArrivalTime();

        try {
            java.time.LocalDate jrDate = java.time.LocalDate.parse(journeyDate);
            java.time.LocalDateTime depTime = java.time.LocalDateTime.parse(departureTime);
            java.time.LocalDateTime arrTime = java.time.LocalDateTime.parse(arrivalTime);
            java.time.LocalDateTime now = java.time.LocalDateTime.now();

            // journeyDate must be today or in the future (after today)
            if (jrDate.isBefore(java.time.LocalDate.now())) {
                result.put("message", "Journey date must be today or in the future.");
                return result;
            }

            // departureTime must be on the same date as journeyDate and in the future
            if (!depTime.toLocalDate().equals(jrDate) || !depTime.isAfter(now)) {
                result.put("message", "Departure time must be on the same date as journey date and in the future.");
                return result;
            }

            // arrivalTime must be after departureTime and in the future
            if (!arrTime.isAfter(depTime)) {
                result.put("message", "Arrival time must be after departure time and in the future.");
                return result;
            }

        } catch (Exception e) {
            result.put("message", "Parsing failed, invalid format");
            // Parsing failed, invalid format
            return result;
        }
        result.put("allowed", "true");
        result.put("message", "Valid schedule details.");
        return result;
    }



    /**
     * Create a new schedule with admin entered schedule details.
     * Generate and return the schedule ID
     *
     * @param schedulesDTO DTO of schedule details
     * @param conn         DB Connection
     * @return Generated schedule ID
     * @throws Exception if any error.
     */
    public static Integer insertSchedule( SchedulesDTO schedulesDTO, Connection conn ) throws Exception {

        Integer scheduleId = null;
        LocalDate journeyDate;
        LocalDateTime departure;
        LocalDateTime arrival;

        try {
            journeyDate = LocalDate.parse(schedulesDTO.getJourneyDate());
            departure = LocalDateTime.parse(schedulesDTO.getDepartureTime());
            arrival = LocalDateTime.parse(schedulesDTO.getArrivalTime());
        } catch (Exception e) {
            throw new BadRequestException("Invalid date or time");
        }

        try (PreparedStatement statement = conn.prepareStatement(add_schedule_query, Statement.RETURN_GENERATED_KEYS);) {
            statement.setInt(1, schedulesDTO.getRouteId());
            statement.setInt(2, schedulesDTO.getBusId());
            statement.setTimestamp(3, Timestamp.valueOf(departure));
            statement.setTimestamp(4, Timestamp.valueOf(arrival));
            statement.setDouble(5, schedulesDTO.getPrice());
            statement.setDate(6, Date.valueOf(journeyDate));
            statement.executeUpdate();

            try (ResultSet scheduleGeneratedKeys = statement.getGeneratedKeys()) {
                if (scheduleGeneratedKeys.next()) {
                    // The generated schedule ID is stored in a variable.
                    scheduleId = scheduleGeneratedKeys.getInt(1);
                }
            }
        }

        return scheduleId;

    }

    /**
     * Inserts stop details (either boarding or dropping) for a schedule.
     *
     * @param connection  JDBC connection
     * @param scheduleId  the schedule's ID
     * @param type        "BOARDING" or "DROPPING"
     * @param locationIds list of location IDs to be inserted
     * @param routeId     route ID to validate city associations
     * @throws Exception if stop cities don't match route cities
     */

    public static void addStops( Connection connection, int scheduleId, String type, List<Integer> locationIds, int routeId ) throws Exception {

        // Check for locations ID's are present
        if (locationIds==null || locationIds.isEmpty()) {
            throw new Exception("Empty boarding / dropping point location ID's");
        }

        Integer required_city_id = null;
        Integer stop_type = ScheduleService.getStopTypeId(type);

        // Any stop location going to be added in the schedule must be a part of the source city or destination city based on the
        // type (BOARDING / DROPPING)

        try (PreparedStatement statement = connection.prepareStatement(get_source_destination_from_route_id_query);) {
            statement.setInt(1, routeId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    if (type.equals("BOARDING")) {
                        required_city_id = rs.getInt("source_city_id");
                    } else {
                        required_city_id = rs.getInt("destination_city_id");
                    }
                }
            }
        }


        try (PreparedStatement stopStatement = connection.prepareStatement(insert_stops_query);
             PreparedStatement cityStatement = connection.prepareStatement(get_city_from_location_id_query);) {

            // For each location ID, add the location in the stops table.
            for (Integer locationId : locationIds) {

                cityStatement.setInt(1, locationId);
                try (ResultSet rs = cityStatement.executeQuery()) {

                    // If the associated city of a location is not the same as required city then throws exception
                    if (rs.next()) {
                        if (required_city_id != null && rs.getInt("city_id") != required_city_id) {
                            throw new BadRequestException();
                        }
                    }
                }

                stopStatement.setInt(1, scheduleId);
                stopStatement.setInt(2, locationId);
                stopStatement.setInt(3, stop_type);
                stopStatement.addBatch();
            }
            stopStatement.executeBatch();
        }
    }


    /**
     * To validate if any user made any booking for a schedule.
     *
     * @param scheduleId schedule ID
     * @param conn       DB connection
     * @return true if booking has already been made, else false
     * @throws Exception if any error.
     */

    public static boolean hasBookingsForSchedule( int scheduleId, Connection conn ) throws Exception {
        try (PreparedStatement statement = conn.prepareStatement(check_booking_schedule_exist_query);) {
            statement.setInt(1, scheduleId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
            return false;
        }
    }


    /**
     * Get the bus ID associated with the schedule ID
     *
     * @param scheduleId schedule ID to find the bus ID
     * @param conn       DB Connection
     * @return the bus ID if found, else returns null
     * @throws Exception if any error.
     */

    public static Integer getBusIdByScheduleId( int scheduleId, Connection conn ) throws Exception {
        try (PreparedStatement busIdStatement = conn.prepareStatement(get_schedule_id_bus_id_query)) {
            busIdStatement.setInt(1, scheduleId);
            try (ResultSet rs = busIdStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("bus_id");
                }
            }
            return null;
        }

    }


    /**
     * Updates the scheduled seats if the bus associated with the schedule has changed.
     * <p>
     * This method first checks whether the existing bus ID differs from the updated bus ID.
     * If they are different, it performs the following actions:
     * - Deletes all previously scheduled seats for the schedule (as they are tied to the old bus layout).
     * - Adds a new set of scheduled seats corresponding to the new bus.
     *
     * @param existingBusId The bus ID currently associated with the schedule in the database.
     * @param updateBusId   The bus ID submitted by the user for updating the schedule.
     * @param schedulesDTO  The schedule details including schedule ID and updated information.
     * @param conn          The database connection to be used for executing SQL operations.
     * @throws Exception If a database error occurs during the update process.
     */

    public static void updateScheduledSeatsIfBusChanged( int existingBusId, int updateBusId, SchedulesDTO schedulesDTO, Connection conn ) throws Exception {
        try (
                PreparedStatement deleteStatement = conn.prepareStatement(delete_scheduled_seats);
        ) {
            if (existingBusId != updateBusId) {
                deleteStatement.setInt(1, schedulesDTO.getScheduleId());
                deleteStatement.executeUpdate();


                addScheduledSeats(schedulesDTO, conn);
            }
        }
    }


    /**
     * Adds all seat entries for a bus into the scheduled_seats table for a specific schedule
     * This links each physical seat with the given schedule, initializing them as unbooked
     *
     * @param schedulesDTO schedule data with bus ID and generated schedule ID
     * @param conn         JDBC connection
     * @throws Exception in case of SQL errors
     */

    public static void addScheduledSeats( SchedulesDTO schedulesDTO, Connection conn ) throws Exception {
        PreparedStatement statement = null, insertScheduledSeatsStatement = null;
        try {
            // Fetch all seat IDs associated with the bus
            statement = conn.prepareStatement(get_seat_id_query);
            statement.setInt(1, schedulesDTO.getBusId());
            try (ResultSet rs = statement.executeQuery()) {

                // Prepare to insert those seats into the scheduled_seats table for the current schedule
                insertScheduledSeatsStatement = conn.prepareStatement(insert_scheduled_seats_query);

                while (rs.next()) {
                    insertScheduledSeatsStatement.setInt(1, rs.getInt("seat_id"));
                    insertScheduledSeatsStatement.setInt(2, schedulesDTO.getScheduleId());
                    insertScheduledSeatsStatement.addBatch();
                }
            }
            insertScheduledSeatsStatement.executeBatch();

        } finally {

            DBConnection.closePreparedStatement(statement);
            DBConnection.closePreparedStatement(insertScheduledSeatsStatement);

        }
    }

    /**
     * Validates if a schedule is active or not using its schedule ID;
     *
     * @param scheduleId schedule ID
     * @param conn       DB Connection
     * @return false if the schedule is not active, true if active
     * @throws Exception if any error.
     */
    public static boolean validateScheduleIsActive( int scheduleId, Connection conn ) throws Exception {
        try (PreparedStatement checkScheduleStatusStatement = conn.prepareStatement(check_schedule_status_query)) {
            checkScheduleStatusStatement.setInt(1, scheduleId);

            try (ResultSet checkScheduleStatusRs = checkScheduleStatusStatement.executeQuery()) {
                if (checkScheduleStatusRs.next()) {
                    if (checkScheduleStatusRs.getInt(1) != ScheduleStatusRegistry.getByCode(ScheduleStatus.ACTIVE.name()).getStatusId()) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }
    }


    /**
     * The method updates all the scheduled seats status of a schedule ID to BLOCKED
     *
     * @param scheduleId schedule ID
     * @param conn       DB Connection
     * @throws Exception if any error.
     */
    public static void updateScheduledSeatsStatusToBlocked( int scheduleId, Connection conn ) throws Exception {
        try (PreparedStatement statement = conn.prepareStatement(update_scheduled_seat_status_query)) {
            statement.setInt(1, ScheduledSeatStatusRegistry.getByCode(ScheduledSeatStatus.BLOCKED.name()).getStatusId());
            statement.setInt(2, scheduleId);
            statement.executeUpdate();
        }
    }
}