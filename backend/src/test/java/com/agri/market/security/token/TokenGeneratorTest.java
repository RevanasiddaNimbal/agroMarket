package com.agri.market.security.token;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TokenGenerator")
class TokenGeneratorTest {

    private final TokenGenerator tokenGenerator =
            new TokenGenerator();

    @Nested
    @DisplayName("generate")
    class GenerateTests {

        @Test
        @DisplayName("should generate a token")
        void shouldGenerateToken() {

            String token = tokenGenerator.generate();

            assertNotNull(token);
            assertFalse(token.isBlank());
        }

        @Test
        @DisplayName("should generate token with expected length")
        void shouldGenerateTokenWithExpectedLength() {

            String token = tokenGenerator.generate();

            assertEquals(43, token.length());
        }

        @Test
        @DisplayName("should generate valid Base64 URL encoded token")
        void shouldGenerateValidBase64UrlEncodedToken() {

            String token = tokenGenerator.generate();

            assertDoesNotThrow(() ->
                    Base64.getUrlDecoder().decode(token)
            );
        }

        @Test
        @DisplayName("should generate token containing exactly 32 decoded bytes")
        void shouldGenerateTokenContainingExactly32DecodedBytes() {

            String token = tokenGenerator.generate();

            byte[] decodedToken =
                    Base64.getUrlDecoder().decode(token);

            assertEquals(32, decodedToken.length);
        }

        @Test
        @DisplayName("should generate token without Base64 padding")
        void shouldGenerateTokenWithoutBase64Padding() {

            String token = tokenGenerator.generate();

            assertFalse(token.contains("="));
        }

        @Test
        @DisplayName("should generate URL safe token")
        void shouldGenerateUrlSafeToken() {

            String token = tokenGenerator.generate();

            assertTrue(
                    token.matches("[A-Za-z0-9_-]+")
            );
        }

        @Test
        @DisplayName("should generate different tokens on consecutive calls")
        void shouldGenerateDifferentTokensOnConsecutiveCalls() {

            String firstToken =
                    tokenGenerator.generate();

            String secondToken =
                    tokenGenerator.generate();

            assertNotEquals(firstToken, secondToken);
        }

        @Test
        @DisplayName("should generate unique tokens across multiple calls")
        void shouldGenerateUniqueTokensAcrossMultipleCalls() {

            java.util.Set<String> tokens =
                    new java.util.HashSet<>();

            for (int i = 0; i < 100; i++) {
                tokens.add(tokenGenerator.generate());
            }

            assertEquals(100, tokens.size());
        }
    }
}