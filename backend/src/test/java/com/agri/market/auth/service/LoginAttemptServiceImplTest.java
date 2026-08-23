package com.agri.market.auth.service;

import com.agri.market.exception.BusinessException;
import com.agri.market.exception.ErrorCode;
import com.agri.market.security.properties.LoginAttemptProperties;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginAttemptServiceImpl")
class LoginAttemptServiceImplTest {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoginAttemptProperties loginAttemptProperties;

    @InjectMocks
    private LoginAttemptServiceImpl loginAttemptService;

    private User createUser() {

        return User.builder()
                .id("test-user-id")
                .email("revanasidda@mail.com")
                .password("encoded-password")
                .failedLoginAttempts(0)
                .temporaryLockedUntil(null)
                .build();
    }

    @Nested
    @DisplayName("validateLockStatus")
    class ValidateLockStatusTests {

        @Test
        void shouldDoNothingWhenUserIsNull() {

            loginAttemptService.validateLockStatus(null);

            verifyNoInteractions(
                    userRepository,
                    loginAttemptProperties
            );
        }

        @Test
        void shouldDoNothingWhenUserIsNotLocked() {

            User user = createUser();

            user.setTemporaryLockedUntil(null);

            loginAttemptService.validateLockStatus(user);

            assertThat(user.getFailedLoginAttempts())
                    .isZero();

            assertThat(user.getTemporaryLockedUntil())
                    .isNull();

            verifyNoInteractions(
                    userRepository,
                    loginAttemptProperties
            );
        }

        @Test
        void shouldThrowAccountLockedWhenTemporaryLockIsActive() {

            User user = createUser();

            user.setFailedLoginAttempts(MAX_ATTEMPTS);
            user.setTemporaryLockedUntil(
                    LocalDateTime.now()
                            .plusMinutes(10)
            );

            assertThatThrownBy(
                    () -> loginAttemptService
                            .validateLockStatus(user)
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {

                        BusinessException ex =
                                (BusinessException) exception;

                        assertThat(ex.getErrorCode())
                                .isEqualTo(
                                        ErrorCode.ACCOUNT_LOCKED
                                );
                    });

            assertThat(user.getFailedLoginAttempts())
                    .isEqualTo(MAX_ATTEMPTS);

            assertThat(user.getTemporaryLockedUntil())
                    .isNotNull();

            verifyNoInteractions(
                    userRepository,
                    loginAttemptProperties
            );
        }

        @Test
        void shouldResetLoginAttemptsWhenTemporaryLockHasExpired() {

            User user = createUser();

            user.setFailedLoginAttempts(MAX_ATTEMPTS);
            user.setTemporaryLockedUntil(
                    LocalDateTime.now()
                            .minusMinutes(1)
            );

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            loginAttemptService.validateLockStatus(user);

            assertThat(user.getFailedLoginAttempts())
                    .isZero();

            assertThat(user.getTemporaryLockedUntil())
                    .isNull();

            verify(userRepository)
                    .save(user);

            verifyNoInteractions(
                    loginAttemptProperties
            );
        }
    }

    @Nested
    @DisplayName("recordFailedLogin")
    class RecordFailedLoginTests {

        @Test
        void shouldDoNothingWhenUserIsNull() {

            loginAttemptService.recordFailedLogin(null);

            verifyNoInteractions(
                    userRepository,
                    loginAttemptProperties
            );
        }

