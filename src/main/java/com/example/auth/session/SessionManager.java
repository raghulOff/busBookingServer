package com.example.auth.session;



import com.example.auth.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    // Map token -> User object
    private static final Map<String, User> sessions = new ConcurrentHashMap<>();

    public static String createSession(User user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }

    public static User getUser(String token) {
        return sessions.get(token);
    }

    public static void invalidate(String token) {
        sessions.remove(token);
    }
}
