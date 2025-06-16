package com.example.busbooking.dao.base;

import com.example.busbooking.db.DBConnection;
import com.example.busbooking.model.Role;
import com.example.busbooking.model.User;
import com.example.busbooking.security.PasswordUtil;
import jakarta.ws.rs.core.Response;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {


    public static final String insert_un_pass_query = "INSERT INTO users (username, password) VALUES (?, ?)";
    public static final String select_role_id_query = "SELECT role_id FROM roles WHERE role_name = ?";
    public static final String insert_user_role_query = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
    public static final String get_user_details_query = "SELECT u.username, u.password, r.role_name, r.role_id, ur.user_id " +
            "FROM users u " +
            "JOIN user_roles ur ON u.user_id = ur.user_id " +
            "JOIN roles r ON ur.role_id = r.role_id " +
            "WHERE u.username = ?";



    // adding new user with any role
    public static void addUser(User user) {
        Connection conn = null;
        PreparedStatement insertUserStmt = null;
        PreparedStatement getRoleIdStmt = null;
        PreparedStatement insertUserRoleStmt = null;

        try {
            conn = DBConnection.getConnection();

            String hashPassword = PasswordUtil.hashPassword(user.getPassword());
            insertUserStmt = conn.prepareStatement(insert_un_pass_query, Statement.RETURN_GENERATED_KEYS);
            insertUserStmt.setString(1, user.getUsername());
            insertUserStmt.setString(2, hashPassword);
            insertUserStmt.executeUpdate();

            ResultSet generatedKeys = insertUserStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Failed to retrieve generated user_id.");
            }
            int userId = generatedKeys.getInt(1);


            getRoleIdStmt = conn.prepareStatement(select_role_id_query);
            getRoleIdStmt.setString(1, user.getRole().name().toUpperCase()); // role names like "admin"
            ResultSet roleRs = getRoleIdStmt.executeQuery();
            if (!roleRs.next()) {
                throw new SQLException("Role not found: " + user.getRole().name());
            }
            int roleId = roleRs.getInt("role_id");


            insertUserRoleStmt = conn.prepareStatement(insert_user_role_query);
            insertUserRoleStmt.setInt(1, userId);
            insertUserRoleStmt.setInt(2, roleId);
            insertUserRoleStmt.executeUpdate();


            System.out.println("User added and role mapped.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // retrieving user from DB
    public static User getUser(String username) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(get_user_details_query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                return new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role_name").toUpperCase()),
                        rs.getInt("role_id"),
                        rs.getInt("user_id")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // Returns all the available roles.
    public static Response getRoles() {
        List<Map<String, Object>> roles = new ArrayList<>();
        for (Role r : Role.values()) {
            Map<String, Object> role = new HashMap<>();
            role.put("roleId", r.getId());
            role.put("roleName", r.name());

            roles.add(role);
        }

        return Response.ok("Got all the roles").entity(roles).build();
    }






}
