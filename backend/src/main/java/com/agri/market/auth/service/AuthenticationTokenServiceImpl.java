package com.agri.market.auth.service;

import com.agri.market.auth.dto.AuthenticationResult;
import com.agri.market.auth.dto.RefreshTokenRequest;
import com.agri.market.auth.entity.RefreshTokenSession;
import com.agri.market.common.exception.BusinessException;
import com.agri.market.security.client.ClientInfo;
import com.agri.market.security.jwt.JwtService;
import com.agri.market.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.agri.market.common.exception.ErrorCode.ERR_USER_DISABLED;
import static com.agri.market.common.exception.ErrorCode.PERMANENT_ACCOUNT_LOCKED;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationTokenServiceImpl
        implements AuthenticationTokenService {

    private static final String TOKEN_TYPE = "Bearer";

    private final JwtService jwtService;
    private final RefreshTokenSessionService refreshTokenSessionServiceImpl;

    @Override
    @Transactional
    public AuthenticationResult createAuthenticationSession(
            final User user,
            final ClientInfo clientInfo
    ) {

        validateUserCanAuthenticate(user);

        log.debug(
                "Creating authentication session for user: {}",
                user.getId()
        );

        final GeneratedTokens tokens =
                generateTokens(user);

        refreshTokenSessionServiceImpl.createSession(
                tokens.refreshToken(),
                user,
                clientInfo
        );

        log.info(
                "Authentication session created successfully for user: {}",
                user.getId()
        );

        return buildAuthenticationResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                user
        );
    }

    @Override
    @Transactional
    public AuthenticationResult refreshAuthenticationSession(
            final RefreshTokenRequest request
    ) {

        log.debug(
                "Refresh authentication session attempt"
        );

        final String refreshToken =
                request.getRefreshToken();

        jwtService.validateRefreshToken(
                refreshToken
        );

        final RefreshTokenSession currentSession =
                refreshTokenSessionServiceImpl
                        .findValidSession(refreshToken);

        final User user =
                currentSession.getUser();

        validateUserCanAuthenticate(user);

        final GeneratedTokens tokens =
                generateTokens(user);

        refreshTokenSessionServiceImpl.rotateSession(
                currentSession,
                tokens.refreshToken()
        );

        log.info(
                "Authentication session refreshed successfully " +
                        "for user: {} and session: {}",
                user.getId(),
                currentSession.getId()
        );

        return buildAuthenticationResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                user
        );
    }

    private void validateUserCanAuthenticate(
            final User user
    ) {

        if (user == null) {
            log.warn(
                    "Authentication session rejected because user is null"
            );

            throw new BusinessException(
                    PERMANENT_ACCOUNT_LOCKED
            );
        }

        if (!user.isEnabled()) {

            log.warn(
                    "Authentication session rejected because user is disabled. User: {}",
                    user.getId()
            );

            throw new BusinessException(
                    ERR_USER_DISABLED
            );
        }

        if (user.isAccountLocked()) {

            log.warn(
                    "Authentication session rejected because user is permanently locked. User: {}",
                    user.getId()
            );

            throw new BusinessException(
                    PERMANENT_ACCOUNT_LOCKED
            );
        }
    }

    private GeneratedTokens generateTokens(
            final User user
    ) {

        log.debug(
                "Generating authentication tokens for user: {}",
                user.getId()
        );

        final String username =
                user.getUsername();

        final String accessToken =
                jwtService.generateAccessToken(username);

        final String refreshToken =
                jwtService.generateRefreshToken(username);

        return new GeneratedTokens(
                accessToken,
                refreshToken
        );
    }

    private AuthenticationResult buildAuthenticationResponse(
            final String accessToken,
            final String refreshToken,
            final User user
    ) {

        return AuthenticationResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TOKEN_TYPE)
                .hasPassword(
                        user.getPassword() != null
                                && !user.getPassword().isBlank()
                )
                .build();
    }

    private record GeneratedTokens(
            String accessToken,
            String refreshToken
    ) {
    }
}