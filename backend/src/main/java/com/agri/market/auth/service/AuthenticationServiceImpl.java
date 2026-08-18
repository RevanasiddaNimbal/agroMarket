package com.agri.market.auth.service;

import com.agri.market.auth.dto.*;
import com.agri.market.exception.BusinessException;
import com.agri.market.role.entity.Role;
import com.agri.market.role.repository.RoleRepository;
import com.agri.market.security.jwt.JwtService;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

import static com.agri.market.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl
        implements AuthenticationService {

    private static final String USER_ROLE = "USER";
    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenSessionService refreshTokenSessionService;

    private final EmailVerificationService emailVerificationService;

    @Override
    @Transactional
    public RegistrationResponse register(
            final RegistrationRequest request,
            final ClientInfo clientInfo
    ) {

        final String email =
                normalizeEmail(request.getEmail());

        log.debug(
                "Registration attempt for email: {}",
                email
        );

        validateRegistration(
                request,
                email
        );

        final Role userRole =
                getDefaultUserRole();

        final User user =
                User.builder()
                        .fullName(request.getFullName())
                        .email(email)
                        .phoneNumber(request.getPhoneNumber())
                        .password(
                                passwordEncoder.encode(
                                        request.getPassword()
                                )
                        )
                        .roles(List.of(userRole))
                        .build();

        user.setEmailVerified(false);
        user.setEnabled(false);

        final User savedUser =
                userRepository.save(user);

        emailVerificationService
                .sendVerificationEmail(savedUser);

        log.info(
                "User registered successfully. Verification email sent for user: {}",
                savedUser.getId()
        );

        return RegistrationResponse.builder()
                .message(
                        "Registration successful. " +
                                "Please verify your email address before logging in."
                )
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponse login(
            final AuthenticationRequest request,
            final ClientInfo clientInfo
    ) {

        final String email =
                normalizeEmail(request.getEmail());

        log.debug(
                "Login attempt for email: {}",
                email
        );

        final Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.getPassword()
                        )
                );

        final User user =
                (User) authentication.getPrincipal();

        log.info(
                "User authenticated successfully with email: {}",
                email
        );

        return createAuthenticationSession(
                user,
                clientInfo
        );
    }

    @Override
    @Transactional
    public AuthenticationResponse refreshToken(
            final RefreshTokenRequest request
    ) {

        log.debug("Refresh token attempt");

        final String refreshToken =
                request.getRefreshToken();

        final var currentSession =
                refreshTokenSessionService
                        .findValidSession(refreshToken);

        jwtService.validateRefreshToken(
                refreshToken
        );

        final User user =
                currentSession.getUser();

        final GeneratedTokens tokens =
                generateTokens(user);

        refreshTokenSessionService.rotateSession(
                currentSession,
                tokens.refreshToken()
        );

        log.info(
                "Refresh token rotated successfully for user: {} and session: {}",
                user.getId(),
                currentSession.getId()
        );

        return buildAuthenticationResponse(
                tokens.accessToken(),
                tokens.refreshToken()
        );
    }

    @Override
    @Transactional
    public void logout(
            final String refreshToken
    ) {

        log.debug("Logout attempt");

        refreshTokenSessionService
                .revokeSession(refreshToken);

        log.info(
                "User session logged out successfully"
        );
    }

    @Override
    @Transactional
    public void logoutAll(
            final String userId
    ) {

        log.info(
                "Logout all sessions requested for user: {}",
                userId
        );

        refreshTokenSessionService
                .revokeAllSessions(userId);
    }

    private AuthenticationResponse createAuthenticationSession(
            final User user,
            final ClientInfo clientInfo
    ) {

        final GeneratedTokens tokens =
                generateTokens(user);

        refreshTokenSessionService.createSession(
                tokens.refreshToken(),
                user,
                clientInfo
        );

        return buildAuthenticationResponse(
                tokens.accessToken(),
                tokens.refreshToken()
        );
    }

    private void validateRegistration(
            final RegistrationRequest request,
            final String normalizedEmail
    ) {

        if (userRepository.existsByEmailIgnoreCase(
                normalizedEmail
        )) {

            log.warn(
                    "Registration rejected: email already exists - {}",
                    normalizedEmail
            );

            throw new BusinessException(
                    EMAIL_ALREADY_EXISTS
            );
        }

        if (userRepository.existsByPhoneNumberIgnoreCase(
                request.getPhoneNumber()
        )) {

            log.warn(
                    "Registration rejected: phone number already exists"
            );

            throw new BusinessException(
                    PHONE_ALREADY_EXISTS
            );
        }

        if (!request.getPassword().equals(
                request.getConfirmPassword()
        )) {

            log.warn(
                    "Registration rejected: password confirmation mismatch"
            );

            throw new BusinessException(
                    PASSWORD_MISMATCH
            );
        }
    }

    private Role getDefaultUserRole() {

        return roleRepository
                .findByName(USER_ROLE)
                .orElseThrow(() -> {

                    log.error(
                            "Default user role '{}' was not found",
                            USER_ROLE
                    );

                    return new BusinessException(
                            ROLE_NOT_FOUND
                    );
                });
    }

    private AuthenticationResponse buildAuthenticationResponse(
            final String accessToken,
            final String refreshToken
    ) {

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TOKEN_TYPE)
                .build();
    }

    private GeneratedTokens generateTokens(
            final User user
    ) {

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

    private String normalizeEmail(
            final String email
    ) {

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private record GeneratedTokens(
            String accessToken,
            String refreshToken
    ) {
    }
}