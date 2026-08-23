package com.agri.market.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.security.PublicKey;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtService")
class JwtServiceTest {

    private static final String PRIVATE_KEY_PATH = "keys/local-only/private_key.pem";
    private static final String PUBLIC_KEY_PATH = "keys/local-only/public_key.pem";

    private JwtService jwtService;
    private PublicKey publicKey;

    @BeforeEach
    void setUp() throws Exception {
        publicKey = KeyUtils.loadPublicKey(PUBLIC_KEY_PATH);
        jwtService = new JwtService(
                PRIVATE_KEY_PATH,
                PUBLIC_KEY_PATH,
                60000L,
                120000L
        );
    }

    @Nested
    @DisplayName("token generation")
    class TokenGenerationTests {

        @Test
        void shouldGenerateAccessTokenWithAccessTokenTypeClaim() {
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
        void shouldGenerateRefreshTokenWithRefreshTokenTypeClaim() {
            String token = jwtService.generateRefreshToken("user@example.com");

            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            assertThat(claims.getSubject()).isEqualTo("user@example.com");
            assertThat(claims.get("token_type", String.class)).isEqualTo("REFRESH_TOKEN");
        }

        @Test
        void shouldGenerateDifferentTokensForAccessAndRefresh() {
            String accessToken = jwtService.generateAccessToken("user@example.com");
            String refreshToken = jwtService.generateRefreshToken("user@example.com");

            assertThat(accessToken).isNotEqualTo(refreshToken);
        }
    }

    @Nested
    @DisplayName("isTokenValid")
    class IsTokenValidTests {

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
            int lastDotIndex = token.lastIndexOf('.');
            String signature = token.substring(lastDotIndex + 1);
            char[] chars = signature.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char original = chars[i];
                char replacement = original == 'A' ? 'B' : 'A';
                chars[i] = replacement;
                String candidate = token.substring(0, lastDotIndex + 1) + new String(chars);
                if (!jwtService.isTokenValid(candidate, "user@example.com")) {
                    assertThat(jwtService.isTokenValid(candidate, "user@example.com")).isFalse();
                    return;
                }
                chars[i] = original;
            }
            throw new AssertionError("Could not produce an invalid signature by single-character tampering");
        }

        @Test
        void shouldRejectMalformedToken() {
            assertThat(jwtService.isTokenValid("not-a-jwt", "user@example.com")).isFalse();
        }

        @Test
        void shouldRejectExpiredToken() throws Exception {
            JwtService expiredService = new JwtService(
                    PRIVATE_KEY_PATH,
                    PUBLIC_KEY_PATH,
                    0L,
                    2000L
            );
            String token = expiredService.generateAccessToken("user@example.com");

            assertThat(expiredService.isTokenValid(token, "user@example.com")).isFalse();
        }

        @Test
        void shouldRejectNullToken() {
            assertThat(jwtService.isTokenValid(null, "user@example.com")).isFalse();
        }
    }

    @Nested
    @DisplayName("extractUsername")
    class ExtractUsernameTests {

        @Test
        void shouldExtractUsernameFromValidToken() {
            String token = jwtService.generateAccessToken("user@example.com");

            assertThat(jwtService.extractUsername(token)).isEqualTo("user@example.com");
        }

        @Test
        void shouldThrowWhenExtractingFromMalformedToken() {
            assertThatThrownBy(() -> jwtService.extractUsername("not-a-jwt"))
                    .isInstanceOf(JwtException.class);
        }
    }

    @Nested
    @DisplayName("validateRefreshToken")
    class ValidateRefreshTokenTests {

        @Test
        void shouldPassForValidRefreshToken() {
            String token = jwtService.generateRefreshToken("user@example.com");

            jwtService.validateRefreshToken(token);
        }

        @Test
        void shouldRejectAccessTokenUsedAsRefreshToken() {
            String token = jwtService.generateAccessToken("user@example.com");

            assertThatThrownBy(() -> jwtService.validateRefreshToken(token))
                    .isInstanceOf(JwtException.class)
                    .hasMessageContaining("Invalid refresh token");
        }

        @Test
        void shouldRejectExpiredRefreshToken() throws Exception {
            JwtService expiredService = new JwtService(
                    PRIVATE_KEY_PATH,
                    PUBLIC_KEY_PATH,
                    1000L,
                    0L
            );
            String token = expiredService.generateRefreshToken("user@example.com");

            assertThatThrownBy(() -> expiredService.validateRefreshToken(token))
                    .isInstanceOf(ExpiredJwtException.class);
        }

        @Test
        void shouldRejectMalformedRefreshToken() {
            assertThatThrownBy(() -> jwtService.validateRefreshToken("not-a-jwt"))
                    .isInstanceOf(JwtException.class);
        }
    }

    @Nested
    @DisplayName("getRefreshTokenExpirationTime")
    class GetRefreshTokenExpirationTimeTests {

        @Test
        void shouldReturnExpirationInTheFuture() {
            assertThat(jwtService.getRefreshTokenExpirationTime())
                    .isAfter(LocalDateTime.now());
        }

        @Test
        void shouldReturnExpirationApproximatelyEqualToConfiguredDuration() {

            LocalDateTime before = LocalDateTime.now();

            LocalDateTime expiration =
                    jwtService.getRefreshTokenExpirationTime();

            LocalDateTime expected =
                    before.plusSeconds(120);

            assertThat(expiration)
                    .isCloseTo(
                            expected,
                            org.assertj.core.api.Assertions.within(
                                    500,
                                    java.time.temporal.ChronoUnit.MILLIS
                            )
                    );
        }
    }
}