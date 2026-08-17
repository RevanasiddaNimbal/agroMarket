package com.agri.market.security.jwt;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class TokenHasher {

    public String hash(final String token) {
        try {
            final MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            final byte[] hash =
                    digest.digest(token.getBytes(StandardCharsets.UTF_8));

            final StringBuilder result = new StringBuilder();

            for (final byte value : hash) {
                result.append(String.format("%02x", value));
            }

            return result.toString();

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available",
                    exception
            );
        }
    }
}
