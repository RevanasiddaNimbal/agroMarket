package com.agri.market.auth.service;

import com.agri.market.exception.BusinessException;
import com.agri.market.security.config.LoginAttemptProperties;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.agri.market.exception.ErrorCode.ACCOUNT_LOCKED;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final UserRepository userRepository;
    private final LoginAttemptProperties loginAttemptProperties;

    @Override
    @Transactional
    public void validateLockStatus(final User user) {

        if (user == null) {
            return;
        }

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime lockedUntil = user.getTemporaryLockedUntil();

        if (lockedUntil == null) {
            return;
        }

        if (lockedUntil.isAfter(now)) {
            log.warn(
                    "Login blocked due to active temporary lock. User: {}, Locked until: {}",
                    user.getEmail(),
                    lockedUntil
            );

            throw new BusinessException(ACCOUNT_LOCKED);
        }

        user.setFailedLoginAttempts(0);
        user.setTemporaryLockedUntil(null);

        userRepository.save(user);

        log.info(
                "Temporary login lock expired. Login attempts reset for user: {}",
                user.getEmail()
        );
    }

    @Override
    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = BusinessException.class
    )
    public void recordFailedLogin(final User user) {

        if (user == null) {
            return;
        }

        final LocalDateTime now = LocalDateTime.now();
        final int nextFailedAttempts =
                user.getFailedLoginAttempts() + 1;

        user.setFailedLoginAttempts(nextFailedAttempts);

        if (nextFailedAttempts >= loginAttemptProperties.getMaxAttempts()) {

            final LocalDateTime lockedUntil =
                    now.plusMinutes(
                            loginAttemptProperties.getLockDurationMinutes()
                    );

            user.setFailedLoginAttempts(
                    loginAttemptProperties.getMaxAttempts()
            );

            user.setTemporaryLockedUntil(lockedUntil);

            userRepository.save(user);

            log.warn(
                    "Temporary account lock triggered. User: {}, Attempts: {}/{}, Locked until: {}",
                    user.getEmail(),
                    user.getFailedLoginAttempts(),
                    loginAttemptProperties.getMaxAttempts(),
                    lockedUntil
            );

            throw new BusinessException(ACCOUNT_LOCKED);
        }

        userRepository.save(user);

        log.warn(
                "Invalid login credentials. Failed attempts: {}/{} for user: {}",
                nextFailedAttempts,
                loginAttemptProperties.getMaxAttempts(),
                user.getEmail()
        );
    }

    @Override
    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void resetAfterSuccessfulLogin(final User user) {

        if (user == null) {
            return;
        }

        user.setFailedLoginAttempts(0);
        user.setTemporaryLockedUntil(null);

        userRepository.save(user);

        log.info(
                "Login attempt state reset after successful login for user: {}",
                user.getEmail()
        );
    }
}