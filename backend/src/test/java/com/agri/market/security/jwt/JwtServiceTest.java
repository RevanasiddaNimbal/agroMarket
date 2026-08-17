package com.agri.market.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtService")
class JwtServiceTest {

    private JwtService jwtService;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @BeforeEach
    void setUp() throws Exception {
        privateKey = KeyUtils.loadPrivateKey("keys/local-only/private_key.pem");
        publicKey = KeyUtils.loadPublicKey("keys/local-only/public_key.pem");
        jwtService = new JwtService(
                "keys/local-only/private_key.pem",
                "keys/local-only/public_key.pem",
                1000L,
                2000L
        );
    }

    @Nested
    @DisplayName("token generation")
    class TokenGenerationTests {

        @Test
        void shouldGenerateAccessTokenWithAccessTokenTypeClaim() throws Exception {
            String token = jwtService.generateAccessToken("user@example.com");

            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            assertThat(claims.getSubject()).isEqualTo("user@example.com");
            assertThat(claims.get("token_type", String.class)).isEqualTo("ACCESS_TOKEN");
        }

        @Test
        void shouldGenerateRefreshTokenWithRefreshTokenTypeClaim() throws Exception {
            String token = jwtService.generateRefreshToken("user@example.com");

            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            assertThat(claims.getSubject()).isEqualTo("user@example.com");
            assertThat(claims.get("token_type", String.class)).isEqualTo("REFRESH_TOKEN");
        }
    }

    @Nested
    @DisplayName("validation")
    class ValidationTests {

        @Test
        void shouldValidateCorrectToken() {
            String token = jwtService.generateAccessToken("user@example.com");

            assertThat(jwtService.isTokenValid(token, "user@example.com")).isTrue();
        }

        @Test
        void shouldRejectTokenWithWrongSubject() {
            String token = jwtService.generateAccessToken("user@example.com");

            assertThat(jwtService.isTokenValid(token, "other@example.com")).isFalse();
        }

        @Test
        void shouldRejectTokenWithInvalidSignature() {
            String token = jwtService.generateAccessToken("user@example.com");
            String tampered = token.substring(0, token.length() - 1)
                    + (token.endsWith("a") ? "b" : "a");

            assertThat(jwtService.isTokenValid(tampered, "user@example.com")).isFalse();
        }

        @Test
        void shouldRejectMalformedToken() {
            assertThat(jwtService.isTokenValid("not-a-jwt", "user@example.com")).isFalse();
        }

        @Test
        void shouldRejectExpiredToken() throws Exception {
            JwtService expiredService = new JwtService(
                    "keys/local-only/private_key.pem",
                    "keys/local-only/public_key.pem",
                    0L,
                    0L
            );
            String token = expiredService.generateAccessToken("user@example.com");

            assertThat(expiredService.isTokenValid(token, "user@example.com")).isFalse();
        }

        @Test
        void shouldValidateRefreshTokenOnlyForRefreshTokens() {
            String token = jwtService.generateAccessToken("user@example.com");

            assertThatThrownBy(() -> jwtService.validateRefreshToken(token))
                    .isInstanceOf(JwtException.class)
                    .hasMessageContaining("Invalid refresh token");
        }

        @Test
        void shouldRejectExpiredRefreshToken() throws Exception {
            JwtService expiredService = new JwtService(
                    "keys/local-only/private_key.pem",
                    "keys/local-only/public_key.pem",
                    0L,
                    0L
            );
            String token = expiredService.generateRefreshToken("user@example.com");

            assertThatThrownBy(() -> expiredService.validateRefreshToken(token))
                    .isInstanceOf(JwtException.class)
                    .hasMessageContaining("expired");
        }
    }

    @Test
    void shouldReturnRefreshTokenExpirationInTheFuture() {
        assertThat(jwtService.getRefreshTokenExpirationTime())
                .isAfter(java.time.LocalDateTime.now());
    }
}
