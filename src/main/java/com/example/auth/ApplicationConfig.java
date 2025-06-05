package com.example.auth;


import com.example.auth.db.DBInitializer;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class ApplicationConfig extends ResourceConfig {
    public ApplicationConfig() {
        packages("com.example.auth");
        DBInitializer.initialize();
    }
}
