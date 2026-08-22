package com.agri.market.auth.service;

import com.agri.market.exception.BusinessException;
import com.agri.market.exception.ErrorCode;
import com.agri.market.security.config.PasswordSecurityProperties;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordExpirationServiceImpl implements PasswordExpirationService {

    private final PasswordSecurityProperties passwordSecurityProperties;
    private final UserRepository userRepository;

    @Override
    public boolean isPasswordExpired(final User user) {
        if (user == null) {
            return false;
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return false;
        }

        if (user.getPasswordChangedAt() == null) {
            log.warn(
                    "Password expiration check: passwordChangedAt is null for user: {}",
                    user.getId()
            );
            return true;
        }

        final LocalDateTime now = LocalDateTime.now();

        final LocalDateTime expirationDate =
                user.getPasswordChangedAt()
                        .plusMinutes(
                                passwordSecurityProperties.getExpirationMinutes()
                        );

        return !now.isBefore(expirationDate);
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = BusinessException.class
    )
    public void validatePasswordPolicy(final User user) {

        if (!hasPassword(user)) {
            return;
        }

        if (isPasswordExpired(user)) {

            if (!user.isCredentialsExpired()) {
                user.setCredentialsExpired(true);
                userRepository.save(user);

                log.warn(
                        "Password expired and credentials marked as expired for user: {}",
                        user.getId()
                );
            }

            throw new BusinessException(ErrorCode.PASSWORD_EXPIRED);
        }

        if (user.isCredentialsExpired()) {
            user.setCredentialsExpired(false);
            userRepository.save(user);

            log.info(
                    "Password expiration state cleared for user: {}",
                    user.getId()
            );
        }
    }

    private boolean hasPassword(final User user) {
        return user != null
                && user.getPassword() != null
                && !user.getPassword().isBlank();
    }
}