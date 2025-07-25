package com.example.busbooking.controller;

import com.example.busbooking.annotation.RolesAllowedCustom;
import com.example.busbooking.dao.user.UserDAO;
import com.example.busbooking.model.Role;
import com.example.busbooking.model.User;
import com.example.busbooking.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


/**
 * REST controller responsible for authentication, role-based authorization,

 * Endpoints are protected using the {@link RolesAllowedCustom} annotation to
 * ensure only users with specific roles can access them.
 */

@Path("/user")

public class AuthController {

    @Context
    HttpServletRequest request;

    /**
     * Registers a new user with the default role of USER.
     *
     * @param user the user details submitted via request body (e.g., username, password)
     * @return HTTP 200 on success, or error response on failure
     */
    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signup( User user ) {
        // assigning role as USER
        user.setRole(Role.USER);
        return AuthService.validateAndRegisterUser(user);
    }


    /**
     * Allows an ADMIN to register a new user with a custom role (e.g., DEVELOPER, ADMIN).
     *
     * @param user the user object with assigned role
     * @return HTTP 200 on success, or error response on failure
     */
    @POST
    @Path("/add-user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.ADMIN})
    public Response addUser( User user ) {
        return AuthService.validateAndRegisterUser(user);
    }


    /**
     * Authenticates a user using credentials and returns username, user ID and role ID on success.
     *
     * @param userInput the login credentials (username and password)
     * @return HTTP 200 with user data on success, error otherwise
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login( User userInput ) throws Exception {
        return AuthService.loginVerification(userInput);
    }


    /**
     * Endpoint accessible by users with USER role to validate access to home page.
     *
     * @return user details if authorized
     */
    @GET
    @Path("/home")
    @RolesAllowedCustom({Role.USER})
    @Produces(MediaType.APPLICATION_JSON)
    public Response homeCheck() {
        String username = (String) request.getAttribute("username");
        return AuthService.extractUserDetails(username, "Only user can see this.");
    }


    /**
     * Endpoint accessible for the bus booking page. Only USER role is allowed.
     *
     * @return user details if authorized
     */
    @GET
    @Path("/book-bus")
    @RolesAllowedCustom({Role.USER})
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookPageCheck() {
        String username = (String) request.getAttribute("username");
        return AuthService.extractUserDetails(username, "Only customer can see this.");

    }


    /**
     * Endpoint to check access to the admin homepage.
     * Only accessible to ADMIN users.
     *
     * @return admin details if authorized
     */
    @GET
    @Path("/admin-home")
    @RolesAllowedCustom({Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response adminCheck() {

        String username = (String) request.getAttribute("username");
        return AuthService.extractUserDetails(username, "Only admin can see this.");

    }


    /**
     * Endpoint for developers to access their dev homepage.
     *
     * @return developer details if authorized
     */
    @GET
    @Path("/dev-home")
    @RolesAllowedCustom({Role.DEVELOPER})
    @Produces(MediaType.APPLICATION_JSON)
    public Response devCheck() {
        String username = (String) request.getAttribute("username");
        return AuthService.extractUserDetails(username, "Only developer can see this.");

    }


    /**
     * Logs out the user by expiring the JWT cookie and adds the token to blacklist.
     *
     * @param token the session token from the cookie
     * @return plain text response
     */
    @GET
    @Path("/logout")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowedCustom({Role.USER, Role.DEVELOPER, Role.ADMIN})
    public Response logout( @CookieParam("token") String token ) {
        return AuthService.logoutUser(token);
    }


    /**
     * Fetches all bookings of currently authenticated user.
     *
     * @return list of booking details if authorized
     */
    @GET
    @Path("/bookings")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.USER})
    public Response bookingHistoryCheck() {
        String username = (String) request.getAttribute("username");
        return AuthService.extractUserDetails(username, "Customer can see this.");

    }

    /**
     * Returns a list of all available roles.
     * Only accessible by ADMIN users.
     *
     * @return list of roles in JSON format
     */
    @GET
    @Path("/get-roles")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowedCustom({Role.ADMIN})
    public Response getRoles() {
        return UserDAO.getRoles();
    }

}
