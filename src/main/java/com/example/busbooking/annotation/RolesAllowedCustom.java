package com.example.busbooking.annotation;
import java.lang.annotation.*;


// Annotation to check user roles.

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RolesAllowedCustom {
    int[] value();
}
