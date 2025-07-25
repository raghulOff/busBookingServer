package com.example.busbooking.annotation;
import com.example.busbooking.model.Role;

import java.lang.annotation.*;

/**
 * Custom annotation used to restrict access to certain methods
 * based on user roles.

 * This annotation can be applied to both class-level and method-level targets.
 * During runtime, a filter should be checking
 * whether the currently authenticated user has at least one of the roles specified.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RolesAllowedCustom {
    Role[] value();
}
