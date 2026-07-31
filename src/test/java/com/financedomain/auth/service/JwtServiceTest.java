package com.financedomain.auth.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    // Base64 encoded 256-bit secret key for testing
    private final String testSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long testExpiration = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);
    }

    @Test
    @DisplayName("Devrait générer un token JWT valide")
    void shouldGenerateValidToken() {
        Long userId = 1L;
        String identifier = "admin";
        String role = "ADMINISTRATOR";

        String token = jwtService.generateToken(userId, identifier, role);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    @DisplayName("Devrait extraire correctement les claims du token")
    void shouldExtractCorrectClaims() {
        Long userId = 42L;
        String identifier = "771234567";
        String role = "CLIENT";

        String token = jwtService.generateToken(userId, identifier, role);

        Claims claims = jwtService.extractAllClaims(token);
        assertEquals(identifier, claims.getSubject());
        assertEquals(role, claims.get("role"));
        assertEquals(userId.toString(), claims.get("id"));
    }

    @Test
    @DisplayName("Devrait retourner false pour un token invalide ou altéré")
    void shouldReturnFalseForInvalidToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.invalidpayload.signature";

        assertFalse(jwtService.isTokenValid(invalidToken));
    }

    @Test
    @DisplayName("Devrait retourner false pour une chaîne de caractères quelconque")
    void shouldReturnFalseForMalformedToken() {
        assertFalse(jwtService.isTokenValid("not_a_jwt_token"));
        assertFalse(jwtService.isTokenValid(""));
    }
}
