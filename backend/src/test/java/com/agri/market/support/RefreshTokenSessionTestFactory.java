package com.agri.market.support;

import com.agri.market.auth.entity.RefreshTokenSession;
import com.agri.market.user.entity.User;

import java.time.LocalDateTime;

public final class RefreshTokenSessionTestFactory {

    private RefreshTokenSessionTestFactory() {
    }

    public static RefreshTokenSession activeSession(
            final User user,
            final String tokenHash,
            final String deviceName
    ) {
        return session(
                user,
                tokenHash,
                deviceName,
                "10.0.0.1",
                LocalDateTime.now().plusDays(7),
                false
        );
    }

    public static RefreshTokenSession session(
            final User user,
            final String tokenHash,
            final String deviceName,
            final String ipAddress,
            final LocalDateTime expiresAt,
            final boolean revoked
    ) {
        final RefreshTokenSession session = RefreshTokenSession.builder()
                .id("session-id")
                .user(user)
                .tokenHash(tokenHash)
                .deviceName(deviceName)
                .ipAddress(ipAddress)
                .expiresAt(expiresAt)
                .revoked(revoked)
                .build();
        session.setRevokedAt(revoked ? LocalDateTime.now() : null);
        return session;
    }
}

