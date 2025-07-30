package com.example.busbooking.service;

import com.example.busbooking.dto.base.RoutesDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.example.busbooking.db.DBConstants.SCHEDULES;

public class RouteService {

    private static final String check_route_schedule_exist_query = String.format("""
            select schedule_id
            from %s where route_id = ?;
            """, SCHEDULES);
    /**
     * Validates if a route is assigned to a schedule
     *
     * @param conn    DB connection
     * @param routeId route ID to check if it is assigned with a schedule
     * @return true if route is assigned with a schedule else false
     * @throws Exception if any error
     */
    public static boolean isRouteAssignedToSchedule( Connection conn, int routeId ) throws Exception {
        try (PreparedStatement checkRouteScheduleExistStatement = conn.prepareStatement(check_route_schedule_exist_query);) {
            checkRouteScheduleExistStatement.setInt(1, routeId);
            try (ResultSet routeScheduleRs = checkRouteScheduleExistStatement.executeQuery()) {
                return routeScheduleRs.next();
            }
        }
    }


    /**
     * Validates the route DTO for nulls, empty values, and logical errors.
     *
     * @param routesDTO DTO to validate
     * @return true if invalid; false otherwise
     */

    public static boolean checkValidRouteDTOValues( RoutesDTO routesDTO ) {

        // NULL check
        if (routesDTO == null) {
            return true;
        }


        // RouteDTO VALIDATION
        String source = routesDTO.getSource();
        String destination = routesDTO.getDestination();
        Integer distanceKm = routesDTO.getDistanceKm();
        String estimatedTime = routesDTO.getEstimatedTime();

        return source == null || destination == null || estimatedTime == null || source.isEmpty() || destination.isEmpty() || estimatedTime.isEmpty() || distanceKm == null || distanceKm < 0 || source.equals(destination);
    }
}
