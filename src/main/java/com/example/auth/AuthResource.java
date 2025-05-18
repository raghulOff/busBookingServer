package com.example.auth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
//import sun.management.MemoryNotifInfoCompositeData;

import javax.print.attribute.standard.Media;

@Path("/user")
public class AuthResource {
    @Context
    private HttpServletRequest request;
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
        if (AuthRepo.storeUser(user)) {
            return Response.ok("success").build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity("failure").build();
        }
    }
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(User user) {
        HttpSession session = request.getSession();
        if (session.getAttribute("username") != null) {
            return Response.ok().entity("success").build();
        }
        if (AuthRepo.loginUser(user)) {
            session.setAttribute("username", user.getUsername());
            return Response.ok().entity("success").build();
        }
        return Response.status(Response.Status.CONFLICT).entity("failed").build();
    }


    @GET
    @Path("/logout")
    @Produces(MediaType.TEXT_PLAIN)
    public Response logout() {
        HttpSession session = request.getSession();
        if (session.getAttribute("username") != null) {
            session.setAttribute("username", null);
            session.invalidate();
        }
        return Response.ok().entity("logged out").build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.TEXT_PLAIN)
    public Response status() {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            return Response.ok("Logged in as: " + session.getAttribute("user")).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("Not logged in").build();
    }



}
