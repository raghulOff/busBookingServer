package com.example.busbooking.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.busbooking.dao.user.UserDAO;
import com.example.busbooking.db.DBConnection;
import com.example.busbooking.dto.base.UsersDTO;
import com.example.busbooking.model.Role;
import com.example.busbooking.model.User;
import com.example.busbooking.security.JwtUtil;
import com.example.busbooking.security.PasswordUtil;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.example.busbooking.db.DBConstants.USERS;
import static com.example.busbooking.db.DBConstants.USER_ROLES;


/**
 * AuthService handles authentication-related operations such as login, signup,
 * token generation, logout, and user verification.
 */

public class AuthService {

    // SQL to check if a user exists.
    private static final String check_user_exist_query = String.format("SELECT 1 FROM %s WHERE username = ?", USERS);
    // SQL to map user ID and role ID in the USER_ROLES table.
    public static final String insert_user_role_query = String.format("INSERT INTO %s (user_id, role_id) VALUES (?, ?)", USER_ROLES);
    // SQL to insert username and password
    public static final String insert_un_pass_query = String.format("INSERT INTO %s (username, password) VALUES (?, ?)", USERS);


    /**
     * Verifies user credentials and generates JWT if login is successful.
     *
     * @param userInput User object containing username and password
     * @return Response with JWT token cookie and basic user details if successful,
     *         or error response otherwise
     */

    public static Response loginVerification( User userInput) throws Exception {

        // Check for valid parameters.
        if (userInput == null || userInput.getUsername() == null || userInput.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
        }

        // GET the user data.
        User user = UserDAO.getUser(userInput.getUsername());

        // Check if user exists
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User not found").build();
        }

        // Check if the user input password and DB stored hash password are same.
        boolean matchPassword = PasswordUtil.verifyPassword(userInput.getPassword(), user.getPassword());

        // If not matched decline
        if (!matchPassword) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }

        // Generate a new JWT token
        String token = JwtUtil.generateToken(user);

        // Generate a new cookie
        NewCookie tokenCookie = new NewCookie("token", token, "/", null, null, -1, true, true);

        // user DTO to send response with details like (username, user ID and role ID)
        UsersDTO usersDTO = new UsersDTO(user.getUsername(), user.getUserId(), user.getRoleId());

        return Response.ok("Login successful").entity(usersDTO).cookie(tokenCookie).build();
    }


    /**
     * Retrieves user details and returns them in the response body.
     *
     * @param username     Username of the user
     * @param responseText Text message to include in response
     * @return Response with user details or appropriate error
     */

    public static Response extractUserDetails(String username, String responseText) {

        UsersDTO userdto = null;
        try {
            // GET the user details
            User user = UserDAO.getUser(username);

            // Check if user exists.
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            // GENERATE new user DTO to send user details as response (username, user ID and role ID)
            userdto = new UsersDTO(user.getUsername(), user.getUserId(), user.getRoleId());

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(responseText).entity(userdto).build();
    }


    /**
     * Handles signup by creating a new user with specified role.
     *
     * @param user User object with username, password, and role
     * @return Response indicating success or appropriate error
     */

    public static Response validateAndRegisterUser( User user ) {

        // Null check
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
        }

        String username = user.getUsername();
        String password = user.getPassword();
        Role role = user.getRole();

        // Check for valid parameters.
        if (username==null||password==null||role==null||username.isEmpty()||password.isEmpty()||String.valueOf(role).isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
        }

        // Check if the user exists.
        if (checkUserExists(user.getUsername())) {
            return Response.status(Response.Status.CONFLICT).entity("Username already exists").build();
        }

        try {

            // Add new user
            UserDAO.addUser(user);

            return Response.ok("Success.").build();
        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Checks if a user already exists in the DB by username.
     *
     * @param username Username to check
     * @return true if user exists, false otherwise
     */


    public static boolean checkUserExists( String username ) {

        try (Connection conn = DBConnection.getConnection();

             PreparedStatement stmt = conn.prepareStatement(check_user_exist_query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    /**
     * Logs out the user by blacklisting their JWT token and expiring the token cookie.
     *
     * @param token JWT token to invalidate
     * @return Response with expired token cookie and logout message
     */

    public static Response logoutUser(String token) {
        try {

            String jti = JwtUtil.getJtiFromToken(token);

            // Add the expired token to blacklist.
            BlackListService.addToBlacklist(jti);

            // Returns back an expired cookie with no token value.
            NewCookie expiredCookie = new NewCookie("token", "", "/", null, null, 0, false, true);
            return Response.ok("Logout success").cookie(expiredCookie).build();

        } catch (JWTVerificationException jwtVerificationException) {

            System.out.println(jwtVerificationException.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("JWT Verification Exception").build();
        }
    }



    /**
     * Adds username and hashed password in the USER table
     *
     * @param username username to be stored.
     * @param password hashed password to be stored
     * @param conn DB Connection
     * @return user ID generated after inserting
     * @throws Exception if any error
     */

    public static Integer addUserAndReturnId(String username, String password, Connection conn) throws Exception {
        try (PreparedStatement statement = conn.prepareStatement(insert_un_pass_query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, password);

            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return null;
        }
    }




    /**
     * Insert user role mapping in the user_roles table
     *
     * @param userId generated user ID after inserting username and password in users table.
     * @param roleId Role ID of the new user.
     * @param conn DB Connection
     * @throws Exception if any error
     */
    public static void insertUserRoleMapping(int userId, int roleId, Connection conn) throws Exception {
        try (PreparedStatement statement = conn.prepareStatement(insert_user_role_query);) {
            statement.setInt(1, userId);
            statement.setInt(2, roleId);
            statement.executeUpdate();
        }

    }
}
