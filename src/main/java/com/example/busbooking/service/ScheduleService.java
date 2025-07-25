package com.example.busbooking.service;

import com.example.busbooking.db.DBConnection;
import com.example.busbooking.dto.base.SchedulesDTO;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.busbooking.db.DBConstants.SCHEDULES;

public class ScheduleService {

    // SQL to add a new schedule.
    private static final String add_schedule_query = String.format("insert into %s (route_id, bus_id, departure_time, arrival_time, price, journey_date)\n" + "values (?, ?, ?, ?, ?, ?)", SCHEDULES);


    private static final String get_stop_type_id_query = "SELECT stop_type_id FROM stop_type WHERE stop_type_code = ?";

    /**
     * Function converts the stop type code (BOARDING, DROPPING etc.) to its stop type id
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
     * Validates whether the schedule input values are acceptable.
     */
    public static boolean checkValidScheduleValues( SchedulesDTO schedulesDTO ) {

        // NULL check
        if (schedulesDTO == null) {
            return true;
        }

        Integer routeId = schedulesDTO.getRouteId();
        Integer busId = schedulesDTO.getBusId();
        Double price = schedulesDTO.getPrice();
        String journeyDate = schedulesDTO.getJourneyDate();
        String departureTime = schedulesDTO.getDepartureTime();
        String arrivalTime = schedulesDTO.getArrivalTime();

        return journeyDate == null || departureTime == null || arrivalTime == null || journeyDate.isEmpty() ||
                departureTime.isEmpty() || arrivalTime.isEmpty() || routeId == null ||
                busId == null || price == null || price <= 0 || price >= 20000;
    }

    public static Integer createScheduleWithGeneratedId( SchedulesDTO schedulesDTO, LocalDate journeyDate, LocalDateTime departure, LocalDateTime arrival, Connection conn) throws Exception {
        Integer scheduleId = null;
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
}
