package com.agri.market.auth.service;

import com.agri.market.auth.dto.ClientInfo;
import com.agri.market.auth.entity.RefreshTokenSession;
import com.agri.market.auth.repository.RefreshTokenSessionRepository;
import com.agri.market.exception.BusinessException;
import com.agri.market.security.jwt.JwtService;
import com.agri.market.security.jwt.TokenHasher;
import com.agri.market.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.agri.market.exception.ErrorCode.INVALID_REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenSessionService {

    private final RefreshTokenSessionRepository refreshTokenSessionRepository;
    private final TokenHasher refreshTokenHasher;
    private final JwtService jwtService;

    @Transactional
    public RefreshTokenSession createSession(
            final String refreshToken,
            final User user,
            final ClientInfo clientInfo
    ) {

        revokeExistingDeviceSession(
                user.getId(),
                clientInfo.deviceName()
        );

        final RefreshTokenSession session =
                RefreshTokenSession.builder()
                        .user(user)
                        .tokenHash(
                                refreshTokenHasher.hash(refreshToken)
                        )
                        .deviceName(clientInfo.deviceName())
                        .ipAddress(clientInfo.ipAddress())
                        .expiresAt(
                                jwtService.getRefreshTokenExpirationTime()
                        )
                        .revoked(false)
                        .build();

        final RefreshTokenSession savedSession =
                refreshTokenSessionRepository.save(session);

        log.info(
                "Authentication session created for user: {} on device: {}",
                user.getId(),
                clientInfo.deviceName()
        );

        return savedSession;
    }

    @Transactional(readOnly = true)
    public RefreshTokenSession findValidSession(
            final String refreshToken
    ) {

        final String tokenHash =
                refreshTokenHasher.hash(refreshToken);

        final RefreshTokenSession session =
                refreshTokenSessionRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Refresh token session not found"
                            );

                            return new BusinessException(
                                    INVALID_REFRESH_TOKEN
                            );
                        });

        validateSession(session);

        return session;
    }

    @Transactional
    public void rotateSession(
            final RefreshTokenSession session,
            final String newRefreshToken
    ) {

        session.setTokenHash(
                refreshTokenHasher.hash(newRefreshToken)
        );

        session.setExpiresAt(
                jwtService.getRefreshTokenExpirationTime()
        );

        session.setRevoked(false);
        session.setRevokedAt(null);

        refreshTokenSessionRepository.save(session);

        log.info(
                "Refresh token session rotated: {}",
                session.getId()
        );
    }

    @Transactional
    public void revokeSession(
            final RefreshTokenSession session
    ) {

        session.setRevoked(true);
        session.setRevokedAt(LocalDateTime.now());

        refreshTokenSessionRepository.save(session);

        log.info(
                "Refresh token session revoked: {}",
                session.getId()
        );
    }

    @Transactional
    public void revokeSession(
            final String refreshToken
    ) {

        final RefreshTokenSession session =
                findSession(refreshToken);

        revokeSession(session);
    }

    @Transactional
    public void revokeAllSessions(
            final String userId
    ) {

        final List<RefreshTokenSession> sessions =
                refreshTokenSessionRepository
                        .findAllByUser_IdAndRevokedFalse(userId);

        sessions.forEach(this::revokeSession);

        log.info(
                "All active sessions revoked for user: {}",
                userId
        );
    }

    private RefreshTokenSession findSession(
            final String refreshToken
    ) {

        final String tokenHash =
                refreshTokenHasher.hash(refreshToken);

        return refreshTokenSessionRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> {

                    log.warn(
                            "Refresh token session not found"
                    );

                    return new BusinessException(
                            INVALID_REFRESH_TOKEN
                    );
                });
    }

    private void validateSession(
            final RefreshTokenSession session
    ) {

        if (session.isRevoked()) {

            log.warn(
                    "Attempt to use revoked refresh token session: {}",
                    session.getId()
            );

            throw new BusinessException(
                    INVALID_REFRESH_TOKEN
            );
        }

        if (session.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            log.warn(
                    "Attempt to use expired refresh token session: {}",
                    session.getId()
            );

            throw new BusinessException(
                    INVALID_REFRESH_TOKEN
            );
        }
    }

    private void revokeExistingDeviceSession(
            final String userId,
            final String deviceName
    ) {

        refreshTokenSessionRepository
                .findByUser_IdAndDeviceNameAndRevokedFalse(
                        userId,
                        deviceName
                )
                .ifPresent(session -> {

                    session.setRevoked(true);
                    session.setRevokedAt(LocalDateTime.now());

                    refreshTokenSessionRepository.save(session);

                    log.info(
                            "Previous active session revoked for user: {} and device: {}",
                            userId,
                            deviceName
                    );
                });
    }
}