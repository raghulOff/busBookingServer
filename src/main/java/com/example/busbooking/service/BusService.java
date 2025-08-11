package com.example.busbooking.service;

import com.example.busbooking.db.DBConnection;
import com.example.busbooking.dto.bus.BusVehiclesDTO;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.busbooking.db.DBConstants.*;


/**
 * Service class responsible for adding a new bus and managing its related seat grid,
 * seat details, and seat types.
 */

public class BusService {

    // SQL to add a new bus into BUSES table.
    private static final String insert_into_buses_table_query = String.format("insert into %s (bus_number, operator_name, bus_type, total_columns)" +
            "\nvalues (?, ?, ?, ?) ON CONFLICT (bus_number) DO NOTHING", BUSES);

    // SQL to add the total rows for each column of a bus in the SEAT_GRID_COLUMNS table.
    private static final String insert_into_seat_grid_columns_query = String.format("insert into %s " +
            "(bus_id, col_number, total_rows, pos) values (?, ?, ?, ?) ON CONFLICT (bus_id, col_number, pos) DO NOTHING", SEAT_GRID_COLUMNS);

    // SQL to retrieve the total rows of each column in the bus seat layout.
    private static final String get_column_id_query = String.format("select column_id, total_rows from %s where bus_id = ? and col_number = ? and pos = ?", SEAT_GRID_COLUMNS);

    // SQL to insert a seat with seat details into the SEATS table assigned with a bus.
    private static final String insert_seat_query = String.format("insert into %s (column_id, row_number, seat_type_id, seat_number) values (?, ?, ?, ?);", SEATS);

    // SQL to get all the seat types.
    private static final String get_seat_types_query = String.format("select seat_type_id, seat_type_name from %s", SEAT_TYPE);

    // SQL to check if a bus is assigned with a schedule.
    private static final String check_bus_assigned_to_schedule_query = String.format("""
            select schedule_id from %s where bus_id = ?;
            """, SCHEDULES);

    /**
     * Inserts a new bus into the database.
     *
     * @param conn          JDBC connection object
     * @param busVehicleDTO DTO containing the new bus details
     * @return The generated bus ID if insertion is successful
     * @throws Exception if the input is invalid or insertion fails
     */

    public static Integer addNewBus( Connection conn, BusVehiclesDTO busVehicleDTO ) throws Exception {

//        String vehicleNumber = busVehicleDTO.getVehicleNumber();
//        String operatorName = busVehicleDTO.getOperatorName();
//        String busType = busVehicleDTO.getBusType();
//        Integer totalColumns = busVehicleDTO.getTotalColumns();
//
//        // Check for valid parameters.
//        if (vehicleNumber == null || operatorName == null || busType == null || totalColumns == null
//                || vehicleNumber.isEmpty() || operatorName.isEmpty() || busType.isEmpty() || totalColumns == 0 || totalColumns > 6) {
//            throw new BadRequestException("Invalid input");
//        }

        // Add the bus details into the BUSES table.
        try (PreparedStatement statement = conn.prepareStatement(insert_into_buses_table_query, Statement.RETURN_GENERATED_KEYS);) {

            statement.setString(1, busVehicleDTO.getVehicleNumber());
            statement.setString(2, busVehicleDTO.getOperatorName());
            statement.setString(3, busVehicleDTO.getBusType());
            statement.setInt(4, busVehicleDTO.getTotalColumns());

            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            return null;
        }

    }


    /**
     * Inserts seat grid columns for the bus based on user-defined seat structure.
     * The admin/dev decides the number of seat columns must exist in the bus
     * and for each column the total no of rows are given by the admin/dev
     *
     * @param conn          JDBC connection
     * @param busVehicleDTO DTO containing the seat grid layout
     * @param busId         ID of the newly added bus
     * @throws Exception if validation fails or insertion fails
     */
    public static void addSeatGridColumns( Connection conn, BusVehiclesDTO busVehicleDTO, int busId ) throws Exception {


        try (PreparedStatement statement = conn.prepareStatement(insert_into_seat_grid_columns_query);) {

//            // Check for valid parameters
//            if (busVehicleDTO.getSeatGridCount() == null || busVehicleDTO.getSeatGridCount().isEmpty()) {
//                throw new BadRequestException("Invalid input.");
//            }

            // Traversing through each column of the bus seat layout and getting the total no of rows for each column.
            for (BusVehiclesDTO.SeatGridCount seatGridCount : busVehicleDTO.getSeatGridCount()) {
//                Integer colNumber = seatGridCount.getCol_number();
//                Integer totalRows = seatGridCount.getTotal_rows();
//                String pos = seatGridCount.getPos();
//
//                // Check for valid parameters.
//                if (colNumber == null || totalRows == null || pos == null || colNumber > busVehicleDTO.getTotalColumns() || colNumber < 1
//                        || totalRows < 0 || totalRows > 15 || (!pos.equals("UPPER") && !pos.equals("LOWER"))) {
//                    throw new BadRequestException("Invalid input");
//                }

                // For each column, total rows that column holds is inserted.
                statement.setInt(1, busId);
                statement.setInt(2, seatGridCount.getCol_number());
                statement.setInt(3, seatGridCount.getTotal_rows());
                statement.setString(4, seatGridCount.getPos());
                statement.executeUpdate();

            }
        }
    }

