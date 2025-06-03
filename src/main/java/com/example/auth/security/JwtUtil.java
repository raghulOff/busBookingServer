package com.example.auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.auth.model.User;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "thisisthesecretkeyforserverthisisthesecretkeyforserverthisisthesecretkeyforserver";
    private static final long EXPIRATION_TIME = 100000000;

    public static String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("userId", user.getUserId())
                .withClaim("roleId", user.getRoleId())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static String getSecret() {return SECRET;}
    public static DecodedJWT verifyToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        return verifier.verify(token);
    }
}
