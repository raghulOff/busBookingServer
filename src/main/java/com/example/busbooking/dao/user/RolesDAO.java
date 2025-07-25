//package com.example.busbooking.dao.user;
//
//import com.example.busbooking.db.DBConnection;
//import com.example.busbooking.model.Role;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.List;
//
//public class RolesDAO {
//
//    public Role getRoleById( int id) throws Exception {
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement("SELECT role_id, role_name FROM roles WHERE role_id = ?")) {
//
//            stmt.setInt(1, id);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return new Role(rs.getInt("role_id"), rs.getString("role_name"));
//            } else {
//                throw new IllegalArgumentException("Invalid role ID: " + id);
//            }
//        }
//    }
//
//    public static List<Role> getAllRoles() throws Exception {
//        List<Role> roles = new ArrayList<>();
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement("SELECT role_id, role_name FROM roles")) {
//
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                roles.add(new Role(rs.getInt("role_id"), rs.getString("role_name")));
//            }
//        }
//        return roles;
//    }
//}