    /**
     * Inserts all individual seats for the bus based on the seat grid columns.
     * Adds all the seats of the bus including the details of seat (row_number, col_number, seat_type etc.)
     *
     * @param conn          JDBC connection
     * @param busVehicleDTO DTO containing seat details
     * @param busId         ID of the bus
     * @throws Exception if input is invalid or insertion fails
     */

    public static void addSeats( Connection conn, BusVehiclesDTO busVehicleDTO, int busId ) throws Exception {

        // Check for valid parameters.
        if (busVehicleDTO.getSeatDetails() == null || busVehicleDTO.getSeatDetails().isEmpty()) {
            throw new BadRequestException("Invalid input.");
        }

        // Traversing through all the seats and retrieving the seat details given by the admin/dev (eg: row_number, col_number, pos, seat_type etc..)
        for (BusVehiclesDTO.SeatDetails seatDetails : busVehicleDTO.getSeatDetails()) {
            int colId = 0;
            Integer cols = seatDetails.getCol_number();
            Integer rows = seatDetails.getRow_number();
            Integer totalCols = busVehicleDTO.getTotalColumns();

            // Selecting column ID from SEAT_GRID_COLUMNS using the seat's bus ID, col number and position.
            try (PreparedStatement colStatement = conn.prepareStatement(get_column_id_query);) {

                colStatement.setInt(1, busId);
                colStatement.setInt(2, seatDetails.getCol_number());
                colStatement.setString(3, seatDetails.getPos());

                try (ResultSet rs = colStatement.executeQuery()) {

                    // if the column exists then assign the column_id to variable `colId`
                    // and this colId is used to insert as a reference to SEAT_GRID_COLUMNS in the SEATS table for each and every seat.
                    if (rs.next()) {
                        colId = rs.getInt("column_id");
                    }
                    // Check for valid parameters.
                    if (cols == null || rows == null || totalCols == null || (cols > totalCols || cols < 1) || (rows > rs.getInt("total_rows") || rows < 1)) {
                        throw new BadRequestException("Invalid input.");
                    }
                }
            }


            // Generates seat number for a seat.
            String seatNumber = generateSeatNumber(seatDetails.getRow_number(), seatDetails.getCol_number(), seatDetails.getPos());

            // Inserting the seat into the SEATS table.
            try (PreparedStatement seatStatement = conn.prepareStatement(insert_seat_query)) {
                seatStatement.setInt(1, colId);
                seatStatement.setInt(2, seatDetails.getRow_number());
                seatStatement.setInt(3, seatDetails.getSeat_type_id());
                seatStatement.setString(4, seatNumber);
                seatStatement.executeUpdate();
            }

        }
    }


    /**
     * Generates a seat number based on row, column, and position (UPPER/LOWER).
     * Example: A1L → Row A, Column 1, Lower deck
     *
     * @param row Row number (1-based)
     * @param col Column number
     * @param pos Position string ("UPPER" or "LOWER")
     * @return The generated seat number
     */
    public static String generateSeatNumber( int row, int col, String pos ) {
        char rowLetter = (char) ('A' + row - 1);
        return rowLetter + Integer.toString(col) + pos.charAt(0);
    }


    /**
     * Retrieves all available seat types from the database.
     *
     * @return A Response object containing a list of seat type IDs and names
     * @throws Exception if database access fails
     */
    public static Response getSeatTypes() throws Exception {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(get_seat_types_query);) {

            // GET all the seat types.
            List<Map<String, Object>> seatTypes;
            try (ResultSet rs = statement.executeQuery()) {

                // List to store all the seat Types.
                seatTypes = new ArrayList<>();

                while (rs.next()) {

                    // Map to store the seat type ID and seat type name.
                    Map<String, Object> seatType = new HashMap<>();
                    seatType.put("seatTypeId", rs.getInt("seat_type_id"));
                    seatType.put("seatTypeName", rs.getString("seat_type_name"));

                    seatTypes.add(seatType);
                }
            }

            return Response.ok().entity(seatTypes).build();

        }
    }

    /**
     * Check if a bus is assigned with a schedule
     * @param busId bus ID
     * @param conn DB Connection
     * @return true if the bus is assigned with a schedule, else return false
     * @throws Exception if any error.
     */
    public static boolean checkBusAssignedWithSchedule(int busId, Connection conn) throws Exception {
        try (PreparedStatement statement = conn.prepareStatement(check_bus_assigned_to_schedule_query);) {
            statement.setInt(1, busId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
            return false;
        }
    }

}
