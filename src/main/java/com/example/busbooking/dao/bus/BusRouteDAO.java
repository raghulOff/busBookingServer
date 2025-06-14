package com.example.busbooking.dao.bus;

import com.example.busbooking.dao.base.RouteDAO;
import com.example.busbooking.db.DBConnection;
import com.example.busbooking.dto.base.RouteDTO;
import jakarta.ws.rs.core.Response;
import org.postgresql.util.PGInterval;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BusRouteDAO implements RouteDAO {

    public static final String get_all_routes_query = "SELECT \n" +
            "    r.route_id," +
            "    c1.city_name AS source_city,\n" +
            "    c2.city_name AS destination_city,\n" +
            "    r.distance_km,\n" +
            "    r.estimated_time\n" +
            "FROM routes r\n" +
            "JOIN cities c1 ON r.source_city_id = c1.city_id\n" +
            "JOIN cities c2 ON r.destination_city_id = c2.city_id;\n";

    public static final String add_route_query = "insert into routes (source_city_id, destination_city_id, distance_km, estimated_time)\n" +
            "                values ((select city_id from cities where city_name = ?),\n" +
            "\t\t\t\t(select city_id from cities where city_name = ?),\n" +
            "\t\t\t\t?, ?)";

    public static final String delete_route_query = "delete from routes where route_id=?";

    public static final String update_route_query = "UPDATE routes\n" +
            "SET\n" +
            "    source_city_id = (SELECT city_id FROM cities WHERE city_name = ?),\n" +
            "    destination_city_id = (SELECT city_id FROM cities WHERE city_name = ?),\n" +
            "    distance_km = ?,\n" +
            "    estimated_time = ?\n" +
            "WHERE route_id = ?;";

    public List<RouteDTO> getRoutes() throws Exception {
        List<RouteDTO> allroutes = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(get_all_routes_query);
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

    public Response addNewRoute( RouteDTO routeDTO ) throws SQLException {



        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(add_route_query);
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


    public Response deleteRoute(int routeId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(delete_route_query);
            statement.setInt(1, routeId);
            statement.executeUpdate();
        } catch (Exception e) {
            System.err.println(e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok("Delete success").build();
    }

    public Response updateRoute(RouteDTO routeDTO) throws Exception {


        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(update_route_query);
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