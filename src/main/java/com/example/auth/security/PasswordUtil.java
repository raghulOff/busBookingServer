package com.example.auth.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtil {
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verifyPassword(String password, String bcryptHash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), bcryptHash);
        return result.verified;
    }
}
