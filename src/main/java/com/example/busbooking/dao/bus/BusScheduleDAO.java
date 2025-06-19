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


public class BusScheduleDAO implements ScheduleDAO {
    public static final String select_all_schedules_query = "select schedule_id, route_id, bus_id, departure_time, arrival_time, price, journey_date\n" +
            "from schedules";

    public static final String add_schedule_query = "insert into schedules (route_id, bus_id, departure_time, arrival_time, price, journey_date)\n" +
            "values (?, ?, ?, ?, ?, ?)";

    public static final String insert_seat_query = "INSERT INTO seats (schedule_id, seat_number, row_number, column_number, status) VALUES (?, ?, ?, ?, false)" +
            " ON CONFLICT (schedule_id, row_number, column_number) DO NOTHING";

    public static final String get_schedule_details_query = """
            SELECT r.route_id, b.bus_id, s.price, sc.city_name as source_city, dc.city_name as destination_city, b.bus_type, b.operator_name,
            s.schedule_id, b.bus_number, s.departure_time, s.arrival_time, s.journey_date, b.total_columns
            FROM schedules s
            JOIN buses b ON s.bus_id = b.bus_id
            JOIN routes r on s.route_id = r.route_id
            JOIN cities sc on r.source_city_id = sc.city_id
            JOIN cities dc on r.destination_city_id = dc.city_id
            WHERE s.schedule_id = ?
            """;


    public static final String get_seat_details_query = """
                     select s.row_number, sg.col_number, sg.pos, s.seat_type_id, s.seat_number, s.status, s.seat_id, st.seat_type_name
                     from seats s
                     join seat_grid_columns sg on s.column_id = sg.column_id
            join seat_type st on st.seat_type_id = s.seat_type_id
                     where sg.bus_id = ? order by s.seat_id asc;
            """;


    // all available schedules are returned.
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
                double price = rs.getDouble("price");
                String journeyDate = rs.getString("journey_date");
                allSchedules.add(new ScheduleDTO(schedule_id, routeid, busid, dep_time, arr_time, price, journeyDate));
            }
        } catch (Exception e) {
            throw e;
        }
        return allSchedules;
    }

    // adds a new schedule
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
            statement.setDouble(5, scheduleDTO.getPrice());
            statement.setDate(6, java.sql.Date.valueOf(journeyDate));
            statement.executeUpdate();


            conn.commit();
        } catch (Exception e) {
            System.out.println(e);
            return Response.status(Response.Status.CONFLICT).entity("Unable To Add New Schedule").build();
        }
        return Response.ok().entity("Schedule created").build();
    }




    // The details of a specific schedule is returned.
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
                        seatrs.getString("seat_type_name"),
                        seatrs.getInt("seat_type_id"),
                        seatrs.getString("pos"),
                        seatrs.getString("seat_number"),
                        seatrs.getString("status"),
                        seatrs.getInt("row_number"),
                        seatrs.getInt("col_number"),
                        seatrs.getInt("seat_id")
                ));
            }


            BusScheduleDetailsDTO sd = new BusScheduleDetailsDTO(
                    rs.getInt("route_id"),
                    rs.getInt("bus_id"),
                    rs.getInt("schedule_id"),
                    rs.getString("bus_number"),
                    rs.getString("departure_time"),
                    rs.getString("arrival_time"),
                    seats,
                    rs.getString("source_city"),
                    rs.getString("destination_city"),
                    rs.getString("operator_name"),
                    rs.getString("bus_type"),
                    rs.getDouble("price"),
                    rs.getString("journey_date"),
                    rs.getInt("total_columns")
            );


            return Response.ok(sd).build();

        } catch (Exception e) {
            System.err.println(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Something went wrong").build();
        }
    }
}
