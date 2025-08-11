package com.example.busbooking.scheduler;

import com.example.busbooking.db.DBConnection;
import com.example.busbooking.enums.BookingStatus;
import com.example.busbooking.enums.ScheduleStatus;
import com.example.busbooking.enums.ScheduledSeatStatus;
import com.example.busbooking.registry.BookingStatusRegistry;
import com.example.busbooking.registry.ScheduleStatusRegistry;
import com.example.busbooking.registry.ScheduledSeatStatusRegistry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static com.example.busbooking.db.DBConstants.*;



/**
 * Scheduler class responsible for periodically updating the status of:
 * - Bus schedules
 * - Scheduled seats
 * - Booking seat records

 * This ensures that completed trips are marked correctly in the database.
 */
public class ScheduleStatusUpdater {

    // SQL query to mark bus schedules as COMPLETED if arrival_time is in the past
    private static final String UPDATE_SCHEDULE_COMPLETED_QUERY =
            String.format("""
                    UPDATE %s SET status_id = ? WHERE status_id = ? AND arrival_time < NOW()
                    """, SCHEDULES);


    // SQL query to update scheduled seat status to TRIP_COMPLETED
    // Only applies to seats whose schedule has been marked as COMPLETED
    private static final String UPDATE_SCHEDULED_SEAT_COMPLETED =
            String.format("""
                    update %s ss set status_id = ?
                    from %s s where ss.schedule_id = s.schedule_id and s.status_id = ?;
                    """, SCHEDULED_SEATS, SCHEDULES);

    // SQL query to update booking seat status to TRIP_COMPLETED
    // Only applies to booking seats whose scheduled seat has TRIP_COMPLETED status
    private static final String UPDATE_BOOKING_SEATS_STATUS_TO_COMPLETED = String.format("""
                    UPDATE %s bs set status_id = ?
                    from %s ss where ss.scheduled_seat_id = bs.scheduled_seat_id and ss.status_id = ?;
            """, BOOKING_SEATS, SCHEDULED_SEATS);


    /**
     * Starts the scheduler that runs updateTripCompletionStatus() every 10 minutes.
     * This uses a single-threaded scheduled executor service.
     */
    public static void start() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Runs every 10 minutes
        scheduler.scheduleAtFixedRate(ScheduleStatusUpdater::updateTripCompletionStatus, 0, 10, TimeUnit.MINUTES); // initial delay, repeat every 10 mins
    }


    /**
     * The main task that:
     * 1. Marks bus schedules as COMPLETED if arrival_time has passed.
     * 2. Updates scheduled seat status to TRIP_COMPLETED.
     * 3. Updates booking seat status to TRIP_COMPLETED.

     * All three updates are executed in a single DB transaction.
     */
    private static void updateTripCompletionStatus() {
        Connection conn = null;
        PreparedStatement stmt1 = null, stmt2 = null, stmt3 = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);


            // 1. Update SCHEDULES table — set status to COMPLETED where arrival_time < NOW

            stmt1 = conn.prepareStatement(UPDATE_SCHEDULE_COMPLETED_QUERY);
            stmt1.setInt(1, ScheduleStatusRegistry.getByCode(ScheduleStatus.COMPLETED.name()).getStatusId());
            stmt1.setInt(2, ScheduleStatusRegistry.getByCode(ScheduleStatus.ACTIVE.name()).getStatusId());

            stmt1.executeUpdate();



            // 2. Update SCHEDULED_SEATS — mark as TRIP_COMPLETED where linked schedule is COMPLETED

            stmt2 = conn.prepareStatement(UPDATE_SCHEDULED_SEAT_COMPLETED);
            stmt2.setInt(1, ScheduledSeatStatusRegistry.getByCode(ScheduledSeatStatus.TRIP_COMPLETED.name()).getStatusId());
            stmt2.setInt(2, ScheduledSeatStatusRegistry.getByCode(ScheduledSeatStatus.TRIP_COMPLETED.name()).getStatusId());

            stmt2.executeUpdate();


            // 3. Update BOOKING_SEATS — mark as TRIP_COMPLETED where linked scheduled seat is TRIP_COMPLETED

            stmt3 = conn.prepareStatement(UPDATE_BOOKING_SEATS_STATUS_TO_COMPLETED);
            stmt3.setInt(1, BookingStatusRegistry.getByCode(BookingStatus.TRIP_COMPLETED.name()).getStatusId());
            stmt3.setInt(2, ScheduledSeatStatusRegistry.getByCode(ScheduledSeatStatus.TRIP_COMPLETED.name()).getStatusId());

            stmt3.executeUpdate();

            conn.commit();

        } catch (Exception e) {

            DBConnection.rollbackConnection(conn);
            System.out.println(e.getMessage());
            e.printStackTrace();

        } finally {
            DBConnection.closeConnection(conn);
            DBConnection.closePreparedStatement(stmt1);
            DBConnection.closePreparedStatement(stmt2);
            DBConnection.closePreparedStatement(stmt3);
        }
    }


}
