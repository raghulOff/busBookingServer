package com.example.busbooking.annotation;
import com.example.busbooking.model.Role;

import java.lang.annotation.*;


// Annotation to check user roles.

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RolesAllowedCustom {
    Role[] value();
}
