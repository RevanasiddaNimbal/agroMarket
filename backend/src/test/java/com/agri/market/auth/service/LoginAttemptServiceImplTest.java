package com.agri.market.auth.service;

import com.agri.market.auth.properties.LoginAttemptProperties;
import com.agri.market.common.exception.BusinessException;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private User user;

    @InjectMocks
    private LoginAttemptServiceImpl loginAttemptService;

    @Nested
    @DisplayName("validateAccountAvailability")
    class ValidateAccountAvailabilityTests {

        @Test
        @DisplayName("should do nothing when user is null")
        void shouldDoNothingWhenUserIsNull() {

            assertDoesNotThrow(
                    () -> loginAttemptService.validateAccountAvailability(null)
            );

            verifyNoInteractions(user);
        }

        @Test
        @DisplayName("should allow enabled unlocked user")
        void shouldAllowEnabledUnlockedUser() {

            when(user.isEnabled()).thenReturn(true);
            when(user.isAccountLocked()).thenReturn(false);

            assertDoesNotThrow(
                    () -> loginAttemptService.validateAccountAvailability(user)
            );

            verify(user).isEnabled();
            verify(user).isAccountLocked();
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("should reject disabled user")
        void shouldRejectDisabledUser() {

            when(user.isEnabled()).thenReturn(false);

            assertThrows(
                    BusinessException.class,
                    () -> loginAttemptService.validateAccountAvailability(user)
            );

            verify(user).isEnabled();
            verify(user, never()).isAccountLocked();
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("should reject permanently locked user")
        void shouldRejectPermanentlyLockedUser() {

            when(user.isEnabled()).thenReturn(true);
            when(user.isAccountLocked()).thenReturn(true);

            assertThrows(
                    BusinessException.class,
                    () -> loginAttemptService.validateAccountAvailability(user)
            );

            verify(user).isEnabled();
            verify(user).isAccountLocked();
            verifyNoInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("validateLockStatus")
    class ValidateLockStatusTests {

        @Test
        @DisplayName("should do nothing when user is null")
        void shouldDoNothingWhenUserIsNull() {

            assertDoesNotThrow(
                    () -> loginAttemptService.validateLockStatus(null)
            );

            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("should allow user when temporary lock is null")
        void shouldAllowUserWhenTemporaryLockIsNull() {

            when(user.getTemporaryLockedUntil()).thenReturn(null);

            assertDoesNotThrow(
                    () -> loginAttemptService.validateLockStatus(user)
            );

            verify(user).getTemporaryLockedUntil();
            verify(user, never()).setFailedLoginAttempts(anyInt());
            verify(user, never()).setTemporaryLockedUntil(any());
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("should reject user when temporary lock is active")
        void shouldRejectUserWhenTemporaryLockIsActive() {

            LocalDateTime lockedUntil =
                    LocalDateTime.now().plusMinutes(10);

            when(user.getTemporaryLockedUntil())
                    .thenReturn(lockedUntil);

            assertThrows(
                    BusinessException.class,
                    () -> loginAttemptService.validateLockStatus(user)
            );

            verify(user).getTemporaryLockedUntil();
            verify(user, never()).setFailedLoginAttempts(anyInt());
            verify(user, never()).setTemporaryLockedUntil(any());
            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("should reset login attempts when temporary lock has expired")
        void shouldResetLoginAttemptsWhenTemporaryLockHasExpired() {

            LocalDateTime lockedUntil =
                    LocalDateTime.now().minusMinutes(1);

            when(user.getTemporaryLockedUntil())
                    .thenReturn(lockedUntil);

            assertDoesNotThrow(
                    () -> loginAttemptService.validateLockStatus(user)
            );

            verify(user).getTemporaryLockedUntil();
            verify(user).setFailedLoginAttempts(0);
            verify(user).setTemporaryLockedUntil(null);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should reset login attempts when lock expires exactly before current time")
        void shouldResetLoginAttemptsWhenLockExpiresBeforeCurrentTime() {

            LocalDateTime lockedUntil =
                    LocalDateTime.now().minusSeconds(1);

            when(user.getTemporaryLockedUntil())
                    .thenReturn(lockedUntil);

            loginAttemptService.validateLockStatus(user);

            verify(user).setFailedLoginAttempts(0);
            verify(user).setTemporaryLockedUntil(null);
            verify(userRepository).save(user);
        }
    }

    @Nested
    @DisplayName("recordFailedLogin")
    class RecordFailedLoginTests {

        @Test
        @DisplayName("should do nothing when user is null")
        void shouldDoNothingWhenUserIsNull() {

            assertDoesNotThrow(
                    () -> loginAttemptService.recordFailedLogin(null)
            );

            verifyNoInteractions(userRepository);
            verifyNoInteractions(loginAttemptProperties);
        }

        @Test
        @DisplayName("should increment failed login attempts")
        void shouldIncrementFailedLoginAttempts() {

            when(user.getFailedLoginAttempts()).thenReturn(0);
            when(loginAttemptProperties.getMaxAttempts())
                    .thenReturn(MAX_ATTEMPTS);

            assertDoesNotThrow(
                    () -> loginAttemptService.recordFailedLogin(user)
            );

            verify(user).getFailedLoginAttempts();
            verify(user).setFailedLoginAttempts(1);
            verify(userRepository).save(user);

            verify(loginAttemptProperties).getMaxAttempts();
            verify(loginAttemptProperties, never())
                    .getLockDurationMinutes();

            verify(user, never())
                    .setTemporaryLockedUntil(any());
        }

        @Test
        @DisplayName("should increment failed login attempts correctly")
        void shouldIncrementFailedLoginAttemptsCorrectly() {

            when(user.getFailedLoginAttempts()).thenReturn(2);
            when(loginAttemptProperties.getMaxAttempts())
                    .thenReturn(MAX_ATTEMPTS);

            loginAttemptService.recordFailedLogin(user);

            verify(user).getFailedLoginAttempts();
            verify(user).setFailedLoginAttempts(3);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should save failed login attempt before maximum is reached")
        void shouldSaveFailedLoginAttemptBeforeMaximumIsReached() {

            when(user.getFailedLoginAttempts())
                    .thenReturn(MAX_ATTEMPTS - 2);

            when(loginAttemptProperties.getMaxAttempts())
                    .thenReturn(MAX_ATTEMPTS);

            assertDoesNotThrow(
                    () -> loginAttemptService.recordFailedLogin(user)
            );

            verify(user).getFailedLoginAttempts();

            verify(user).setFailedLoginAttempts(MAX_ATTEMPTS - 1);

            verify(userRepository).save(user);

            verify(user, never())
                    .setTemporaryLockedUntil(any());

            verify(loginAttemptProperties, never())
                    .getLockDurationMinutes();
        }

        @Test
        @DisplayName("should lock account when maximum attempts are reached")
        void shouldLockAccountWhenMaximumAttemptsAreReached() {

            when(user.getFailedLoginAttempts())
                    .thenReturn(MAX_ATTEMPTS - 1);

            when(loginAttemptProperties.getMaxAttempts())
                    .thenReturn(MAX_ATTEMPTS);

            when(loginAttemptProperties.getLockDurationMinutes())
                    .thenReturn(LOCK_DURATION_MINUTES);

            assertThrows(
                    BusinessException.class,
                    () -> loginAttemptService.recordFailedLogin(user)
            );

            verify(user).getFailedLoginAttempts();
            verify(user).setFailedLoginAttempts(MAX_ATTEMPTS);
            verify(user).setTemporaryLockedUntil(any(LocalDateTime.class));
            verify(userRepository).save(user);

            verify(loginAttemptProperties).getMaxAttempts();
            verify(loginAttemptProperties).getLockDurationMinutes();
        }

        @Test
        @DisplayName("should lock account when failed attempts exceed maximum")
        void shouldLockAccountWhenFailedAttemptsExceedMaximum() {

            when(user.getFailedLoginAttempts())
                    .thenReturn(MAX_ATTEMPTS);

            when(loginAttemptProperties.getMaxAttempts())
                    .thenReturn(MAX_ATTEMPTS);

            when(loginAttemptProperties.getLockDurationMinutes())
                    .thenReturn(LOCK_DURATION_MINUTES);

            assertThrows(
                    BusinessException.class,
                    () -> loginAttemptService.recordFailedLogin(user)
            );

            verify(user).setFailedLoginAttempts(MAX_ATTEMPTS);
            verify(user).setTemporaryLockedUntil(any(LocalDateTime.class));
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should set temporary lock duration correctly")
        void shouldSetTemporaryLockDurationCorrectly() {

            when(user.getFailedLoginAttempts()).thenReturn(4);
            when(loginAttemptProperties.getMaxAttempts())
                    .thenReturn(MAX_ATTEMPTS);
            when(loginAttemptProperties.getLockDurationMinutes())
                    .thenReturn(LOCK_DURATION_MINUTES);

            LocalDateTime before = LocalDateTime.now()
                    .plusMinutes(LOCK_DURATION_MINUTES);

            assertThrows(
                    BusinessException.class,
                    () -> loginAttemptService.recordFailedLogin(user)
            );

            LocalDateTime after = LocalDateTime.now()
                    .plusMinutes(LOCK_DURATION_MINUTES);

            verify(user).setFailedLoginAttempts(MAX_ATTEMPTS);

            verify(user).setTemporaryLockedUntil(
                    argThat(lockTime ->
                            lockTime != null
                                    && !lockTime.isBefore(before)
                                    && !lockTime.isAfter(after)
                    )
            );

            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should save user before throwing lock exception")
        void shouldSaveUserBeforeThrowingLockException() {

            when(user.getFailedLoginAttempts()).thenReturn(4);
            when(loginAttemptProperties.getMaxAttempts())
                    .thenReturn(MAX_ATTEMPTS);
            when(loginAttemptProperties.getLockDurationMinutes())
                    .thenReturn(LOCK_DURATION_MINUTES);

            assertThrows(
                    BusinessException.class,
                    () -> loginAttemptService.recordFailedLogin(user)
            );

            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should not reset attempts when recording failed login")
        void shouldNotResetAttemptsWhenRecordingFailedLogin() {

            when(user.getFailedLoginAttempts()).thenReturn(1);
            when(loginAttemptProperties.getMaxAttempts())
                    .thenReturn(MAX_ATTEMPTS);

            loginAttemptService.recordFailedLogin(user);

            verify(user).setFailedLoginAttempts(2);
            verify(user, never()).setFailedLoginAttempts(0);
            verify(user, never()).setTemporaryLockedUntil(null);
        }
    }

    @Nested
    @DisplayName("resetAfterSuccessfulLogin")
    class ResetAfterSuccessfulLoginTests {

        @Test
        @DisplayName("should do nothing when user is null")
        void shouldDoNothingWhenUserIsNull() {

            assertDoesNotThrow(
                    () -> loginAttemptService.resetAfterSuccessfulLogin(null)
            );

            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("should reset failed login attempts after successful login")
        void shouldResetFailedLoginAttemptsAfterSuccessfulLogin() {

            loginAttemptService.resetAfterSuccessfulLogin(user);

            verify(user).setFailedLoginAttempts(0);
            verify(user).setTemporaryLockedUntil(null);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should clear temporary lock after successful login")
        void shouldClearTemporaryLockAfterSuccessfulLogin() {

            loginAttemptService.resetAfterSuccessfulLogin(user);

            verify(user).setTemporaryLockedUntil(null);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("should reset attempts before clearing temporary lock")
        void shouldResetAttemptsBeforeClearingTemporaryLock() {

            loginAttemptService.resetAfterSuccessfulLogin(user);

            var inOrder = inOrder(user, userRepository);

            inOrder.verify(user)
                    .setFailedLoginAttempts(0);

            inOrder.verify(user)
                    .setTemporaryLockedUntil(null);

            inOrder.verify(userRepository)
                    .save(user);
        }
    }
}