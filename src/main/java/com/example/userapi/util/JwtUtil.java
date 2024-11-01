package com.example.userapi.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "secret-key";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

    public static String generateToken(String email) {
        return JWT.create().withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(new Date().getTime() + 1000 * 60 * 60 * 24))
                .sign(ALGORITHM);
    }

    public static DecodedJWT verifyToken(String token) throws JWTVerificationException {
        JWTVerifier jtwVerify = JWT.require(ALGORITHM).build();
        return jtwVerify.verify(token);
    }
}
