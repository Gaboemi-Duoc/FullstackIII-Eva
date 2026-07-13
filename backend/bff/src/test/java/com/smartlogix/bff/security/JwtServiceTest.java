package com.smartlogix.bff.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    @DisplayName("generateToken: genera un token no nulo y con formato JWT")
    void generateToken_generaTokenValido() {
        String token = jwtService.generateToken("admin");

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("extractUsername: extrae el username correcto del token")
    void extractUsername_extraeUsernameCorrecto() {
        String token = jwtService.generateToken("admin");

        String username = jwtService.extractUsername(token);

        assertEquals("admin", username);
    }

    @Test
    @DisplayName("isTokenValid: retorna true para token recién generado")
    void isTokenValid_tokenValido_retornaTrue() {
        String token = jwtService.generateToken("admin");

        boolean esValido = jwtService.isTokenValid(token, "admin");

        assertTrue(esValido);
    }

    @Test
    @DisplayName("isTokenValid: retorna false si el username no coincide")
    void isTokenValid_usernameNoCoincide_retornaFalse() {
        String token = jwtService.generateToken("admin");

        boolean esValido = jwtService.isTokenValid(token, "otro_usuario");

        assertFalse(esValido);
    }

    @Test
    @DisplayName("extractClaim: permite extraer cualquier claim del token")
    void extractClaim_extraeClaimCorrectamente() {
        String token = jwtService.generateToken("admin");

        String subject = jwtService.extractClaim(token, claims -> claims.getSubject());

        assertEquals("admin", subject);
    }
}