package com.example.busbooking.dao.bus;

import com.example.busbooking.dao.base.VehicleDAO;
import com.example.busbooking.db.DBConnection;

import com.example.busbooking.dto.bus.BusSearchResponseDTO;
import com.example.busbooking.dto.bus.BusVehicleDTO;
import com.example.busbooking.service.AddBusService;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BusVehicleDAO implements VehicleDAO {
    public static final String src_destination_bus_query = """
            SELECT 
                s.schedule_id,
                b.bus_number,
                b.bus_type,
                s.departure_time,
                s.arrival_time,
                s.price,
                b.operator_name,
                r.distance_km,
                r.estimated_time
            FROM schedules s
            JOIN routes r ON s.route_id = r.route_id
            JOIN buses b ON s.bus_id = b.bus_id
            JOIN cities c1 ON r.source_city_id = c1.city_id
            JOIN cities c2 ON r.destination_city_id = c2.city_id
            WHERE 
                c1.city_name = ? AND 
                c2.city_name = ? AND 
                s.journey_date = ?
        """;

    public static final String get_all_buses_query = "select bus_id, bus_number, bus_type, total_columns, operator_name from buses";
    public static final String delete_bus_query = "delete from buses where bus_id = ?";
    public static final String update_bus_query = "update buses set bus_number = ?, bus_type = ?, total_seats = ?, operator_name = ? where bus_id = ?";


    // returns the available buses particular to source, destination and date of journey.
    public static List<BusSearchResponseDTO> getAvailableBuses( String from, String to, String doj) throws Exception {
        List<BusSearchResponseDTO> searchResponseList = new ArrayList<>();


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(src_destination_bus_query)) {

            stmt.setString(1, from);
            stmt.setString(2, to);
            stmt.setDate(3, Date.valueOf(doj)); // ensure `doj` is in yyyy-MM-dd format

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int scheduleId = rs.getInt("schedule_id");
                    String busNumber = rs.getString("bus_number");
                    String busType = rs.getString("bus_type");
                    String departureTime = rs.getString("departure_time");
                    String arrivalTime = rs.getString("arrival_time");
                    BigDecimal price = rs.getBigDecimal("price");
                    String operatorName = rs.getString("operator_name");
                    int distanceKm = rs.getInt("distance_km");
                    String estimatedTime = rs.getString("estimated_time");

                    searchResponseList.add(new BusSearchResponseDTO(scheduleId, busNumber, busType, departureTime,
                            arrivalTime, price, operatorName, distanceKm, estimatedTime));

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResponseList;
    }


    // returns all the buses available.
    public List<BusVehicleDTO> getAll() throws Exception {
        List<BusVehicleDTO> allBuses = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(get_all_buses_query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int busId = rs.getInt("bus_id");
                String busNumber = rs.getString("bus_number");
                String busType = rs.getString("bus_type");
                int totalColumns = rs.getInt("total_columns");
                String op_name = rs.getString("operator_name");
                allBuses.add(new BusVehicleDTO(busNumber, totalColumns, op_name, busId, busType));
            }
        } catch (Exception e) {
            throw e;
        }
        return allBuses;
    }



    // deletes an existing bus
    public Response delete(int busId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(delete_bus_query);
            statement.setInt(1, busId);
            statement.executeUpdate();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).entity("This bus has been assigned a schedule. Please access the schedule section to modify it.").build();
        }
        return Response.ok("Bus deleted").build();
    }

    // updates an existing bus.
    public Response update( BusVehicleDTO busVehicleDTO) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(update_bus_query);
            statement.setString(1, busVehicleDTO.getVehicleNumber());
            statement.setString(2, busVehicleDTO.getBusType());
            statement.setInt(3, busVehicleDTO.getTotalSeats());
            statement.setString(4, busVehicleDTO.getOperatorName());
            statement.setInt(5, busVehicleDTO.getBusId());
            statement.executeUpdate();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Update Unsuccessful. Try again!").build();
        }
        return Response.ok("Update Success!").build();
    }




    // adding a new bus
    public Response addNew( BusVehicleDTO busVehicleDTO ) throws Exception {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // add a new bus in the buses table and returns the generated bus id;
            int busId = AddBusService.addNewBus(conn, busVehicleDTO);

            // this stores the details of no of rows for each column in the bus;
            AddBusService.addSeatGridColumns(conn, busVehicleDTO, busId);

            // adds all the seats for the bus with bus type received from the user.
            AddBusService.addSeats(conn, busVehicleDTO, busId);

            conn.commit();

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Roll back if error occurs
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw e;
        } finally {

            if (conn != null) conn.close();
        }
        return Response.ok().build();
    }

}
