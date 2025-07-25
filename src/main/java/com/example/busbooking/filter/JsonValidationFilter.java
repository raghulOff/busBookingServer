package com.example.busbooking.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@Provider
public class JsonValidationFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        MediaType mediaType = requestContext.getMediaType();

        // Check if the request's media type is exactly "application/json"
        if (mediaType != null && "application/json".equals(requestContext.getMediaType().toString())) {

            StringBuilder json = new StringBuilder();

            // Read the raw JSON body from the request input stream
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestContext.getEntityStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
            }

            String jsonString = json.toString();

            // If the JSON is invalid, return a 400 Bad Request response immediately
            if (!isValidJson(jsonString)) {
                requestContext.abortWith(
                        Response.status(Response.Status.BAD_REQUEST)
                                .entity("Invalid JSON format")
                                .build()
                );
            }

            // Reset entity stream for further processing
            requestContext.setEntityStream(new ByteArrayInputStream(jsonString.getBytes()));
        }
    }

    // Utility method to validate JSON string using Jackson's ObjectMapper
    private boolean isValidJson(String json) {
        try {
            new ObjectMapper().readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}
