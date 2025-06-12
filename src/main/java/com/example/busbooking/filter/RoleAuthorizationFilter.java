package com.example.busbooking.filter;

import com.example.busbooking.annotation.RolesAllowedCustom;

import com.example.busbooking.service.AvoidPath;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;


@Provider
@Priority(Priorities.AUTHORIZATION)
public class RoleAuthorizationFilter implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        if (!AvoidPath.avoidPath(path)) {
            return;
        }

        Method method = resourceInfo.getResourceMethod();
        RolesAllowedCustom roleAnnotation = method.getAnnotation(RolesAllowedCustom.class);
        int roleId = (int) requestContext.getProperty("roleId");
        int userId = (int) requestContext.getProperty("userId");
        String username = (String) requestContext.getProperty("username");

        try {

            List<Integer> allowedRoles = Arrays.stream(roleAnnotation.value()).boxed().toList();
            if (!allowedRoles.contains(roleId)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Insufficient permission").build());
            }

            requestContext.setProperty("userId", userId);
            requestContext.setProperty("roleId", roleId);
            requestContext.setProperty("username", username);

        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Authorization failed").build());
        }
    }
}
