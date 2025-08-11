package com.example.busbooking.filter;

import com.example.busbooking.annotation.PermissionsAllowed;

import com.example.busbooking.enums.Permission;

import com.example.busbooking.registry.RolePermissionRegistry;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Method;



/**
 * Authorization filter that checks whether the user (based on roleId) has permission
 * to access the requested resource, using {{@link PermissionsAllowed}} annotation.
 */

@Provider
@Priority(Priorities.AUTHORIZATION)
public class RoleAuthorizationFilter implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();

        // Chain and let the api call reach the controller if the endpoints are login/signup
        if (path.contains("login") || path.contains("signup")) {
            return;
        }

        Method method = resourceInfo.getResourceMethod();

        // Get the Annotation values of requested method.
        PermissionsAllowed permissionsAllowed = method.getAnnotation(PermissionsAllowed.class);

        int roleId = (int) requestContext.getProperty("roleId");
        int userId = (int) requestContext.getProperty("userId");

        String username = (String) requestContext.getProperty("username");



        try {
            // Iterate through each permission defined in @PermissionsAllowed
            for (Permission permission : permissionsAllowed.value()) {
                // Check if the role has the required permission from the registry
                if (RolePermissionRegistry.hasPermission(roleId, permission.name())) {
                    // Propagate the user context and allow request to proceed
                    requestContext.setProperty("userId", userId);
                    requestContext.setProperty("roleId", roleId);
                    requestContext.setProperty("username", username);
                    return;
                }
            }
            // If none of the permissions match, deny access

            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Insufficient permission").build());


        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Authorization failed").build());
        }
    }
}
