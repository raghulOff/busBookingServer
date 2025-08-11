package com.example.busbooking.annotation;

import com.example.busbooking.enums.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation used to restrict access to certain methods
 * based on user roles.

 * This annotation can be applied to method-level targets.
 * During runtime, a filter should be checking
 * whether the currently authenticated user has the value in the {{@link PermissionsAllowed}} annotation
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionsAllowed {
    Permission[] value();
}