package com.example.busbooking.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.busbooking.security.JwtUtil;
import com.example.busbooking.service.BlackListService;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Map;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {


        String path = requestContext.getUriInfo().getPath();

        if (path.contains("login") || path.contains("signup")) {
            return;
        }

        String token = null;

        // TESTING PURPOSE
        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring("Bearer ".length()).trim();
        }

        String user_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIiwidXNlcklkIjoyLCJyb2xlSWQiOjMsImp0aSI6Imp0aS0xNzUyNzM5OTcyMTUyIiwiaWF0IjoxNzUyNzM5OTcyLCJleHAiOjE3NjI3Mzk5NzJ9.jFKGif9WPBysG3MoWN-L-e0G3xfz6Yz5PutPvxKTrVM";
        String admin_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsInVzZXJJZCI6MSwicm9sZUlkIjoxLCJqdGkiOiJqdGktMTc1MjczODIzODEzMyIsImlhdCI6MTc1MjczODIzOCwiZXhwIjoxNzYyNzM4MjM4fQ.sEdz-vyXr49df5ILY-auukn-gkFwaaZTM-pbvUUGeKM";


        if (token != null && (token.equals(user_token) || token.equals(admin_token))) {
            try {
                DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
                int userId = decodedJWT.getClaim("userId").asInt();
                int roleId = decodedJWT.getClaim("roleId").asInt();
                String username = decodedJWT.getSubject();

                requestContext.setProperty("userId", userId);
                requestContext.setProperty("roleId", roleId);
                requestContext.setProperty("username", username);
            } catch (Exception e) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token").build());
            }
            return;
        }

        Map<String, Cookie> cookies = requestContext.getCookies();
        if (cookies != null && cookies.containsKey("token")) {
            token = cookies.get("token").getValue();
        }
        if (token == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Missing token").build());
            return;
        }
        try {
            JwtUtil.verifyToken(token);
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Token has expired").build());
            return;
        }
        String jti = JwtUtil.getJtiFromToken(token);

        if (BlackListService.isBlacklisted(jti)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Token is invalidated").build());
        }

        try {
            DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
            int userId = decodedJWT.getClaim("userId").asInt();
            int roleId = decodedJWT.getClaim("roleId").asInt();
            String username = decodedJWT.getSubject();

            requestContext.setProperty("userId", userId);
            requestContext.setProperty("roleId", roleId);
            requestContext.setProperty("username", username);
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token").build());
        }
    }
}
