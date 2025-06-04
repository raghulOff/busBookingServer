package com.example.auth.controller;

import com.example.auth.annotation.RolesAllowedCustom;
import com.example.auth.dao.UserRepository;
import com.example.auth.dto.UserDTO;
import com.example.auth.model.User;
import com.example.auth.service.LoginService;
import com.example.auth.service.SignupService;
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

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(User userInput) {
        return LoginService.loginVerification(userInput);
    }


    @GET
    @Path("/add-user")
    @RolesAllowedCustom({1})
    public Response addUserCheck() {
        String username = (String) request.getAttribute("username");

        User user = UserRepository.getUser(username);

        UserDTO userdto = new UserDTO(user.getUsername(), user.getUserId(), user.getRoleId());

        return Response.ok("Only admins can see this").entity(userdto).build();
    }


    @GET
    @Path("/home")
    @RolesAllowedCustom({3})
    public Response homeCheck() {
        String username = (String) request.getAttribute("username");

        User user = UserRepository.getUser(username);
        UserDTO userdto = new UserDTO(user.getUsername(), user.getUserId(), user.getRoleId());

        return Response.ok("only user can see this").entity(userdto).build();
    }


    @GET
    @Path("/admin-home")
    @RolesAllowedCustom({1})
    public Response adminCheck() {
        String username = (String) request.getAttribute("username");
        User user = UserRepository.getUser(username);
        UserDTO userdto = new UserDTO(user.getUsername(), user.getUserId(), user.getRoleId());

        return Response.ok("admin can see this").entity(userdto).build();
    }

    @GET
    @Path("/add-developer")
    @RolesAllowedCustom({1})
    public Response addDevCheck() {
        String username = (String) request.getAttribute("username");
        User user = UserRepository.getUser(username);
        UserDTO userdto = new UserDTO(user.getUsername(), user.getUserId(), user.getRoleId());
        return Response.ok("admin can see this").entity(userdto).build();
    }

    @GET
    @Path("/dev-home")
    @RolesAllowedCustom({2})
    public Response devCheck() {
        String username = (String) request.getAttribute("username");
        User user = UserRepository.getUser(username);
        UserDTO userdto = new UserDTO(user.getUsername(), user.getUserId(), user.getRoleId());
        return Response.ok("developer can see this").entity(userdto).build();
    }

    @GET
    @Path("/logout")
    @Produces(MediaType.TEXT_PLAIN)
    public Response logout() {
        NewCookie expiredCookie = new NewCookie("token", "", "/", null, null, 0, false);
        return Response.ok("Logout success").cookie(expiredCookie).build();
    }

}
