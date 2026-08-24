package com.agri.market.auth.service;

import com.agri.market.auth.entity.RefreshTokenSession;
import com.agri.market.auth.repository.RefreshTokenSessionRepository;
import com.agri.market.common.exception.BusinessException;
import com.agri.market.security.client.ClientInfo;
import com.agri.market.security.jwt.JwtService;
import com.agri.market.security.jwt.TokenHasher;
import com.agri.market.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.agri.market.common.exception.ErrorCode.INVALID_REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenSessionServiceImpl implements RefreshTokenSessionService {

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

    @Transactional
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
    public RefreshTokenSession rotateSession(
            final RefreshTokenSession currentSession,
            final String newRefreshToken
    ) {

        currentSession.setRevoked(true);
        currentSession.setRevokedAt(
                LocalDateTime.now()
        );

        refreshTokenSessionRepository.save(
                currentSession
        );

        final RefreshTokenSession newSession =
                RefreshTokenSession.builder()
                        .user(currentSession.getUser())
                        .tokenHash(
                                refreshTokenHasher.hash(
                                        newRefreshToken
                                )
                        )
                        .deviceName(
                                currentSession.getDeviceName()
                        )
                        .ipAddress(
                                currentSession.getIpAddress()
                        )
                        .expiresAt(
                                jwtService.getRefreshTokenExpirationTime()
                        )
                        .revoked(false)
                        .build();

        final RefreshTokenSession savedSession =
                refreshTokenSessionRepository.save(
                        newSession
                );

        log.info(
                "Refresh token rotated. Old session: {}, New session: {}",
                currentSession.getId(),
                savedSession.getId()
        );

        return savedSession;
    }

    @Transactional
    public void revokeSession(
            final RefreshTokenSession session
    ) {

        if (session.isRevoked()) {
            return;
        }

        session.setRevoked(true);
        session.setRevokedAt(
                LocalDateTime.now()
        );

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
                        .findAllByUser_IdAndRevokedFalse(
                                userId
                        );

        if (sessions.isEmpty()) {
            return;
        }

        final LocalDateTime now =
                LocalDateTime.now();

        sessions.forEach(session -> {
            session.setRevoked(true);
            session.setRevokedAt(now);
        });

        refreshTokenSessionRepository.saveAll(
                sessions
        );

        log.info(
                "All active refresh token sessions revoked for user: {}",
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
                    "Refresh token reuse detected. Session: {}, User: {}",
                    session.getId(),
                    session.getUser().getId()
            );

            revokeAllSessions(
                    session.getUser().getId()
            );

            throw new BusinessException(
                    INVALID_REFRESH_TOKEN
            );
        }

        if (session.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            log.warn(
                    "Expired refresh token used. Session: {}",
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
                    session.setRevokedAt(
                            LocalDateTime.now()
                    );

                    refreshTokenSessionRepository.save(
                            session
                    );

                    log.info(
                            "Previous active session revoked for user: {} and device: {}",
                            userId,
                            deviceName
                    );
                });
    }
}