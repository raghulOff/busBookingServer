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


}
