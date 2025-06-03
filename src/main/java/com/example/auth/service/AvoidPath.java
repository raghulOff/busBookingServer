package com.example.auth.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AvoidPath {
    public static boolean avoidPath(String path) {
        List<String> includePathValues = new ArrayList<>(Arrays.asList("add-user", "add-developer", "admin-home", "dev-home", "home"));
        for (String p : includePathValues) {
            if (path.contains(p)) {
                return true;
            }
        }
        return false;
    }
}
