package com.example.busbooking.service;
import java.util.HashSet;
import java.util.Set;

public class BlackListService {
    private static final Set<String> blacklist = new HashSet<>();

    // Add a token's jti to the blacklist
    public static void addToBlacklist(String jti) {
        blacklist.add(jti);
    }

    // Check if the token's jti is in the blacklist
    public static boolean isBlacklisted(String jti) {
        return blacklist.contains(jti);
    }

    // Clear the blacklist (useful for testing or clearing after server restart)
    public static void clearBlacklist() {
        blacklist.clear();
    }
}
