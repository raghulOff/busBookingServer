package com.example.auth.dao;

import com.example.auth.db.DBConnection;
import com.example.auth.dto.RouteDTO;
import jakarta.ws.rs.core.Response;
import org.postgresql.util.PGInterval;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RouteDAO {
    public static List<RouteDTO> getRoutes() throws Exception {
        String query = "SELECT \n" +
                "    r.route_id," +
                "    c1.city_name AS source_city,\n" +
                "    c2.city_name AS destination_city,\n" +
                "    r.distance_km,\n" +
                "    r.estimated_time\n" +
                "FROM routes r\n" +
                "JOIN cities c1 ON r.source_city_id = c1.city_id\n" +
                "JOIN cities c2 ON r.destination_city_id = c2.city_id;\n";
        List<RouteDTO> allroutes = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int route_id = rs.getInt("route_id");
                String src = rs.getString("source_city");
                String dest = rs.getString("destination_city");
                int distance = rs.getInt("distance_km");
                String est_time = rs.getString("estimated_time");
                allroutes.add(new RouteDTO(route_id, src, dest, distance, est_time));
            }
        } catch (Exception e) {
            throw e;
        }
        return allroutes;
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


    public static Response deleteRoute(int routeId) throws Exception {
        String query = "delete from routes where route_id=?";
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, routeId);
            statement.executeUpdate();
        } catch (Exception e) {
            System.err.println(e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok("Delete success").build();
    }

    public static Response updateRoute(RouteDTO routeDTO) throws Exception {
        String query = "UPDATE routes\n" +
                "SET\n" +
                "    source_city_id = (SELECT city_id FROM cities WHERE city_name = ?),\n" +
                "    destination_city_id = (SELECT city_id FROM cities WHERE city_name = ?),\n" +
                "    distance_km = ?,\n" +
                "    estimated_time = ?\n" +
                "WHERE route_id = ?;";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, routeDTO.getSource());
            statement.setString(2, routeDTO.getDestination());
            statement.setInt(3, routeDTO.getDistanceKm());
            statement.setObject(4, new PGInterval(routeDTO.getEstimatedTime()));
            statement.setInt(5, routeDTO.getRouteId());

            statement.executeUpdate();
        } catch (Exception e) {
            System.err.println(e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok("Update success").build();
    }

}