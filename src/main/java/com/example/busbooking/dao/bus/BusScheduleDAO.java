package com.example.busbooking.dao.bus;

import com.example.busbooking.dao.base.ScheduleDAO;
import com.example.busbooking.db.DBConnection;
import com.example.busbooking.dto.base.ScheduleDTO;
import com.example.busbooking.dto.bus.BusScheduleDetailsDTO;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Response;
import org.jvnet.hk2.annotations.Service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.busbooking.service.GenerateSeats.generateSeatsForSchedule;


@Service
@Singleton
public class BusScheduleDAO implements ScheduleDAO {
    public static final String select_all_schedules_query = "select schedule_id, route_id, bus_id, departure_time, arrival_time, available_seats, price, journey_date\n" +
            "from schedules";

    public static final String add_schedule_query = "insert into schedules (route_id, bus_id, departure_time, arrival_time, available_seats, price, journey_date)\n" +
            "values (?, ?, ?, ?, ?, ?, ?)";

    public static final String insert_seat_query = "INSERT INTO seats (schedule_id, seat_number, row_number, column_number, status) VALUES (?, ?, ?, ?, false)" +
            " ON CONFLICT (schedule_id, row_number, column_number) DO NOTHING";

    public static final String get_schedule_details_query = """
                SELECT s.price, sc.city_name as source_city, dc.city_name as destination_city, b.bus_type, b.operator_name, s.schedule_id, b.bus_number, s.departure_time, s.arrival_time, s.available_seats
                FROM schedules s
                JOIN buses b ON s.bus_id = b.bus_id
                JOIN routes r on s.route_id = r.route_id
                JOIN cities sc on r.source_city_id = sc.city_id
                JOIN cities dc on r.destination_city_id = dc.city_id
                WHERE s.schedule_id = ?
                """;

    public static final String get_seat_details_query = """
                    SELECT seat_id, seat_number, status, row_number, column_number
                    FROM seats
                    WHERE schedule_id = ? order by row_number, column_number
                """;

    public List<ScheduleDTO> getSchedules() throws Exception {

        List<ScheduleDTO> allSchedules = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(select_all_schedules_query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int schedule_id = rs.getInt("schedule_id");
                int routeid = rs.getInt("route_id");
                int busid = rs.getInt("bus_id");
                String dep_time = rs.getString("departure_time");
                String arr_time = rs.getString("arrival_time");
                int av_seats = rs.getInt("available_seats");
                double price = rs.getDouble("price");
                String journeyDate = rs.getString("journey_date");
                allSchedules.add(new ScheduleDTO(schedule_id, routeid, busid, dep_time, arr_time, av_seats, price, journeyDate));
            }
        } catch (Exception e) {
            throw e;
        }
        return allSchedules;
    }

    public Response addNewSchedule( ScheduleDTO scheduleDTO ) throws Exception {
        LocalDate journeyDate = LocalDate.parse(scheduleDTO.getJourneyDate());
        LocalDateTime departure = LocalDateTime.parse(scheduleDTO.getDepartureTime());
        LocalDateTime arrival = LocalDateTime.parse(scheduleDTO.getArrivalTime());

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement statement = conn.prepareStatement(add_schedule_query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, scheduleDTO.getRouteId());
            statement.setInt(2, scheduleDTO.getBusId());
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(departure));
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(arrival));
            statement.setInt(5, scheduleDTO.getAvailableSeats());
            statement.setDouble(6, scheduleDTO.getPrice());
            statement.setDate(7, java.sql.Date.valueOf(journeyDate));
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int scheduleId = generatedKeys.getInt(1);
                generateSeatsForSchedule(scheduleId, scheduleDTO.getAvailableSeats(), conn, insert_seat_query);
            }

            conn.commit();
        } catch (Exception e) {
            System.out.println(e);
            return Response.status(Response.Status.CONFLICT).entity("Unable To Add New Schedule").build();
        }
        return Response.ok().entity("Schedule created").build();
    }



    public Response getScheduleDetails( int scheduleId ) {

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(get_schedule_details_query);
            statement.setInt(1, scheduleId);

            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                return Response.status(Response.Status.NOT_FOUND).entity("Schedule not found").build();
            }

            statement = conn.prepareStatement(get_seat_details_query);
            statement.setInt(1, scheduleId);

            ResultSet seatrs = statement.executeQuery();
            List<BusScheduleDetailsDTO.SeatDTO> seats = new ArrayList<>();
            while (seatrs.next()) {
                seats.add(new BusScheduleDetailsDTO.SeatDTO(
                        seatrs.getString("seat_number"),
                        seatrs.getBoolean("status"),
                        seatrs.getInt("row_number"),
                        seatrs.getInt("column_number"),
                        seatrs.getInt("seat_id")
                ));
            }


            BusScheduleDetailsDTO sd = new BusScheduleDetailsDTO(
                    rs.getInt("schedule_id"),
                    rs.getString("bus_number"),
                    rs.getString("departure_time").toString(),
                    rs.getString("arrival_time").toString(),
                    rs.getInt("available_seats"),
                    seats,
                    rs.getString("source_city"),
                    rs.getString("destination_city"),
                    rs.getString("operator_name"),
                    rs.getString("bus_type"),
                    rs.getDouble("price")
            );

            return Response.ok(sd).build();

        } catch (Exception e) {
            System.err.println(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Something went wrong").build();
        }
    }
}
