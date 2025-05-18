package com.example.auth;


import com.example.auth.session.SessionManager;
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
    @Context
    private HttpServletRequest request;
    private HttpServletResponse response;
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String printString() {
        return "hi";
    }
    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signup(User user) {
        System.out.println(user);
        if (AuthRepo.exists(user.getUsername())) {
            return Response.status(Response.Status.CONFLICT).entity("Already exist").build();
        }
        AuthRepo.addUser(user);
        return Response.ok().build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(User userInput, @Context HttpServletResponse response) {

        User user = AuthRepo.getUser(userInput.getUsername());
        System.out.println(userInput.getUsername());
        if (user == null || !user.getPassword().equals(userInput.getPassword())) {
            System.out.println("Invalid credential");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
        String token = SessionManager.createSession(user);
        if (user.isAdmin()) {
            NewCookie tokenCookie = new NewCookie("adminToken", token, "/", null, null, -1, false, true);
            return Response.ok("Admin login success").cookie(tokenCookie).build();
        }

        // Send token as HTTP-only cookie
        NewCookie tokenCookie = new NewCookie("token", token, "/", null, null, -1, false, true);
        return Response.ok("Login successful").cookie(tokenCookie).build();
    }



    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response status(@CookieParam("token") String token) {


        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Unauthorized: No session token found. Please login.")
                    .build();
        }

        // 2. Validate session
        User user = SessionManager.getUser(token);

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Unauthorized: Invalid or expired session. Please login.")
                    .build();
        }


        return Response.ok(user).build();
    }

    @GET
    @Path("/logout")
    @Produces(MediaType.TEXT_PLAIN)
    public Response logout(@CookieParam("token") String token) {
        if (token != null) {
            SessionManager.invalidate(token);
        }
        NewCookie expiredCookie = new NewCookie("token", "", "/", null, null, 0, false);
        return Response.ok("Logout success").cookie(expiredCookie).build();
    }



    @GET
    @Path("/adminStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public Response adminStatus(@CookieParam("adminToken") String adminToken) {
        if (adminToken == null) {
            System.out.println("Admin token null");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        User user = SessionManager.getUser(adminToken);

        if (user == null) {
            System.out.println("Admin token user null");

            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Unauthorized: Invalid or expired session. Please login.")
                    .build();
        }
        System.out.println("Admin token success");


        return Response.ok(user).build();
    }



//    @GET
//    @Path("/home")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getCurrentUser(@CookieParam("token") String token) {
//        User user = SessionManager.getUser(token);
//        if (user == null) {
//            return Response.status(Response.Status.UNAUTHORIZED).build();
//        }
//        return Response.ok(user).build();  // Sends JSON of the user
//    }
//




}
