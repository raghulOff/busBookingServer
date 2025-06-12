package com.example.busbooking.controller;

import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.UserDAO;
import com.example.busbooking.dto.UserDTO;
import com.example.busbooking.model.User;
import com.example.busbooking.service.LoginService;
import com.example.busbooking.service.SignupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;


@Path("/user")

public class AuthResource {

    @Context
    HttpServletRequest request;

    // adds new user with role.
    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signup(User user) {
        if (SignupService.signupVerification(user)) {
            return Response.ok("Signup success").build();
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
    @RolesAllowedCustom({3})
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
    @RolesAllowedCustom({3})
    public Response bookPageCheck() {
        String username = (String) request.getAttribute("username");
        User user = UserDAO.getUser(username);
        UserDTO userdto = new UserDTO(user.getUsername(), user.getUserId(), user.getRoleId());

        return Response.ok("Customer can see this").entity(userdto).build();
    }


    // visiting this page triggers this endpoint to check if the user has access to this page. Returns the admin data

    @GET
    @Path("/admin-home")
    @RolesAllowedCustom({1})
    public Response adminCheck() {
        String username = (String) request.getAttribute("username");
        User user = UserDAO.getUser(username);
        UserDTO userdto = new UserDTO(user.getUsername(), user.getUserId(), user.getRoleId());

        return Response.ok("admin can see this").entity(userdto).build();
    }


    // visiting this page triggers this endpoint to check if the user has access to this page. Returns the developer data

    @GET
    @Path("/dev-home")
    @RolesAllowedCustom({2})
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
    @RolesAllowedCustom({1,2,3})
    public Response logout() {
        NewCookie expiredCookie = new NewCookie("token", "", "/", null, null, 0, false);
        return Response.ok("Logout success").cookie(expiredCookie).build();
    }

}
