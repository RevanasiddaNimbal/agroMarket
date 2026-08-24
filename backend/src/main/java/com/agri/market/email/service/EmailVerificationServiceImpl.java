package com.agri.market.email.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.email.config.EmailProperties;
import com.agri.market.email.dto.EmailVerificationRequest;
import com.agri.market.email.entity.EmailVerificationToken;
import com.agri.market.email.repository.EmailVerificationTokenRepository;
import com.agri.market.security.jwt.TokenHasher;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.agri.market.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationServiceImpl
        implements EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final EmailProperties emailProperties;
    private final TokenHasher tokenHasher;

    @Override
    @Transactional
    public void sendVerificationEmail(
            final User user
    ) {

        tokenRepository.deleteByUser(user);

        final String rawToken = generateToken();

        final String tokenHash =
                tokenHasher.hash(rawToken);

        final EmailVerificationToken verificationToken =
                EmailVerificationToken.builder()
                        .user(user)
                        .tokenHash(tokenHash)
                        .expiresAt(
                                LocalDateTime.now()
                                        .plusMinutes(15)
                        )
                        .used(false)
                        .build();

        tokenRepository.save(verificationToken);

        final String verificationLink =
                emailProperties.getFrontendUrl()
                        + "/verify-email?token="
                        + rawToken;

        emailService.sendVerificationEmail(
                user.getEmail(),
                verificationLink
        );

        log.info(
                "Verification email request processed for user: {}",
                rawToken
        );
    }

    @Override
    @Transactional
    public void verifyEmail(
            final String rawToken
    ) {

        final String tokenHash =
                tokenHasher.hash(rawToken);

        final EmailVerificationToken token =
                tokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Invalid email verification token supplied"
                            );

                            return new BusinessException(
                                    INVALID_VERIFICATION_TOKEN
                            );
                        });

        validateToken(token);

        final User user =
                token.getUser();

        user.setEmailVerified(true);
        user.setEnabled(true);

        token.setUsed(true);

        userRepository.save(user);
        tokenRepository.save(token);

        log.info(
                "Email verification completed successfully for user: {}",
                user.getId()
        );
    }

    @Override
    @Transactional
    public void resendVerificationEmail(
            final EmailVerificationRequest request
    ) {

        final User user =
                userRepository
                        .findByEmailIgnoreCase(request.getEmail().trim())
                        .orElse(null);

        if (user == null) {

            log.info(
                    "Verification email request processed for unknown account"
            );

            return;
        }

        if (user.isEmailVerified()) {

            log.info(
                    "Verification email request ignored for already verified user: {}",
                    user.getId()
            );

            return;
        }

        sendVerificationEmail(user);
    }

    private void validateToken(
            final EmailVerificationToken token
    ) {

        if (token.isUsed()) {

            log.warn(
                    "Attempt to reuse email verification token: {}",
                    token.getId()
            );

            throw new BusinessException(
                    VERIFICATION_TOKEN_ALREADY_USED
            );
        }

        if (token.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            log.warn(
                    "Expired email verification token used: {}",
                    token.getId()
            );

            throw new BusinessException(
                    VERIFICATION_TOKEN_EXPIRED
            );
        }
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}