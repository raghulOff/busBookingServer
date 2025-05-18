package com.example.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthRepo {
    private static final Map<String, User> users = new ConcurrentHashMap<>();

    static {
        // Add a default admin
        users.put("admin", new User("admin", "admin123", Role.ADMIN));
    }
    public static User getUser(String username) {
        return users.get(username);
    }

    public static void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public static void removeUser(String username) {
        users.remove(username);
    }

    public static boolean exists(String username) {
        return users.containsKey(username);
    }

    public static Map<String, User> getAllUsers() {
        return users;
    }
}
