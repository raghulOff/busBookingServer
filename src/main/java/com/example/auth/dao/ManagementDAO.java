package com.example.auth.dao;

import com.example.auth.db.DBConnection;
import com.example.auth.dto.BusDTO;
import com.example.auth.dto.CityDTO;
import com.example.auth.dto.RouteDTO;
import com.example.auth.dto.ScheduleDTO;
import jakarta.ws.rs.core.Response;
import org.postgresql.util.PGInterval;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class ManagementDAO {
    public static Response addNewBus( BusDTO busDto ) throws Exception {
        String query = "insert into buses (bus_number, bus_type, total_seats, operator_name) values (?, ?, ?, ?) ";
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, busDto.getBusNo());
            statement.setString(2, busDto.getBusType());
            statement.setInt(3, busDto.getTotalSeats());
            statement.setString(4, busDto.getOperatorName());

            statement.executeUpdate();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("bus already exist").build();
        }
        return Response.ok("new bus added").build();
    }

    public static Response addNewCity( CityDTO cityDTO ) {
        String query = "insert into cities (city_name) values (?)";
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, cityDTO.getCityName());
            System.out.println(statement.executeUpdate());


        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("city already exist").build();
        }
        return Response.status(Response.Status.CREATED).entity("city added").build();

    }

    public static Response addNewSchedule( ScheduleDTO scheduleDTO ) throws Exception {
        LocalDate journeyDate = LocalDate.parse(scheduleDTO.getJourneyDate());
        LocalDateTime departure = LocalDateTime.parse(scheduleDTO.getDepartureTime());
        LocalDateTime arrival = LocalDateTime.parse(scheduleDTO.getArrivalTime());
        String query = "insert into schedules (route_id, bus_id, departure_time, arrival_time, available_seats, price, journey_date)\n" +
                "values (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, scheduleDTO.getRouteId());
            statement.setInt(2, scheduleDTO.getBusId());
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(departure));
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(arrival));
            statement.setInt(5, scheduleDTO.getAvailableSeats());
            statement.setDouble(6, scheduleDTO.getPrice());
            statement.setDate(7, java.sql.Date.valueOf(journeyDate));

            statement.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
            return Response.status(Response.Status.CONFLICT).entity("can't insert new schedule").build();
        }
        return Response.status(Response.Status.CREATED).entity("Schedule created").build();
    }


    public static Response addNewRoute( RouteDTO routeDTO ) throws SQLException {
        String query = "insert into routes (source_city_id, destination_city_id, distance_km, estimated_time)\n" +
                "                values ((select city_id from cities where city_name = ?),\n" +
                "\t\t\t\t(select city_id from cities where city_name = ?),\n" +
                "\t\t\t\t?, ?)";


        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, routeDTO.getSource());
            statement.setString(2, routeDTO.getDestination());
            statement.setInt(3, routeDTO.getDistanceKm());
            statement.setObject(4, new PGInterval(routeDTO.getEstimatedTime()));

            statement.executeUpdate();

        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("Can't create route").build();
        }
        return Response.status(Response.Status.CREATED).entity("Route added").build();
    }
}

