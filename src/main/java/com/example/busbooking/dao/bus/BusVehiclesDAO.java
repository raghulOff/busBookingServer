package com.example.busbooking.dao.bus;

import com.example.busbooking.dao.base.VehicleDAO;
import com.example.busbooking.db.DBConnection;
import com.example.busbooking.db.DBConstants;
import com.example.busbooking.dto.bus.BusSearchRequestDTO;
import com.example.busbooking.dto.bus.BusSearchResponseDTO;
import com.example.busbooking.dto.bus.BusVehiclesDTO;
import com.example.busbooking.service.AddBusService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import org.postgresql.util.PSQLException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.busbooking.db.DBConnection.rollbackConnection;
import static com.example.busbooking.db.DBConstants.*;


/**
 * DAO implementation for managing bus-related data such as adding, updating,
 * deleting, and retrieving buses and their availability.
 */

public class BusVehiclesDAO implements VehicleDAO<BusVehiclesDTO> {

    // SQL to get available buses for a source, destination and DOJ
    private static final String src_destination_bus_query = String.format("""
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
                FROM %s s
                JOIN %s r ON s.route_id = r.route_id
                JOIN %s b ON s.bus_id = b.bus_id
                JOIN %s c1 ON r.source_city_id = c1.city_id
                JOIN %s c2 ON r.destination_city_id = c2.city_id
                WHERE 
                    c1.city_name = ? AND 
                    c2.city_name = ? AND 
                    s.journey_date = ? AND
                    s.status_id = 1
            """, SCHEDULES, ROUTES, BUSES, CITIES, CITIES);

    // SQL to get all the available buses in the DB
    private static final String get_all_buses_query = String.format("select bus_id, bus_number, bus_type, total_columns, operator_name from %s", BUSES);

    // SQL to delete a bus
    private static final String delete_bus_query = String.format("delete from %s where bus_id = ?", BUSES);

    // SQL to update a bus
    private static final String update_bus_query = String.format("update %s set bus_number = ?, bus_type = ?, total_seats = ?, operator_name = ? where bus_id = ?", BUSES);

    // SQL to check if a bus exist.
    private static final String check_bus_exist = String.format("select bus_id from %s where bus_id = ?", BUSES);



