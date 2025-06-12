package com.example.busbooking.dao;

import com.example.busbooking.db.DBConnection;
import com.example.busbooking.dto.BookSeatDTO;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class BookSeatDAO {
    private static final String booking_insert_query = "insert into bookings\n" +
            "(user_id, schedule_id, total_amount, boarding_point_id, dropping_point_id)\n" +
            "values (?, ?, ?, ?, ?);";

    private static final String passenger_insert_query = "insert into passenger_details (passenger_name, passenger_age)\n" +
            "values (?, ?);";

    private static final String booking_seats_insert_query = "insert into booking_seats (booking_id, seat_id, passenger_id, status)\n" +
            "values (?, ?, ?, ?);";

    private static final String seat_update_query = "update seats set status = true, user_id = ? where seat_id = ?;";

    
    public static Response bookSeat( BookSeatDTO bookSeatDTO ) {
        try (Connection conn = DBConnection.getConnection();) {
            PreparedStatement book_statement = conn.prepareStatement(booking_insert_query, Statement.RETURN_GENERATED_KEYS);
            book_statement.setInt(1, bookSeatDTO.getUserId());
            book_statement.setInt(2, bookSeatDTO.getScheduleId());
            book_statement.setInt(3, bookSeatDTO.getPayableAmount());
            book_statement.setInt(4, bookSeatDTO.getBoardingPointId());
            book_statement.setInt(5, bookSeatDTO.getDroppingPointId());

            book_statement.executeUpdate();
            ResultSet rs = book_statement.getGeneratedKeys();
            int bookingId = 0;
            if (rs.next()) {
                bookingId = rs.getInt(1);
            }


            PreparedStatement passengerStatement;
            PreparedStatement seatsStatement;
            for (BookSeatDTO.PassengerDetailsDTO passengerDetail : bookSeatDTO.getPassengerDetails()) {
                passengerStatement = conn.prepareStatement(passenger_insert_query, Statement.RETURN_GENERATED_KEYS);
                passengerStatement.setString(1, passengerDetail.getPassengerName());
                passengerStatement.setInt(2, passengerDetail.getPassengerAge());
                passengerStatement.executeUpdate();
                ResultSet rsPassenger = passengerStatement.getGeneratedKeys();
                int passengerId = 0;
                if (rsPassenger.next()) {
                    passengerId = rsPassenger.getInt(1);
                }


                seatsStatement = conn.prepareStatement(booking_seats_insert_query);
                seatsStatement.setInt(1, bookingId);
                seatsStatement.setInt(2, passengerDetail.getSeatId());
                seatsStatement.setInt(3, passengerId);
                seatsStatement.setString(4, "BOOKED");

                seatsStatement.executeUpdate();

                PreparedStatement updateSeatStatement = conn.prepareStatement(seat_update_query);
                updateSeatStatement.setInt(1, bookSeatDTO.getUserId());
                updateSeatStatement.setInt(2, passengerDetail.getSeatId());
                updateSeatStatement.executeUpdate();
            }



            return Response.ok("Seats are booked.").build();
        } catch (Exception e) {
            System.err.println(e);
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Cannot book seats.").build();
        }
    }

}

