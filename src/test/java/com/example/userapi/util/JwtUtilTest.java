package com.example.userapi.util;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class JwtUtilTest {

    @Test
    void testGenerateToken() {
        String token = JwtUtil.generateToken("email@email.com");
        assertNotNull(token);
    }

    @Test
    void testVerifyToken_validToken() {
        String token = JwtUtil.generateToken("email@email.com");
        String email = JwtUtil.verifyToken(token).getSubject();

        assertEquals("email@email.com", email);
    }

    @Test
    void testVerifyToken_InvalidToken() {
        String invalidToken = "invalidToken";
        assertThrows(JWTVerificationException.class, () -> {
            JwtUtil.verifyToken(invalidToken);
        });
    }
}