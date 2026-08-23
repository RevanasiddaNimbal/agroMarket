package com.agri.market.auth.service;

import com.agri.market.exception.BusinessException;
import com.agri.market.exception.ErrorCode;
import com.agri.market.security.properties.PasswordSecurityProperties;
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
@DisplayName("PasswordExpirationServiceImpl")
class PasswordExpirationServiceImplTest {

    private static final long EXPIRATION_MINUTES = 90;

    @Mock
    private PasswordSecurityProperties passwordSecurityProperties;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PasswordExpirationServiceImpl passwordExpirationService;

    private User createUser() {

        return User.builder()
                .id("test-user-id")
                .email("revanasidda@mail.com")
                .password("encoded-password")
                .passwordChangedAt(
                        LocalDateTime.now()
                                .minusMinutes(30)
                )
                .credentialsExpired(false)
                .build();
    }

    @Nested
    @DisplayName("isPasswordExpired")
    class IsPasswordExpiredTests {

        @Test
        void shouldReturnFalseWhenUserIsNull() {

            boolean result =
                    passwordExpirationService.isPasswordExpired(null);

            assertThat(result)
                    .isFalse();

            verifyNoInteractions(
                    passwordSecurityProperties,
                    userRepository
            );
        }

        @Test
        void shouldReturnFalseWhenPasswordIsNull() {

            User user = createUser();

            user.setPassword(null);

            boolean result =
                    passwordExpirationService.isPasswordExpired(user);

            assertThat(result)
                    .isFalse();

            verifyNoInteractions(
                    passwordSecurityProperties,
                    userRepository
            );
        }

        @Test
        void shouldReturnFalseWhenPasswordIsBlank() {

            User user = createUser();

            user.setPassword("   ");

            boolean result =
                    passwordExpirationService.isPasswordExpired(user);

            assertThat(result)
                    .isFalse();

            verifyNoInteractions(
                    passwordSecurityProperties,
                    userRepository
            );
        }

        @Test
        void shouldReturnTrueWhenPasswordChangedAtIsNull() {

            User user = createUser();

            user.setPassword("encoded-password");
            user.setPasswordChangedAt(null);

            boolean result =
                    passwordExpirationService.isPasswordExpired(user);

            assertThat(result)
                    .isTrue();

            verifyNoInteractions(
                    passwordSecurityProperties,
                    userRepository
            );
        }

        @Test
        void shouldReturnFalseWhenPasswordHasNotExpired() {

            User user = createUser();

            user.setPassword("encoded-password");
            user.setPasswordChangedAt(
                    LocalDateTime.now().minusMinutes(30)
            );

            when(passwordSecurityProperties.getExpirationMinutes())
                    .thenReturn(EXPIRATION_MINUTES);

            boolean result =
                    passwordExpirationService.isPasswordExpired(user);

            assertThat(result)
                    .isFalse();

            verify(passwordSecurityProperties)
                    .getExpirationMinutes();

            verifyNoInteractions(userRepository);
        }

        @Test
        void shouldReturnTrueWhenPasswordHasExpired() {

            User user = createUser();

            user.setPassword("encoded-password");
            user.setPasswordChangedAt(
                    LocalDateTime.now().minusMinutes(120)
            );

            when(passwordSecurityProperties.getExpirationMinutes())
                    .thenReturn(EXPIRATION_MINUTES);

            boolean result =
                    passwordExpirationService.isPasswordExpired(user);

            assertThat(result)
                    .isTrue();

            verify(passwordSecurityProperties)
                    .getExpirationMinutes();

            verifyNoInteractions(userRepository);
        }

