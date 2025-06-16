package com.example.busbooking.dao.bus;

import com.example.busbooking.dao.base.BookingsDAO;
import com.example.busbooking.db.DBConnection;
import com.example.busbooking.dto.base.BookSeatDTO;
import com.example.busbooking.dto.base.BookingsDTO;
import com.example.busbooking.service.BookSeatService;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BusBookingsDAO implements BookingsDAO {

    public static final String get_bookings_query = """
            select b.booking_id, s.journey_date, b.total_amount, bus.operator_name, sc.city_name as src, dc.city_name as destination,
            s.arrival_time, s.departure_time, lb.location_name as boarding, ld.location_name as dropping
            from bookings b
            join schedules s on b.schedule_id = s.schedule_id
            join buses bus on s.bus_id = bus.bus_id
            join routes r on s.route_id = r.route_id
            join cities sc on sc.city_id = r.source_city_id
            join cities dc on dc.city_id = r.destination_city_id
            join locations lb on lb.location_id = b.boarding_point_id
            join locations ld on ld.location_id = b.dropping_point_id
            join users u on u.user_id = b.user_id
            where b.user_id = ? order by b.booking_id desc;
            """;

    public static final String get_passenger_details_query = """
            select s.seat_id, s.seat_number, p.passenger_name, p.passenger_age, b.booking_id, bs.status
            from booking_seats bs
            join passenger_details p on bs.passenger_id = p.passenger_id
            join bookings b on b.booking_id = bs.booking_id
            join seats s on s.seat_id = bs.seat_id
            where b.user_id = ? and b.booking_id = ?;
            """;


    public static final String booking_insert_query = "insert into bookings\n" +
            "(user_id, schedule_id, total_amount, boarding_point_id, dropping_point_id, trip_status)\n" +
            "values (?, ?, ?, ?, ?, 'UPCOMING');";

    public static final String passenger_insert_query = "insert into passenger_details (passenger_name, passenger_age)\n" +
            "values (?, ?);";

    public static final String booking_seats_insert_query = "insert into booking_seats (booking_id, seat_id, passenger_id, status)\n" +
            "values (?, ?, ?, ?);";

    public static final String seat_update_query = "update seats set status = true, user_id = ? where seat_id = ?;";

    public static final String update_available_seats_query = "update schedules set available_seats = available_seats-? where schedule_id = ?";


    public static final String update_booking_seats_status_query = "update booking_seats set status = 'CANCELLED' where seat_id = ?";

    public static final String update_seats_status_user_id_query = "update seats set status = false, user_id = null where seat_id = ?";


    // To retrieve all the history of bookings for a user id;
    public Response getAllBookings( int userId ) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(get_bookings_query)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            List<BookingsDTO> bookings = new ArrayList<>();
            while (rs.next()) {
                String journeyDate = rs.getString("journey_date");
                double totalAmount = rs.getDouble("total_amount");
                String operatorName = rs.getString("operator_name");
                String sourceCity = rs.getString("src");
                String destinationCity = rs.getString("destination");
                String arrivalTime = rs.getString("arrival_time");
                String departureTime = rs.getString("departure_time");
                String boardingLocation = rs.getString("boarding");
                String droppingLocation = rs.getString("dropping");
                int bookingId = rs.getInt("booking_id");

                List<Map<String, Object>> passengerDetails = getPassengerDetails(userId, bookingId);

                bookings.add(new BookingsDTO(journeyDate, totalAmount, operatorName, sourceCity, destinationCity,
                        arrivalTime, departureTime, boardingLocation, droppingLocation, passengerDetails));
            }

            return Response.ok("Booking details retrieved").entity(bookings).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Returns passenger details of a specific booking.
    public List<Map<String, Object>> getPassengerDetails( int userId, int bookingId ) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(get_passenger_details_query)) {
            statement.setInt(1, userId);
            statement.setInt(2, bookingId);

            ResultSet rs = statement.executeQuery();

            List<Map<String, Object>> passengerDetails = new ArrayList<>();


            while (rs.next()) {
                Map<String, Object> passenger = new HashMap<>();
                String seatNumber = rs.getString("seat_number");
                String passengerName = rs.getString("passenger_name");
                int passengerAge = rs.getInt("passenger_age");
                int seatId = rs.getInt("seat_id");
                String status = rs.getString("status");

                passenger.put("seatNumber", seatNumber);
                passenger.put("passengerName", passengerName);
                passenger.put("passengerAge", passengerAge);
                passenger.put("seatId", seatId);
                passenger.put("status", status);
                passengerDetails.add(passenger);
            }

            return passengerDetails;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // this method handles all the bookings process including updating / decreasing the available seats for the bus
    // checking for concurrency if the seat is already booked.
    // Storing the booking passenger details.

    public Response bookSeat(BookSeatDTO bookSeatDTO) {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement statement = conn.prepareStatement(update_available_seats_query);) {
                statement.setInt(1, bookSeatDTO.getPassengerDetails().size());
                statement.setInt(2, bookSeatDTO.getScheduleId());
                statement.executeUpdate();
            }

            int bookingId = BookSeatService.addNewBooking(conn, bookSeatDTO);

            for (BookSeatDTO.PassengerDetailsDTO passengerDetail : bookSeatDTO.getPassengerDetails()) {

                if (BookSeatService.checkSeatStatus(passengerDetail.getSeatId(), conn)) {
                    throw new Exception();
                }
                int passengerId = BookSeatService.addNewPassenger(conn, passengerDetail);


                BookSeatService.insertBookingSeats(conn, bookingId, passengerId, passengerDetail);

                PreparedStatement updateSeatStatement = conn.prepareStatement(seat_update_query);
                updateSeatStatement.setInt(1, bookSeatDTO.getUserId());
                updateSeatStatement.setInt(2, passengerDetail.getSeatId());
                updateSeatStatement.executeUpdate();
            }

            conn.commit();
            return Response.ok("Seats are booked.").build();

        } catch (Exception e) {
            System.err.println(e);
            try {
                if (!conn.isClosed()) {
                    conn.rollback();
                }
            } catch (Exception rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx);
            }
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Cannot book seats.").build();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    System.err.println("Connection close failed: " + e);
                }
            }
        }
    }




    // method to cancel a passenger's ticket.

    public Response cancelTicket(int seatId) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(update_booking_seats_status_query);
            statement.setInt(1, seatId);
            statement.executeUpdate();

            statement = conn.prepareStatement(update_seats_status_user_id_query);
            statement.setInt(1, seatId);
            statement.executeUpdate();

            return Response.ok("Tickets cancelled.").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }




}

