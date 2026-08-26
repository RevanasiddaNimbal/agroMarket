package com.agri.market.auth.service;

import com.agri.market.auth.dto.*;
import com.agri.market.common.exception.BusinessException;
import com.agri.market.email.service.EmailVerificationService;
import com.agri.market.password.service.PasswordExpirationService;
import com.agri.market.role.entity.Role;
import com.agri.market.role.repository.RoleRepository;
import com.agri.market.security.client.ClientInfo;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static com.agri.market.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String USER_ROLE = "USER";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenSessionService refreshTokenSessionServiceImpl;
    private final EmailVerificationService emailVerificationService;
    private final AuthenticationTokenService authenticationTokenService;
    private final PasswordExpirationService passwordExpirationService;
    private final LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public RegistrationResponse register(
            final RegistrationRequest request,
            final ClientInfo clientInfo
    ) {
        final String email = normalizeEmail(request.getEmail());

        log.debug("Registration attempt received for email: {}", email);

        validateRegistration(request, email);

        final Role userRole = getDefaultUserRole();

        final User user = User.builder()
                .fullName(request.getFullName())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .passwordChangedAt(LocalDateTime.now())
                .failedLoginAttempts(0)
                .accountLocked(false)
                .temporaryLockedUntil(null)
                .credentialsExpired(false)
                .roles(List.of(userRole))
                .build();

        user.setEmailVerified(false);
        user.setEnabled(false);

        final User savedUser = userRepository.save(user);

        emailVerificationService.sendVerificationEmail(savedUser);

        log.info(
                "User registered successfully. Verification email sent. User: {}",
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
    public AuthenticationResult login(
            final AuthenticationRequest request,
            final ClientInfo clientInfo
    ) {

        final String email = normalizeEmail(request.getEmail());

        log.debug(
                "Authentication request received for email: {}",
                email
        );

        final User user = findUserForLogin(email);

        loginAttemptService.validateAccountAvailability(user);

        loginAttemptService.validateLockStatus(user);

        if (user.getPassword() == null || user.getPassword().isBlank()) {

            log.warn(
                    "Password authentication rejected because no password is configured. User: {}",
                    user.getId()
            );

            throw new BusinessException(PASSWORD_LOGIN_NOT_AVAILABLE);
        }

        try {

            final Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    request.getPassword()
                            )
                    );

            final User authenticatedUser =
                    (User) authentication.getPrincipal();

            loginAttemptService.resetAfterSuccessfulLogin(
                    authenticatedUser
            );


            passwordExpirationService.validatePasswordPolicy(
                    authenticatedUser
            );

            log.info(
                    "User authenticated successfully. User: {}",
                    authenticatedUser.getId()
            );

            return authenticationTokenService.createAuthenticationSession(
                    authenticatedUser,
                    clientInfo
            );

        } catch (BadCredentialsException ex) {

            log.warn(
                    "Authentication failed due to invalid credentials. User: {}",
                    user.getId()
            );


            loginAttemptService.recordFailedLogin(user);

            throw new BusinessException(BAD_CREDENTIALS);

        } catch (LockedException ex) {

            log.warn(
                    "Authentication rejected because account is locked. User: {}",
                    user.getId()
            );

            throw new BusinessException(
                    PERMANENT_ACCOUNT_LOCKED
            );
        }
    }

    @Override
    @Transactional
    public AuthenticationResult refreshToken(
            final RefreshTokenRequest request
    ) {
        log.debug("Refresh token request received");

        final AuthenticationResult response =
                authenticationTokenService.refreshAuthenticationSession(
                        request
                );

        log.info("Authentication session refreshed successfully");

        return response;
    }

    @Override
    @Transactional
    public void logout(final String refreshToken) {

        log.debug("Logout request received");

        refreshTokenSessionServiceImpl.revokeSession(refreshToken);

        log.info("User session logged out successfully");
    }

    @Override
    @Transactional
    public void logoutAll(final String userId) {

        log.info(
                "Logout all sessions requested for user: {}",
                userId
        );

        refreshTokenSessionServiceImpl.revokeAllSessions(userId);

        log.info(
                "All sessions revoked successfully for user: {}",
                userId
        );
    }

    private User findUserForLogin(final String email) {

        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> {

                    log.warn(
                            "Authentication rejected because user was not found. Email: {}",
                            email
                    );

                    return new BusinessException(USER_NOT_REGISTERED);
                });
    }

    private void validateRegistration(
            final RegistrationRequest request,
            final String normalizedEmail
    ) {
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {

            log.warn(
                    "Registration rejected because email already exists: {}",
                    normalizedEmail
            );

            throw new BusinessException(EMAIL_ALREADY_EXISTS);
        }


        if (!request.getPassword().equals(
                request.getConfirmPassword()
        )) {

            log.warn(
                    "Registration rejected because password confirmation does not match"
            );

            throw new BusinessException(PASSWORD_MISMATCH);
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

                    return new BusinessException(ROLE_NOT_FOUND);
                });
    }

    private String normalizeEmail(final String email) {

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}