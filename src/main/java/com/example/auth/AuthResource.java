package com.example.auth;
import com.example.auth.repository.UserRepository;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.auth.security.JwtUtil;
//import com.example.auth.session.SessionManager;
import com.example.auth.security.PasswordUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.mvc.Viewable;

import javax.print.attribute.standard.Media;

@Path("/user")
public class AuthResource {
    @OPTIONS
    @Path("{any: .*}")
    public Response handlePreflight() {
        return Response.ok().build();
    }


    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signup(User user) {
        System.out.println("coming here");
        if (UserRepository.exists(user.getUsername())) {
            return Response.status(Response.Status.CONFLICT).entity("Already exist").build();
        }
        UserRepository.addUser(user);
        return Response.ok().build();
    }


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(User userInput) {
        User user = UserRepository.getUser(userInput.getUsername());
        boolean matchPassword = PasswordUtil.verifyPassword(userInput.getPassword(), user.getPassword());
        if (user == null || !matchPassword) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }

        String token = JwtUtil.generateToken(user);

        NewCookie tokenCookie = new NewCookie("token", token, "/", null, null, -1, false, true);

        return Response.ok("Login successful").cookie(tokenCookie).build();
    }



    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response status(@CookieParam("token") String token) {
        try {
            DecodedJWT jwt = JwtUtil.verifyToken(token);
            String username = jwt.getSubject();
            User user = UserRepository.getUser(username);

            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid user").build();
            }

            return Response.ok(user).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid or expired token").build();
        }
    }

//    @GET
//    @Path("/adminStatus")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response adminStatus(@CookieParam("adminToken") String token) {
//        try {
//            DecodedJWT jwt = JwtUtil.verifyToken(token);
//            String role = jwt.getClaim("role").asString();
//
//            if (!"ADMIN".equals(role)) {
//                return Response.status(Response.Status.FORBIDDEN).entity("Not an admin").build();
//            }
//
//            String username = jwt.getSubject();
//            User user = AuthRepo.getUser(username);
//            return Response.ok(user).build();
//
//        } catch (Exception e) {
//            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid or expired token").build();
//        }
//    }


    @GET
    @Path("/logout")
    @Produces(MediaType.TEXT_PLAIN)
    public Response logout() {
        NewCookie expiredCookie = new NewCookie("token", "", "/", null, null, 0, false);
//        NewCookie adminExpired = new NewCookie("adminToken", "", "/", null, null, 0, false);
        return Response.ok("Logout success").cookie(expiredCookie).build();
    }


}
