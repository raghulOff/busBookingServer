//package com.example.auth.session;
//
//
//
//
//import com.example.auth.User;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.ws.rs.container.ContainerRequestContext;
//import jakarta.ws.rs.core.Context;
//import jakarta.ws.rs.core.Cookie;
//import jakarta.ws.rs.core.HttpHeaders;
//
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//public class SessionManager {
//
//    // Map token -> User object
//    @Context
//    private static HttpHeaders httpHeaders;
//    private static final Map<String, User> sessions = new ConcurrentHashMap<>();
//
//
//
//    public static String createSession(User user, String t) {
//
//
//        if (t != null) {
//            System.out.println("already token is there");
//            invalidate(t);
//        }
//
//        UUID uuid = UUID.randomUUID();
//        String token = uuid.toString();
//        sessions.put(token, user);
////        int i = 0;
////        for (Map.Entry<String, User> s:sessions.entrySet()) {
////            System.out.println("Token no: " + (i+1) + " -> " + s.getKey() + " " + s.getValue().getUsername());
////            i++;
////        }
////        i = 0;
//
//        return token;
//    }
//
//    public static User getUser(String token) {
//        return sessions.get(token);
//    }
//
//    public static void invalidate(String token) {
//        sessions.remove(token);
//    }
//}
