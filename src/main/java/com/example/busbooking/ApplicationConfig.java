package com.example.busbooking;


import com.example.busbooking.db.DBInitializer;
import org.glassfish.jersey.jackson.JacksonFeature;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;

import java.sql.SQLException;

@ApplicationPath("/api")
public class ApplicationConfig extends ResourceConfig {
    public ApplicationConfig() throws SQLException {
        packages("com.example.busbooking");
        register(JacksonFeature.class);
//        register(JacksonFeature.withoutExceptionMappers());
//        register(JsonParseExceptionMapper.class);
//        register(JsonMappingExceptionMapper.class);
//        register(JacksonJaxbJsonProvider.class);

        // creates tables and adds sample values in DB
        DBInitializer.initialize();
    }


}
