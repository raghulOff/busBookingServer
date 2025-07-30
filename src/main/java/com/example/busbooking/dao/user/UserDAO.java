package com.example.busbooking.dao.user;

import com.example.busbooking.db.DBConnection;
import com.example.busbooking.model.Role;
import com.example.busbooking.model.User;
import com.example.busbooking.security.PasswordUtil;
import com.example.busbooking.service.AuthService;
import jakarta.ws.rs.core.Response;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.busbooking.db.DBConstants.*;


/**
 * DAO class for handling user-related database operations such as
 * adding users, retrieving users, and fetching available roles.
 */
public class UserDAO {

    // SQL to get the user details.
    public static final String get_user_details_query = String.format("SELECT u.username, u.password, r.role_name, r.role_id, ur.user_id " +
            "FROM %s u " +
            "JOIN %s ur ON u.user_id = ur.user_id " +
            "JOIN %s r ON ur.role_id = r.role_id " +
            "WHERE u.username = ?", USERS, USER_ROLES, ROLES);


    /**
     * Adds a new user and associates the user with the specified role.
     * The password is hashed before storing in the database.
     *
     * @param user The user object containing username, password, and role
     * @throws Exception If any SQL or hashing error occurs
     */

    public static void addUser( User user ) throws Exception {

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);


            // Hashing the password before storing it in the DB
            String hashPassword = PasswordUtil.hashPassword(user.getPassword());

            Integer userId = AuthService.addUserAndReturnId(user.getUsername(), hashPassword, conn);

            if (userId == null) {
                throw new SQLException("Failed to retrieve generated user_id.");
            }

            int roleId = user.getRole().getId();

            AuthService.insertUserRoleMapping(userId, roleId, conn);

            conn.commit(); // Commit transaction

        } catch (Exception e) {

            DBConnection.rollbackConnection(conn);
            System.out.println(e.getMessage());
            throw e;

        } finally {
            DBConnection.closeConnection(conn);
        }
    }


    /**
     * Retrieves a user object from the database using the username.
     *
     * @param username Username to be searched
     * @return A User object if found, otherwise null
     */

    public static User getUser( String username ) throws Exception {

        try (Connection conn = DBConnection.getConnection();
             // GET the user details
             PreparedStatement stmt = conn.prepareStatement(get_user_details_query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return new User(
                            rs.getString("username"),
                            rs.getString("password"),
                            Role.valueOf(rs.getString("role_name").toUpperCase()),
                            rs.getInt("role_id"),
                            rs.getInt("user_id")
                    );
                }
            }
        }
        return null;
    }


    /**
     * Retrieves all available user roles defined in the system.
     *
     * @return A Response containing list of roles with their IDs and names
     */

    public static Response getRoles() {

        // List to store all the roles.
        List<Map<String, Object>> roles = new ArrayList<>();
        for (Role r : Role.values()) {
            // Map to store the role ID and role Name.
            Map<String, Object> role = new HashMap<>();
            role.put("roleId", r.getId());
            role.put("roleName", r.name());

            roles.add(role);
        }

        return Response.ok("Got all the roles").entity(roles).build();
    }

}
