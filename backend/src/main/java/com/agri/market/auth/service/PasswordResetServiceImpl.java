package com.agri.market.auth.service;

import com.agri.market.auth.dto.ForgotPasswordRequest;
import com.agri.market.auth.dto.ResetPasswordRequest;
import com.agri.market.auth.entity.PasswordResetToken;
import com.agri.market.auth.repository.PasswordResetTokenRepository;
import com.agri.market.email.config.EmailProperties;
import com.agri.market.email.service.EmailService;
import com.agri.market.exception.BusinessException;
import com.agri.market.security.jwt.TokenHasher;
import com.agri.market.security.token.TokenGenerator;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.agri.market.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl
        implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenSessionService refreshTokenSessionService;
    private final PasswordEncoder passwordEncoder;
    private final TokenHasher tokenHasher;
    private final TokenGenerator tokenGenerator;
    private final EmailService emailService;
    private final EmailProperties emailProperties;

    @Override
    @Transactional
    public void forgotPassword(
            final ForgotPasswordRequest request
    ) {

        final String email =
                request.getEmail().trim();

        final User user =
                userRepository
                        .findByEmailIgnoreCase(email)
                        .orElse(null);

        if (user == null) {

            log.info(
                    "Password reset request processed for unknown account"
            );

            return;
        }

        passwordResetTokenRepository
                .deleteAllByUser(user);

        final String rawToken =
                tokenGenerator.generate();

        final String tokenHash =
                tokenHasher.hash(rawToken);

        final LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusMinutes(
                                emailProperties
                                        .getPasswordResetTokenExpirationMinutes()
                        );

        final PasswordResetToken resetToken =
                PasswordResetToken.builder()
                        .user(user)
                        .tokenHash(tokenHash)
                        .expiresAt(expiresAt)
                        .used(false)
                        .build();

        passwordResetTokenRepository.save(resetToken);

        final String resetLink =
                emailProperties.getFrontendUrl()
                        + "/auth/reset-password?token="
                        + rawToken;

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetLink
        );

        log.info(
                "Password reset email request processed for user: {}",
                user.getId()
        );
    }

    @Override
    @Transactional
    public void resetPassword(
            final ResetPasswordRequest request
    ) {

        validatePasswords(request);

        final String tokenHash =
                tokenHasher.hash(
                        request.getToken()
                );

        final PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Invalid password reset token supplied"
                            );

                            return new BusinessException(
                                    INVALID_PASSWORD_RESET_TOKEN
                            );
                        });

        validateResetToken(resetToken);

        final User user =
                resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        resetToken.markAsUsed();

        userRepository.save(user);
        passwordResetTokenRepository.save(resetToken);

        refreshTokenSessionService
                .revokeAllSessions(user.getId());

        log.info(
                "Password reset completed successfully for user: {}",
                user.getId()
        );
    }

    private void validateResetToken(
            final PasswordResetToken resetToken
    ) {

        if (resetToken.isUsed()) {

            log.warn(
                    "Attempt to reuse password reset token: {}",
                    resetToken.getId()
            );

            throw new BusinessException(
                    PASSWORD_RESET_TOKEN_ALREADY_USED
            );
        }

        if (resetToken.isExpired()) {

            log.warn(
                    "Expired password reset token used: {}",
                    resetToken.getId()
            );

            throw new BusinessException(
                    PASSWORD_RESET_TOKEN_EXPIRED
            );
        }
    }

    private void validatePasswords(
            final ResetPasswordRequest request
    ) {

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            log.warn(
                    "Password reset rejected because passwords do not match"
            );

            throw new BusinessException(
                    PASSWORD_MISMATCH
            );
        }
    }
}