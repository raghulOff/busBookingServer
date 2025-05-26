package com.example.auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.auth.User;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "thisisthesecretkeyforserver";
    private static final long EXPIRATION_TIME = 1000000;

    public static String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getUsername())
//                .withClaim("role", user.isAdmin() ? "ADMIN" : "USER")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static DecodedJWT verifyToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        return verifier.verify(token);
    }
}
