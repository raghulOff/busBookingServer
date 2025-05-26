package com.example.auth.repository;

import com.example.auth.Role;
import com.example.auth.User;
import com.example.auth.db.DBConnection;
import com.example.auth.security.PasswordUtil;

import javax.swing.plaf.nimbus.State;
import java.sql.*;

public class UserRepository {

    public static boolean exists(String username) {
        String query = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // true if user exists

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void addUser(User user) {
        Connection conn = null;
        PreparedStatement insertUserStmt = null;
        PreparedStatement getRoleIdStmt = null;
        PreparedStatement insertUserRoleStmt = null;

        try {
            conn = DBConnection.getConnection();

            String hashPassword = PasswordUtil.hashPassword(user.getPassword());
            insertUserStmt = conn.prepareStatement(
                    "INSERT INTO users (username, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            insertUserStmt.setString(1, user.getUsername());
            insertUserStmt.setString(2, hashPassword);
            insertUserStmt.executeUpdate();

            ResultSet generatedKeys = insertUserStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Failed to retrieve generated user_id.");
            }
            int userId = generatedKeys.getInt(1);


            getRoleIdStmt = conn.prepareStatement("SELECT role_id FROM roles WHERE role_name = ?");
            getRoleIdStmt.setString(1, user.getRole().name().toLowerCase()); // role names like "admin"
            ResultSet roleRs = getRoleIdStmt.executeQuery();
            if (!roleRs.next()) {
                throw new SQLException("Role not found: " + user.getRole().name());
            }
            int roleId = roleRs.getInt("role_id");


            insertUserRoleStmt = conn.prepareStatement("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)");
            insertUserRoleStmt.setInt(1, userId);
            insertUserRoleStmt.setInt(2, roleId);
            insertUserRoleStmt.executeUpdate();


            System.out.println("User added and role mapped.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static User getUser(String username) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.username, u.password, r.role_name " +
                             "FROM users u " +
                             "JOIN user_roles ur ON u.user_id = ur.user_id " +
                             "JOIN roles r ON ur.role_id = r.role_id " +
                             "WHERE u.username = ?")) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role_name").toUpperCase())
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }






//    public static void addUser(User user) {
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)")) {
//            stmt.setString(1, user.getUsername());
//            stmt.setString(2, user.getPassword());
//            stmt.setString(3, user.getRole().name());
//            stmt.executeUpdate();
//            System.out.println("db connected");
//            conn.close();
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static User getUser(String username) {
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
//            stmt.setString(1, username);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return new User(
//                        rs.getString("username"),
//                        rs.getString("password"),
//                        Role.valueOf(rs.getString("role"))
//                );
//            }
//            System.out.println("db connected");
//            conn.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


}
