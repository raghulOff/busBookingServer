package com.example.busbooking.service;

import com.example.busbooking.dto.bus.BusVehicleDTO;

import java.sql.*;

public class AddBusService {
    public static final String insert_into_buses_table_query = "insert into buses (bus_number, operator_name, bus_type, total_columns)" +
            "\nvalues (?, ?, ?, ?) ON CONFLICT (bus_number) DO NOTHING";
    public static final String insert_into_seat_grid_columns_query = "insert into seat_grid_columns " +
            "(bus_id, col_number, total_rows, pos) values (?, ?, ?, ?) ON CONFLICT (bus_id, col_number, pos) DO NOTHING";

    public static int addNewBus( Connection conn, BusVehicleDTO busVehicleDTO ) throws Exception {
        try (PreparedStatement statement = conn.prepareStatement(insert_into_buses_table_query, Statement.RETURN_GENERATED_KEYS);) {

            statement.setString(1, busVehicleDTO.getVehicleNumber());
            statement.setString(2, busVehicleDTO.getOperatorName());
            statement.setString(3, busVehicleDTO.getBusType());
            statement.setInt(4, busVehicleDTO.getTotalColumns());

            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            int busId = 0;
            if (generatedKeys.next()) {
                busId = generatedKeys.getInt(1);
            }
            return busId;
        } catch (Exception e) {
            throw e;
        }
    }

    public static void addSeatGridColumns(Connection conn, BusVehicleDTO busVehicleDTO, int busId) throws Exception {
        try (PreparedStatement statement = conn.prepareStatement(insert_into_seat_grid_columns_query);) {
            for (BusVehicleDTO.SeatGridCount seatGridCount : busVehicleDTO.getSeatGridCount()) {
                if (seatGridCount.getCol_number() > busVehicleDTO.getTotalColumns() ||
                    seatGridCount.getCol_number() < 1) {
                    throw new Exception("Invalid column number");
                }
                statement.setInt(1, busId);
                statement.setInt(2, seatGridCount.getCol_number());
                statement.setInt(3, seatGridCount.getTotal_rows());
                statement.setString(4, seatGridCount.getPos());
                statement.executeUpdate();

            }
        } catch (Exception e) {
            throw e;
        }
    }
    public static String get_column_id_query = "select column_id, total_rows from seat_grid_columns where bus_id = ? and col_number = ? and pos = ?";
    public static final String insert_seat_query = "insert into seats (column_id, row_number, seat_type_id, seat_number) values (?, ?, ?, ?);";


    public static void addSeats(Connection conn, BusVehicleDTO busVehicleDTO, int busId) throws Exception {
        try {
            for (BusVehicleDTO.SeatDetails seatDetails : busVehicleDTO.getSeatDetails()) {

                int cols = seatDetails.getCol_number();
                int rows = seatDetails.getRow_number();
                int totalCols = busVehicleDTO.getTotalColumns();


                PreparedStatement colStatement = conn.prepareStatement(get_column_id_query);
                colStatement.setInt(1, busId);
                colStatement.setInt(2, seatDetails.getCol_number());
                colStatement.setString(3, seatDetails.getPos());

                ResultSet rs = colStatement.executeQuery();

                int colId = 0;
                if (rs.next()) {
                    colId = rs.getInt("column_id");

                }

                if ((cols > totalCols || cols < 1) || (rows > rs.getInt("total_rows") || rows < 1)) {
                    throw new Exception("Row or Col input is invalid");
                }

                String seatNumber = generateSeatNumber(seatDetails.getRow_number(), seatDetails.getCol_number(), seatDetails.getPos());

                PreparedStatement seatStatement = conn.prepareStatement(insert_seat_query);
                seatStatement.setInt(1, colId);
                seatStatement.setInt(2, seatDetails.getRow_number());
                seatStatement.setInt(3, seatDetails.getSeat_type_id());
                seatStatement.setString(4, seatNumber);
                seatStatement.executeUpdate();

            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static String generateSeatNumber(int row, int col, String pos) {
        char rowLetter = (char) ('A' + row - 1);
        return rowLetter + Integer.toString(col) + pos.charAt(0);
    }
}
