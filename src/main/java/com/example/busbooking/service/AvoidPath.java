package com.example.busbooking.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// This method is used in the authentication and authorization filter to avoid login and signup endpoints passed through filters.
public class AvoidPath {
    public static boolean avoidPath(String path) {
        List<String> includePathValues = new ArrayList<>(Arrays.asList("login", "signup"));
        for (String p : includePathValues) {
            if (path.contains(p)) {
                return false;
            }
        }
        return true;
    }
}
