package com.example.busbooking.controller;

import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.base.UserDAO;
import com.example.busbooking.dto.base.UserDTO;
import com.example.busbooking.model.Role;
import com.example.busbooking.model.User;
import com.example.busbooking.security.JwtUtil;
import com.example.busbooking.service.BlackListService;
import com.example.busbooking.service.LoginService;
import com.example.busbooking.service.SignupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;


@Path("/user")

public class AuthController {

    @Context
    HttpServletRequest request;

    // adds new user with role.
    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signup(User user) {
        user.setRole(Role.USER);
        if (SignupService.signupVerification(user)) {
            return Response.ok("Signup success").build();
        }
        return Response.status(Response.Status.CONFLICT).entity("Already exist").build();
    }


    // this endpoint is accessible only by admin to allowed roles and can add a user with custom role.
    @POST
    @Path("/add-user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.ADMIN})
    public Response addUser(User user) {
        if (SignupService.signupVerification(user)) {
            return Response.ok("Add user success").build();

        }
        return Response.status(Response.Status.CONFLICT).entity("Already exist").build();
    }



    // after successful login, returns the user id and role id.
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(User userInput) {
        return LoginService.loginVerification(userInput);
    }



    // visiting home page triggers this endpoint to check if the user has access to this page. Returns the user data
    @GET
    @Path("/home")
    @RolesAllowedCustom({Role.USER})
    public Response homeCheck() {
        String username = (String) request.getAttribute("username");
        UserDTO userdto = null;
        try {
            User user = UserDAO.getUser(username);
            userdto = new UserDTO(user.getUsername(), user.getUserId(), user.getRoleId());
        } catch (Exception e) {
            System.err.println(e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok("only user can see this").entity(userdto).build();
    }


    // visiting this page triggers this endpoint to check if the user has access to this page. Returns the user data

    @GET
    @Path("/book-bus")
    @RolesAllowedCustom({Role.USER})
    public Response bookPageCheck() {
        String username = (String) request.getAttribute("username");
        User user = UserDAO.getUser(username);
        UserDTO userdto = new UserDTO(user.getUsername(), user.getUserId(), user.getRoleId());

        return Response.ok("Customer can see this").entity(userdto).build();
    }


    // visiting this page triggers this endpoint to check if the user has access to this page. Returns the admin data

    @GET
    @Path("/admin-home")
    @RolesAllowedCustom({Role.ADMIN})
    public Response adminCheck() {
        String username = (String) request.getAttribute("username");
        User user = UserDAO.getUser(username);
        UserDTO userdto = new UserDTO(user.getUsername(), user.getUserId(), user.getRoleId());

        return Response.ok("admin can see this").entity(userdto).build();
    }


    // visiting this page triggers this endpoint to check if the user has access to this page. Returns the developer data

    @GET
    @Path("/dev-home")
    @RolesAllowedCustom({Role.DEVELOPER})
    public Response devCheck() {
        String username = (String) request.getAttribute("username");
        User user = UserDAO.getUser(username);
        UserDTO userdto = new UserDTO(user.getUsername(), user.getUserId(), user.getRoleId());
        return Response.ok("developer can see this").entity(userdto).build();
    }


    // expires the cookie which ends the user session.
    @GET
    @Path("/logout")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.USER, Role.DEVELOPER, Role.ADMIN})
    public Response logout(@CookieParam("token") String token) {

        String jti = JwtUtil.getJtiFromToken(token);
        BlackListService.addToBlacklist(jti);

        NewCookie expiredCookie = new NewCookie("token", "", "/", null, null, 0, false, true);
        return Response.ok("Logout success").cookie(expiredCookie).build();
    }


    // returns all the bookings done by a user
    @GET
    @Path("/bookings")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.USER})
    public Response bookingHistoryCheck() {
        String username = (String) request.getAttribute("username");
        User user = UserDAO.getUser(username);
        UserDTO userdto = new UserDTO(user.getUsername(), user.getUserId(), user.getRoleId());

        return Response.ok("Customer can see this").entity(userdto).build();
    }

    // returns all the roles available
    @GET
    @Path("/get-roles")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN})
    public Response getRoles() {
        return UserDAO.getRoles();
    }

}