    /**
     * Retrieves a list of available buses for a specific source, destination,
     * and date of journey.
     *
     * @param busSearchRequestDTO contains the source, destination, and journey date
     * @return Response containing a list of matching buses or appropriate error message
     * @throws Exception if any error occurs
     */
    public static Response getAvailableBuses( BusSearchRequestDTO busSearchRequestDTO ) throws Exception {
        // NULL check
        if (busSearchRequestDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
        }

        String from = busSearchRequestDTO.getFrom();
        String to = busSearchRequestDTO.getTo();
        String doj = busSearchRequestDTO.getDoj();

        // Valid parameter check
        if (from == null || to == null || doj == null || from.isEmpty() || to.isEmpty() || doj.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
        }

        // List to return the bus search response based on the input parameters (from, to, doj)
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

                    // Adds all the available buses with details for a specific (source, destination and DOJ)
                    searchResponseList.add(new BusSearchResponseDTO(scheduleId, busNumber, busType, departureTime,
                            arrivalTime, price, operatorName, distanceKm, estimatedTime));

                }
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong.").build();
            }

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Something went wrong.").build();
        }
        return Response.ok(searchResponseList).build();
    }


    /**
     * Fetches all buses from the database with basic details such as bus number,
     * type, operator, and layout information.
     *
     * @return Response with a list of {@link BusVehiclesDTO} objects
     * @throws Exception if any database operation fails
     */

    public Response getAll() throws Exception {

        // List to store and return all the available buses in the DB
        List<BusVehiclesDTO> allBuses = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(get_all_buses_query);) {


            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    int busId = rs.getInt("bus_id");
                    String busNumber = rs.getString("bus_number");
                    String busType = rs.getString("bus_type");
                    int totalColumns = rs.getInt("total_columns");
                    String op_name = rs.getString("operator_name");

                    // Adds all the available buses with details.
                    allBuses.add(new BusVehiclesDTO(busNumber, totalColumns, op_name, busId, busType));
                }
            }
        }

        return Response.ok().entity(allBuses).build();

    }


    /**
     * Deletes a bus from the database if it is not currently assigned to a schedule.
     * If assigned, deletion is not allowed (foreign key constraint).
     *
     * @param busId the ID of the bus to delete
     * @return Response indicating success, not found, or constraint violation
     * @throws Exception if a SQL error occurs
     */

     public Response delete( int busId ) throws Exception {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkBusExistStatement = conn.prepareStatement(check_bus_exist);
             PreparedStatement statement = conn.prepareStatement(delete_bus_query);) {

            // Check if the bus exists or not.

            checkBusExistStatement.setInt(1, busId);
            try (ResultSet rs = checkBusExistStatement.executeQuery()) {

                // Return if the bus doesn't exist.
                if (!rs.next()) {
                    return Response.status(Response.Status.NOT_FOUND).entity("No bus with this bus_id found").build();
                }
            }

            // DELETE the bus.
            statement.setInt(1, busId);
            statement.executeUpdate();

        } catch (SQLException e) {
            // FOREIGN KEY VIOLATION - if this bus is assigned with a schedule, then the schedule must be deleted or modified first
            // before deleting this bus.
            if (FOREIGN_KEY_VIOLATION.equals(e.getSQLState())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("This bus has been assigned a schedule. Please access the schedule section to modify it.")
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("An internal error occurred while deleting the bus.")
                        .build();
            }
        }
        return Response.ok("Bus deleted").build();
    }

    /**
     * Updates the details of an existing bus (number, type, operator, total seats).
     *
     * @param busVehicleDTO DTO containing the new values for the bus
     * @return Response indicating update success or failure
     * @throws Exception if database error occurs or bus doesn't exist
     */

    public Response update( BusVehiclesDTO busVehicleDTO ) throws Exception {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkBusExistStatement = conn.prepareStatement(check_bus_exist);
             PreparedStatement statement = conn.prepareStatement(update_bus_query);) {

            // Check if the bus exists.

            checkBusExistStatement.setInt(1, busVehicleDTO.getBusId());

            try (ResultSet rs = checkBusExistStatement.executeQuery()) {

                // Return if the bus is not found.
                if (!rs.next()) {
                    return Response.status(Response.Status.NOT_FOUND).entity("No bus with this bus_id found").build();
                }
            }

            // Replace the new bus details over the old bus details.

            statement.setString(1, busVehicleDTO.getVehicleNumber());
            statement.setString(2, busVehicleDTO.getBusType());
            statement.setInt(3, busVehicleDTO.getTotalSeats());
            statement.setString(4, busVehicleDTO.getOperatorName());
            statement.setInt(5, busVehicleDTO.getBusId());
            statement.executeUpdate();

        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error!").build();
        }
        return Response.ok("Update Success!").build();
    }


    /**
     * Adds a new bus along with its seat layout and column grid.
     * Performs the following steps transactionally:
     *     Insert new bus into the BUSES table
     *     Insert seat grid column structure
     *     Add all seats according to layout and bus type
     *
     * @param busVehicleDTO contains all necessary details to register a bus
     * @return Response indicating success or type of failure
     * @throws Exception if a rollback or SQL issue occurs
     */

    public Response addNew( BusVehiclesDTO busVehicleDTO ) throws Exception {
        // Null check
        if (busVehicleDTO==null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input.").build();
        }
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // add a new bus in the buses table and returns the generated bus id;
            int busId = AddBusService.addNewBus(conn, busVehicleDTO);

            // this stores the details of no of rows for each column in the bus;
            AddBusService.addSeatGridColumns(conn, busVehicleDTO, busId);

            // adds all the seats for the bus with seat type received from the user.
            AddBusService.addSeats(conn, busVehicleDTO, busId);

            conn.commit();

        } catch (PSQLException e) {

            rollbackConnection(conn);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Database error. Invalid request.")
                    .build();

        } catch (BadRequestException e) {

            rollbackConnection(conn);
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input.").build();

        } catch (Exception e) {

            rollbackConnection(conn);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Something went wrong.").build();

        } finally {
            if (conn != null) conn.close();
        }

        return Response.ok("Bus added.").build();
    }





}
