package com.example.busbooking.config;


import com.example.busbooking.db.DBConnection;
import com.example.busbooking.db.DBInitializer;
import com.example.busbooking.exception.ValidationExceptionMapper;
import com.example.busbooking.registry.BookingStatusRegistry;
import com.example.busbooking.registry.RolePermissionRegistry;
import com.example.busbooking.registry.ScheduleStatusRegistry;
import com.example.busbooking.registry.ScheduledSeatStatusRegistry;
import com.example.busbooking.scheduler.ScheduleStatusUpdater;
import org.glassfish.jersey.jackson.JacksonFeature;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.validation.ValidationFeature;



@ApplicationPath("/api")
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() throws Exception {

        packages("com.example.busbooking");
        register(JacksonFeature.class);
        register(ValidationFeature.class);
        register(ValidationExceptionMapper.class);

        // Creates tables and adds sample values in DB
        DBInitializer.initialize();

        // Caching frequently used statuses from DB
        BookingStatusRegistry.loadFromDB();
        ScheduledSeatStatusRegistry.loadFromDB();
        ScheduleStatusRegistry.loadFromDB();

        // Caching the role permission mapping
        RolePermissionRegistry.load(DBConnection.getConnection());


        // To update the status of the bus schedules, booked seats and scheduled seats.
        ScheduleStatusUpdater.start();



    }
}
