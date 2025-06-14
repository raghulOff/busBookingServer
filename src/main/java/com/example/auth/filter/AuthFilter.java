package com.example.auth.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.auth.security.JwtUtil;
import com.example.auth.service.AvoidPath;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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
        System.out.println(path);
        if (!AvoidPath.avoidPath(path)) {
            return;
        }

        String token = null;
        Map<String, Cookie> cookies = requestContext.getCookies();
        if (cookies != null && cookies.containsKey("token")) {
            token = cookies.get("token").getValue();
        }
        if (token == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Missing token").build());
            return;
        }

        try {
            DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(JwtUtil.getSecret().getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();


            int userId = claims.get("userId", Integer.class);
            int roleId = claims.get("roleId", Integer.class);
            String username = decodedJWT.getSubject();

            requestContext.setProperty("userId", userId);
            requestContext.setProperty("roleId", roleId);
            requestContext.setProperty("username", username);
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token").build());
        }
    }
}
