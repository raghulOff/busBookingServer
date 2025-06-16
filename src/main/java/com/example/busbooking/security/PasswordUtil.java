package com.example.busbooking.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtil {
    // hashing the password using bcrypt.
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    // entered password and stored password is verified using bcrypt verifier.
    public static boolean verifyPassword(String password, String bcryptHash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), bcryptHash);
        return result.verified;
    }
}
