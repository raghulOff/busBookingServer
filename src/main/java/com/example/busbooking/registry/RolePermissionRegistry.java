package com.example.busbooking.registry;

import java.sql.*;
import java.util.*;

/**
 * Registry class for loading and accessing role-permission mappings from the database.
 *
 * This class acts as an in-memory cache that maps each role ID to a set of permission names.
 * It is used for enforcing role-based access control (RBAC) throughout the system.
 */

public class RolePermissionRegistry {

    private static final Map<Integer, Set<String>> rolePermissionsMap = new HashMap<>();

    public static void load(Connection conn) throws SQLException {
        String sql = """
            SELECT r.role_id, r.role_name, p.permission_name
            FROM role_permissions rp
            JOIN roles r ON rp.role_id = r.role_id
            JOIN permissions p ON rp.permission_id = p.permission_id
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                String permissionName = rs.getString("permission_name");
                Integer roleId = rs.getInt("role_id");

                // EXPANDED CODE FOR REFERENCE
            /**
                Set<String> permissions = rolePermissionsMap.get(roleId);
                if (permissions == null) {
                    permissions = new HashSet<>();
                    rolePermissionsMap.put(roleId, permissions);
                }
                permissions.add(permissionName);
             */

                rolePermissionsMap
                        .computeIfAbsent(roleId, k -> new HashSet<>())
                        .add(permissionName);
            }
        }

        System.out.println("Role-permission cache loaded.");
    }




    public static boolean hasPermission(Integer roleId, String permissionName) {
        return rolePermissionsMap.getOrDefault(roleId, Set.of()).contains(permissionName);
    }

    public static Set<String> getPermissionsForRole(Integer roleId) {
        return rolePermissionsMap.getOrDefault(roleId, Set.of());
    }
}