        @Test
        void shouldIncrementFailedLoginAttempts() {

            User user = createUser();

            user.setFailedLoginAttempts(2);

            when(loginAttemptProperties.getMaxAttempts())
                    .thenReturn(MAX_ATTEMPTS);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            loginAttemptService.recordFailedLogin(user);

            assertThat(user.getFailedLoginAttempts())
                    .isEqualTo(3);

            assertThat(user.getTemporaryLockedUntil())
                    .isNull();

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldLockAccountWhenMaximumFailedAttemptsAreReached() {

            User user = createUser();

            user.setFailedLoginAttempts(
                    MAX_ATTEMPTS - 1
            );

            when(loginAttemptProperties.getMaxAttempts())
                    .thenReturn(MAX_ATTEMPTS);

            when(loginAttemptProperties.getLockDurationMinutes())
                    .thenReturn(LOCK_DURATION_MINUTES);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            assertThatThrownBy(
                    () -> loginAttemptService
                            .recordFailedLogin(user)
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {

                        BusinessException ex =
                                (BusinessException) exception;

                        assertThat(ex.getErrorCode())
                                .isEqualTo(
                                        ErrorCode.ACCOUNT_LOCKED
                                );
                    });

            assertThat(user.getFailedLoginAttempts())
                    .isEqualTo(MAX_ATTEMPTS);

            assertThat(user.getTemporaryLockedUntil())
                    .isNotNull()
                    .isAfter(LocalDateTime.now());

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldKeepFailedLoginAttemptsCappedAtMaximum() {

            User user = createUser();

            user.setFailedLoginAttempts(MAX_ATTEMPTS);

            when(loginAttemptProperties.getMaxAttempts())
                    .thenReturn(MAX_ATTEMPTS);

            when(loginAttemptProperties.getLockDurationMinutes())
                    .thenReturn(LOCK_DURATION_MINUTES);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            assertThatThrownBy(
                    () -> loginAttemptService
                            .recordFailedLogin(user)
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {

                        BusinessException ex =
                                (BusinessException) exception;

                        assertThat(ex.getErrorCode())
                                .isEqualTo(
                                        ErrorCode.ACCOUNT_LOCKED
                                );
                    });

            assertThat(user.getFailedLoginAttempts())
                    .isEqualTo(MAX_ATTEMPTS);

            assertThat(user.getTemporaryLockedUntil())
                    .isNotNull();

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldSetTemporaryLockUsingConfiguredDuration() {

            User user = createUser();

            user.setFailedLoginAttempts(
                    MAX_ATTEMPTS - 1
            );

            when(loginAttemptProperties.getMaxAttempts())
                    .thenReturn(MAX_ATTEMPTS);

            when(loginAttemptProperties.getLockDurationMinutes())
                    .thenReturn(LOCK_DURATION_MINUTES);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            LocalDateTime before =
                    LocalDateTime.now();

            assertThatThrownBy(
                    () -> loginAttemptService
                            .recordFailedLogin(user)
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {

                        BusinessException ex =
                                (BusinessException) exception;

                        assertThat(ex.getErrorCode())
                                .isEqualTo(
                                        ErrorCode.ACCOUNT_LOCKED
                                );
                    });

            LocalDateTime after =
                    LocalDateTime.now();

            LocalDateTime lockedUntil =
                    user.getTemporaryLockedUntil();

            assertThat(lockedUntil)
                    .isNotNull()
                    .isBetween(
                            before.plusMinutes(
                                    LOCK_DURATION_MINUTES
                            ),
                            after.plusMinutes(
                                    LOCK_DURATION_MINUTES
                            )
                    );

            assertThat(user.getFailedLoginAttempts())
                    .isEqualTo(MAX_ATTEMPTS);

            verify(userRepository)
                    .save(user);
        }
    }

    @Nested
    @DisplayName("resetAfterSuccessfulLogin")
    class ResetAfterSuccessfulLoginTests {

        @Test
        void shouldDoNothingWhenUserIsNull() {

            loginAttemptService
                    .resetAfterSuccessfulLogin(null);

            verifyNoInteractions(
                    userRepository,
                    loginAttemptProperties
            );
        }

        @Test
        void shouldResetLoginAttemptsAndTemporaryLock() {

            User user = createUser();

            user.setFailedLoginAttempts(MAX_ATTEMPTS);

            user.setTemporaryLockedUntil(
                    LocalDateTime.now()
                            .plusMinutes(10)
            );

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            loginAttemptService
                    .resetAfterSuccessfulLogin(user);

            assertThat(user.getFailedLoginAttempts())
                    .isZero();

            assertThat(user.getTemporaryLockedUntil())
                    .isNull();

            verify(userRepository)
                    .save(user);

            verifyNoInteractions(
                    loginAttemptProperties
            );
        }
    }
}