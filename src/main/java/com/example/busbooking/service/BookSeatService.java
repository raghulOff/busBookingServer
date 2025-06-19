package com.example.busbooking.service;

import com.example.busbooking.dto.base.BookSeatDTO;

import java.sql.*;

import static com.example.busbooking.dao.bus.BusBookingsDAO.*;


// Service method in the process of booking seats;


public class BookSeatService {

    public static final String check_seat_status_query = """
            select status from seats where seat_id = ?;
            """;

    // adds new booking into the bookings table
    public static int addNewBooking( Connection conn, BookSeatDTO bookSeatDTO ) throws SQLException {
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
        return bookingId;
    }

    // adds a new passenger mapped in the booking_seats table
    public static int addNewPassenger(Connection conn, BookSeatDTO.PassengerDetailsDTO passengerDetail) throws SQLException {
        PreparedStatement passengerStatement = conn.prepareStatement(passenger_insert_query, Statement.RETURN_GENERATED_KEYS);
        passengerStatement.setString(1, passengerDetail.getPassengerName());
        passengerStatement.setInt(2, passengerDetail.getPassengerAge());
        passengerStatement.executeUpdate();
        ResultSet rsPassenger = passengerStatement.getGeneratedKeys();
        int passengerId = 0;
        if (rsPassenger.next()) {
            passengerId = rsPassenger.getInt(1);
        }
        return passengerId;
    }


    // creates a new row in the booking_seats table and maps the seat_id, passenger_id, booking_id and status;
    public static void insertBookingSeats( Connection conn, int bookingId, int passengerId, BookSeatDTO.PassengerDetailsDTO passengerDetail ) throws SQLException {
        PreparedStatement seatsStatement = conn.prepareStatement(booking_seats_insert_query);
        seatsStatement.setInt(1, bookingId);
        seatsStatement.setInt(2, passengerDetail.getSeatId());
        seatsStatement.setInt(3, passengerId);
        seatsStatement.setString(4, "BOOKED");

        seatsStatement.executeUpdate();
    }




    // checks if a seat is available or not
    public static boolean checkSeatStatus( int seatId, Connection conn ) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(check_seat_status_query);
        statement.setInt(1, seatId);

        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            if (rs.getString("status").equals("AVAILABLE")) {
                return false;
            }
        }
        return true;
    }
}
