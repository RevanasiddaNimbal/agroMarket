package com.agri.market.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private static final String TOKEN_TYPE = "token_type";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtService(
            @Value("${jwt.private-key-path}") final String privateKeyPath,

            @Value("${jwt.public-key-path}") final String publicKeyPath,

            @Value("${app.security.jwt.access-token-expiration}") final long accessTokenExpiration,

            @Value("${app.security.jwt.refresh-token-expiration}") final long refreshTokenExpiration
    ) throws Exception {

        this.privateKey = KeyUtils.loadPrivateKey(privateKeyPath);
        this.publicKey = KeyUtils.loadPublicKey(publicKeyPath);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(final String username) {

        return buildToken(
                username,
                Map.of(TOKEN_TYPE, ACCESS_TOKEN),
                accessTokenExpiration
        );
    }

    public String generateRefreshToken(final String username) {

        return buildToken(
                username,
                Map.of(TOKEN_TYPE, REFRESH_TOKEN),
                refreshTokenExpiration
        );
    }

    public boolean isTokenValid(
            final String token,
            final String expectedUsername
    ) {

        try {
            final Claims claims = extractClaims(token);

            final String username = claims.getSubject();

            return expectedUsername.equals(username)
                    && !isExpired(claims);

        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public String extractUsername(final String token) {

        return extractClaims(token).getSubject();
    }

    public void validateRefreshToken(final String refreshToken) {

        final Claims claims = extractClaims(refreshToken);

        validateRefreshTokenClaims(claims);
    }

    public LocalDateTime getRefreshTokenExpirationTime() {

        return LocalDateTime.now()
                .plusNanos(refreshTokenExpiration * 1_000_000);
    }

    private String buildToken(
            final String username,
            final Map<String, Object> claims,
            final long expiration
    ) {

        final Date issuedAt = new Date();

        final Date expirationDate =
                new Date(issuedAt.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(privateKey)
                .compact();
    }

    private void validateRefreshTokenClaims(
            final Claims claims
    ) {

        final String tokenType =
                claims.get(TOKEN_TYPE, String.class);

        if (!REFRESH_TOKEN.equals(tokenType)) {
            throw new JwtException("Invalid refresh token");
        }

        if (isExpired(claims)) {
            throw new JwtException(
                    "Refresh token has expired"
            );
        }
    }

    private boolean isExpired(final Claims claims) {

        return claims.getExpiration().before(new Date());
    }

    private Claims extractClaims(final String token) {

        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}