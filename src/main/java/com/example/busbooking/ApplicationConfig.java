package com.example.busbooking;


import com.example.busbooking.db.DBInitializer;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class ApplicationConfig extends ResourceConfig {
    public ApplicationConfig() {
        packages("com.example.busbooking");

        // creates tables and adds sample values in DB
        DBInitializer.initialize();
    }
}
