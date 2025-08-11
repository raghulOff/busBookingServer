package com.example.busbooking.enums;

import com.example.busbooking.db.DBInitializer;

import java.util.List;



/**
 * NOTE: For demo/testing only. For DB Initialization purpose.
 * Enum to define a mapping between USER roles and their associated permissions.

 * This acts as a central place to associate each role with the list of permissions
 * it is allowed to perform. The actual permission lists are initialized in {@link DBInitializer}.

 * */
public enum RolePermissionMapping {
    ADMIN(DBInitializer.adminPermissions),
    USER(DBInitializer.userPermissions);

    private final List<Permission> permissions;

    RolePermissionMapping(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}