        @Test
        void shouldReturnTrueWhenPasswordExpiresExactlyAtCurrentTime() {

            User user = createUser();

            user.setPassword("encoded-password");

            LocalDateTime passwordChangedAt =
                    LocalDateTime.now()
                            .minusMinutes(EXPIRATION_MINUTES);

            user.setPasswordChangedAt(passwordChangedAt);

            when(passwordSecurityProperties.getExpirationMinutes())
                    .thenReturn(EXPIRATION_MINUTES);

            boolean result =
                    passwordExpirationService.isPasswordExpired(user);

            assertThat(result)
                    .isTrue();

            verify(passwordSecurityProperties)
                    .getExpirationMinutes();

            verifyNoInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("validatePasswordPolicy")
    class ValidatePasswordPolicyTests {

        @Test
        void shouldDoNothingWhenUserIsNull() {

            passwordExpirationService
                    .validatePasswordPolicy(null);

            verifyNoInteractions(
                    passwordSecurityProperties,
                    userRepository
            );
        }

        @Test
        void shouldDoNothingWhenPasswordIsNull() {

            User user = createUser();

            user.setPassword(null);

            passwordExpirationService
                    .validatePasswordPolicy(user);

            verifyNoInteractions(
                    passwordSecurityProperties,
                    userRepository
            );
        }

        @Test
        void shouldDoNothingWhenPasswordIsBlank() {

            User user = createUser();

            user.setPassword("   ");

            passwordExpirationService
                    .validatePasswordPolicy(user);

            verifyNoInteractions(
                    passwordSecurityProperties,
                    userRepository
            );
        }

        @Test
        void shouldMarkCredentialsExpiredAndThrowWhenPasswordIsExpired() {

            User user = createUser();

            user.setPassword("encoded-password");
            user.setPasswordChangedAt(
                    LocalDateTime.now().minusMinutes(120)
            );
            user.setCredentialsExpired(false);

            when(passwordSecurityProperties.getExpirationMinutes())
                    .thenReturn(EXPIRATION_MINUTES);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            assertThatThrownBy(
                    () -> passwordExpirationService
                            .validatePasswordPolicy(user)
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {

                        BusinessException ex =
                                (BusinessException) exception;

                        assertThat(ex.getErrorCode())
                                .isEqualTo(
                                        ErrorCode.PASSWORD_EXPIRED
                                );
                    });

            assertThat(user.isCredentialsExpired())
                    .isTrue();

            verify(userRepository)
                    .save(user);

            verify(passwordSecurityProperties)
                    .getExpirationMinutes();
        }

        @Test
        void shouldNotSaveAgainWhenPasswordIsExpiredAndCredentialsAlreadyMarkedExpired() {

            User user = createUser();

            user.setPassword("encoded-password");
            user.setPasswordChangedAt(
                    LocalDateTime.now().minusMinutes(120)
            );
            user.setCredentialsExpired(true);

            when(passwordSecurityProperties.getExpirationMinutes())
                    .thenReturn(EXPIRATION_MINUTES);

            assertThatThrownBy(
                    () -> passwordExpirationService
                            .validatePasswordPolicy(user)
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {

                        BusinessException ex =
                                (BusinessException) exception;

                        assertThat(ex.getErrorCode())
                                .isEqualTo(
                                        ErrorCode.PASSWORD_EXPIRED
                                );
                    });

            assertThat(user.isCredentialsExpired())
                    .isTrue();

            verify(userRepository, never())
                    .save(any(User.class));

            verify(passwordSecurityProperties)
                    .getExpirationMinutes();
        }

        @Test
        void shouldClearCredentialsExpiredWhenPasswordIsValid() {

            User user = createUser();

            user.setPassword("encoded-password");
            user.setPasswordChangedAt(
                    LocalDateTime.now().minusMinutes(30)
            );
            user.setCredentialsExpired(true);

            when(passwordSecurityProperties.getExpirationMinutes())
                    .thenReturn(EXPIRATION_MINUTES);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            passwordExpirationService
                    .validatePasswordPolicy(user);

            assertThat(user.isCredentialsExpired())
                    .isFalse();

            verify(userRepository)
                    .save(user);

            verify(passwordSecurityProperties)
                    .getExpirationMinutes();
        }

        @Test
        void shouldNotSaveWhenPasswordIsValidAndCredentialsAreNotExpired() {

            User user = createUser();

            user.setPassword("encoded-password");
            user.setPasswordChangedAt(
                    LocalDateTime.now().minusMinutes(30)
            );
            user.setCredentialsExpired(false);

            when(passwordSecurityProperties.getExpirationMinutes())
                    .thenReturn(EXPIRATION_MINUTES);

            passwordExpirationService
                    .validatePasswordPolicy(user);

            assertThat(user.isCredentialsExpired())
                    .isFalse();

            verify(userRepository, never())
                    .save(any(User.class));

            verify(passwordSecurityProperties)
                    .getExpirationMinutes();
        }

        @Test
        void shouldMarkCredentialsExpiredWhenPasswordChangedAtIsNull() {

            User user = createUser();

            user.setPassword("encoded-password");
            user.setPasswordChangedAt(null);
            user.setCredentialsExpired(false);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            assertThatThrownBy(
                    () -> passwordExpirationService
                            .validatePasswordPolicy(user)
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception -> {

                        BusinessException ex =
                                (BusinessException) exception;

                        assertThat(ex.getErrorCode())
                                .isEqualTo(
                                        ErrorCode.PASSWORD_EXPIRED
                                );
                    });

            assertThat(user.isCredentialsExpired())
                    .isTrue();

            verify(userRepository)
                    .save(user);

            verifyNoInteractions(passwordSecurityProperties);
        }

        @Test
        void shouldNotChangeCredentialsStateWhenPasswordIsNull() {

            User user = createUser();

            user.setPassword(null);
            user.setCredentialsExpired(true);

            passwordExpirationService
                    .validatePasswordPolicy(user);

            assertThat(user.isCredentialsExpired())
                    .isTrue();

            verifyNoInteractions(
                    passwordSecurityProperties,
                    userRepository
            );
        }

        @Test
        void shouldNotChangeCredentialsStateWhenPasswordIsBlank() {

            User user = createUser();

            user.setPassword("   ");
            user.setCredentialsExpired(true);

            passwordExpirationService
                    .validatePasswordPolicy(user);

            assertThat(user.isCredentialsExpired())
                    .isTrue();

            verifyNoInteractions(
                    passwordSecurityProperties,
                    userRepository
            );
        }
    }
}