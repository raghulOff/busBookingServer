package com.example.auth.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response)
            throws IOException {

        String origin = request.getHeaderString("Origin");
        if (origin != null && (origin.equals("http://localhost:4200") || origin.equals("http://localhost:8081"))) {
            response.getHeaders().add("Access-Control-Allow-Origin", origin); // ✅ dynamic origin
            response.getHeaders().add("Access-Control-Allow-Credentials", "true"); // ✅ allow cookies
        }

        response.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}
