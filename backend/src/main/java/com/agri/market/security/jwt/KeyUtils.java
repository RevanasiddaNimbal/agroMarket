package com.agri.market.security.jwt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class KeyUtils {

    private static final String RSA_ALGORITHM = "RSA";

    private static final String PRIVATE_KEY_BEGIN =
            "-----BEGIN PRIVATE KEY-----";

    private static final String PRIVATE_KEY_END =
            "-----END PRIVATE KEY-----";

    private static final String PUBLIC_KEY_BEGIN =
            "-----BEGIN PUBLIC KEY-----";

    private static final String PUBLIC_KEY_END =
            "-----END PUBLIC KEY-----";

    private KeyUtils() {
        // Utility class.
    }

    public static PrivateKey loadPrivateKey(
            final String resourcePath
    ) throws GeneralSecurityException, IOException {

        final byte[] keyBytes = decodePemKey(
                resourcePath,
                PRIVATE_KEY_BEGIN,
                PRIVATE_KEY_END
        );

        final PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(keyBytes);

        return KeyFactory
                .getInstance(RSA_ALGORITHM)
                .generatePrivate(keySpec);
    }

    public static PublicKey loadPublicKey(
            final String resourcePath
    ) throws GeneralSecurityException, IOException {

        final byte[] keyBytes = decodePemKey(
                resourcePath,
                PUBLIC_KEY_BEGIN,
                PUBLIC_KEY_END
        );

        final X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(keyBytes);

        return KeyFactory
                .getInstance(RSA_ALGORITHM)
                .generatePublic(keySpec);
    }

    private static byte[] decodePemKey(
            final String resourcePath,
            final String beginMarker,
            final String endMarker
    ) throws IOException {

        final String pem = readKeyFromResource(resourcePath);

        final String encodedKey = pem
                .replace(beginMarker, "")
                .replace(endMarker, "")
                .replaceAll("\\s", "");

        if (encodedKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Key content is empty: " + resourcePath
            );
        }

        try {
            return Base64.getDecoder().decode(encodedKey);
        } catch (final IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid Base64 key content: " + resourcePath,
                    exception
            );
        }
    }

    private static String readKeyFromResource(
            final String resourcePath
    ) throws IOException {

        try (InputStream inputStream =
                     KeyUtils.class
                             .getClassLoader()
                             .getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                throw new IllegalArgumentException(
                        "Key resource not found: " + resourcePath
                );
            }

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}