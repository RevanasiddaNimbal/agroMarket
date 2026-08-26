package com.agri.market.auth.service;

import com.agri.market.auth.properties.LoginAttemptProperties;
import com.agri.market.common.exception.BusinessException;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.agri.market.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final UserRepository userRepository;
    private final LoginAttemptProperties loginAttemptProperties;

    @Override
    @Transactional(readOnly = true)
    public void validateAccountAvailability(final User user) {

        if (user == null) {
            return;
        }

        if (!user.isEnabled()) {

            log.warn(
                    "Account access rejected because user is disabled. User: {}",
                    user.getId()
            );

            throw new BusinessException(ERR_USER_DISABLED);
        }

        if (user.isAccountLocked()) {

            log.warn(
                    "Account access rejected because user is permanently locked. User: {}",
                    user.getId()
            );

            throw new BusinessException(PERMANENT_ACCOUNT_LOCKED);
        }
    }


    @Override
    @Transactional
    public void validateLockStatus(final User user) {

        if (user == null) {
            return;
        }

        final LocalDateTime now = LocalDateTime.now();

        final LocalDateTime lockedUntil =
                user.getTemporaryLockedUntil();

        if (lockedUntil == null) {
            return;
        }

        if (lockedUntil.isAfter(now)) {

            log.warn(
                    "Login blocked due to active temporary lock. User: {}, Locked until: {}",
                    user.getId(),
                    lockedUntil
            );

            throw new BusinessException(ACCOUNT_LOCKED);
        }


        user.setFailedLoginAttempts(0);
        user.setTemporaryLockedUntil(null);

        userRepository.save(user);

        log.info(
                "Temporary login lock expired. Login attempts reset for user: {}",
                user.getId()
        );
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailedLogin(final User user) {

        if (user == null) {
            return;
        }

        final LocalDateTime now = LocalDateTime.now();

        final int currentFailedAttempts =
                user.getFailedLoginAttempts();

        final int maxAttempts =
                loginAttemptProperties.getMaxAttempts();

        final int nextFailedAttempts =
                Math.min(currentFailedAttempts + 1, maxAttempts);

        user.setFailedLoginAttempts(nextFailedAttempts);

        if (nextFailedAttempts >= maxAttempts) {

            final LocalDateTime lockedUntil =
                    now.plusMinutes(
                            loginAttemptProperties
                                    .getLockDurationMinutes()
                    );

            user.setTemporaryLockedUntil(lockedUntil);

            userRepository.save(user);

            log.warn(
                    "Temporary account lock triggered. User: {}, Attempts: {}/{}, Locked until: {}",
                    user.getId(),
                    nextFailedAttempts,
                    maxAttempts,
                    lockedUntil
            );

            throw new BusinessException(ACCOUNT_LOCKED);
        }

        userRepository.save(user);

        log.warn(
                "Invalid login credentials. Failed attempts: {}/{} for user: {}",
                nextFailedAttempts,
                maxAttempts,
                user.getId()
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetAfterSuccessfulLogin(final User user) {

        if (user == null) {
            return;
        }

        user.setFailedLoginAttempts(0);
        user.setTemporaryLockedUntil(null);

        userRepository.save(user);

        log.info(
                "Login attempt state reset after successful login for user: {}",
                user.getId()
        );
    }
}