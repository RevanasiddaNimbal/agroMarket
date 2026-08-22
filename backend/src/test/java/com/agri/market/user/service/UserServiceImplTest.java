package com.agri.market.user.service;

import com.agri.market.exception.BusinessException;
import com.agri.market.support.ChangePasswordRequestTestFactory;
import com.agri.market.support.ProfileUpdateRequestTestFactory;
import com.agri.market.support.UserTestFactory;
import com.agri.market.user.dto.ChangePasswordRequestDto;
import com.agri.market.user.dto.ProfileUpdateRequestDto;
import com.agri.market.user.dto.SetPasswordRequestDto;
import com.agri.market.user.entity.User;
import com.agri.market.user.mapper.UserMapper;
import com.agri.market.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.agri.market.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
class UserServiceImplTest {

    private static final String USER_EMAIL = "revanasidda@mail.com";
    private static final String CURRENT_PASSWORD = "Old@Password123";
    private static final String NEW_PASSWORD = "New@Password123";
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String ENCODED_NEW_PASSWORD = "encoded-new-password";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsernameTests {

        @Test
        void shouldReturnUserDetailsWhenUserExists() {

            User user = UserTestFactory.activeUser();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            UserDetails result =
                    userService.loadUserByUsername(USER_EMAIL);

            assertThat(result)
                    .isSameAs(user);

            verify(userRepository)
                    .findByEmailIgnoreCase(USER_EMAIL);
        }

        @Test
        void shouldThrowWhenUserDoesNotExist() {

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> userService.loadUserByUsername(USER_EMAIL)
            )
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining(USER_EMAIL);

            verify(userRepository)
                    .findByEmailIgnoreCase(USER_EMAIL);
        }
    }

    @Nested
    @DisplayName("updateProfileInfo")
    class UpdateProfileInfoTests {

        @Test
        void shouldUpdateAndSaveProfile() {

            User user = UserTestFactory.activeUser();

            ProfileUpdateRequestDto request =
                    ProfileUpdateRequestTestFactory.validRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            doAnswer(invocation -> {

                User target = invocation.getArgument(1);

                target.setFullName("Updated Name");
                target.setProfilePictureUrl(
                        "https://example.com/profile.jpg"
                );

                return null;

            }).when(userMapper)
                    .updateUserFromProfileRequest(request, user);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            userService.updateProfileInfo(
                    request,
                    USER_EMAIL
            );

            ArgumentCaptor<User> captor =
                    ArgumentCaptor.forClass(User.class);

            verify(userMapper)
                    .updateUserFromProfileRequest(
                            request,
                            user
                    );

            verify(userRepository)
                    .save(captor.capture());

            assertThat(captor.getValue().getFullName())
                    .isEqualTo("Updated Name");

            assertThat(captor.getValue().getProfilePictureUrl())
                    .isEqualTo(
                            "https://example.com/profile.jpg"
                    );
        }

        @Test
        void shouldPropagateRepositoryFailureDuringProfileUpdate() {

            User user = UserTestFactory.activeUser();

            ProfileUpdateRequestDto request =
                    ProfileUpdateRequestTestFactory.validRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            doThrow(new RuntimeException("db down"))
                    .when(userRepository)
                    .save(user);

            assertThatThrownBy(
                    () -> userService.updateProfileInfo(
                            request,
                            USER_EMAIL
                    )
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("db down");

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldThrowWhenUserDoesNotExist() {

            ProfileUpdateRequestDto request =
                    ProfileUpdateRequestTestFactory.validRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> userService.updateProfileInfo(
                            request,
                            USER_EMAIL
                    )
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex ->
                            assertThat(
                                    ((BusinessException) ex)
                                            .getErrorCode()
                            ).isEqualTo(USER_NOT_FOUND)
                    );

            verifyNoInteractions(userMapper);

            verify(userRepository, never())
                    .save(any(User.class));
        }
    }

    @Nested
    @DisplayName("setPassword")
    class SetPasswordTests {

        @Test
        void shouldSetPasswordSuccessfullyWhenPasswordIsNotConfigured() {

            User user = UserTestFactory.activeUser();

            user.setPassword(null);
            user.setFailedLoginAttempts(3);
            user.setTemporaryLockedUntil(
                    LocalDateTime.now().plusMinutes(10)
            );
            user.setCredentialsExpired(true);

            SetPasswordRequestDto request =
                    validSetPasswordRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.encode(NEW_PASSWORD))
                    .thenReturn(ENCODED_NEW_PASSWORD);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            userService.setPassword(
                    request,
                    USER_EMAIL
            );

            assertThat(user.getPassword())
                    .isEqualTo(ENCODED_NEW_PASSWORD);

            assertThat(user.getPasswordChangedAt())
                    .isNotNull();

            assertThat(user.isCredentialsExpired())
                    .isFalse();

            assertThat(user.getFailedLoginAttempts())
                    .isZero();

            assertThat(user.getTemporaryLockedUntil())
                    .isNull();

            verify(passwordEncoder)
                    .encode(NEW_PASSWORD);

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldSetPasswordSuccessfullyWhenPasswordIsBlank() {

            User user = UserTestFactory.activeUser();

            user.setPassword("   ");

            SetPasswordRequestDto request =
                    validSetPasswordRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.encode(NEW_PASSWORD))
                    .thenReturn(ENCODED_NEW_PASSWORD);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            userService.setPassword(
                    request,
                    USER_EMAIL
            );

            assertThat(user.getPassword())
                    .isEqualTo(ENCODED_NEW_PASSWORD);

            assertThat(user.getPasswordChangedAt())
                    .isNotNull();

            verify(passwordEncoder)
                    .encode(NEW_PASSWORD);

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldRejectWhenPasswordsDoNotMatch() {

            SetPasswordRequestDto request =
                    validSetPasswordRequest();

            request.setConfirmNewPassword(
                    "Different@Password123"
            );

            assertThatThrownBy(
                    () -> userService.setPassword(
                            request,
                            USER_EMAIL
                    )
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex ->
                            assertThat(
                                    ((BusinessException) ex)
                                            .getErrorCode()
                            ).isEqualTo(PASSWORD_MISMATCH)
                    );

            verifyNoInteractions(
                    userRepository,
                    passwordEncoder
            );
        }

        @Test
        void shouldRejectWhenPasswordIsAlreadySet() {

            User user = UserTestFactory.activeUser();

            user.setPassword(ENCODED_PASSWORD);

            SetPasswordRequestDto request =
                    validSetPasswordRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            assertThatThrownBy(
                    () -> userService.setPassword(
                            request,
                            USER_EMAIL
                    )
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex ->
                            assertThat(
                                    ((BusinessException) ex)
                                            .getErrorCode()
                            ).isEqualTo(PASSWORD_ALREADY_SET)
                    );

            verify(passwordEncoder, never())
                    .encode(anyString());

            verify(userRepository, never())
                    .save(any(User.class));
        }

        @Test
        void shouldRejectWhenUserDoesNotExist() {

            SetPasswordRequestDto request =
                    validSetPasswordRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> userService.setPassword(
                            request,
                            USER_EMAIL
                    )
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex ->
                            assertThat(
                                    ((BusinessException) ex)
                                            .getErrorCode()
                            ).isEqualTo(USER_NOT_FOUND)
                    );

            verifyNoInteractions(passwordEncoder);

            verify(userRepository, never())
                    .save(any(User.class));
        }

        @Test
        void shouldResetFailedLoginAttemptsWhenPasswordIsSet() {

            User user = UserTestFactory.activeUser();

            user.setPassword(null);
            user.setFailedLoginAttempts(4);

            SetPasswordRequestDto request =
                    validSetPasswordRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.encode(NEW_PASSWORD))
                    .thenReturn(ENCODED_NEW_PASSWORD);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            userService.setPassword(
                    request,
                    USER_EMAIL
            );

            assertThat(user.getFailedLoginAttempts())
                    .isZero();

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldClearTemporaryLockWhenPasswordIsSet() {

            User user = UserTestFactory.activeUser();

            user.setPassword(null);

            LocalDateTime lockedUntil =
                    LocalDateTime.now().plusMinutes(10);

            user.setTemporaryLockedUntil(lockedUntil);

            SetPasswordRequestDto request =
                    validSetPasswordRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.encode(NEW_PASSWORD))
                    .thenReturn(ENCODED_NEW_PASSWORD);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            userService.setPassword(
                    request,
                    USER_EMAIL
            );

            assertThat(user.getTemporaryLockedUntil())
                    .isNull();

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldMarkCredentialsAsNotExpiredWhenPasswordIsSet() {

            User user = UserTestFactory.activeUser();

            user.setPassword(null);
            user.setCredentialsExpired(true);

            SetPasswordRequestDto request =
                    validSetPasswordRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.encode(NEW_PASSWORD))
                    .thenReturn(ENCODED_NEW_PASSWORD);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            userService.setPassword(
                    request,
                    USER_EMAIL
            );

            assertThat(user.isCredentialsExpired())
                    .isFalse();

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldSetPasswordChangedAtWhenPasswordIsSet() {

            User user = UserTestFactory.activeUser();

            user.setPassword(null);

            SetPasswordRequestDto request =
                    validSetPasswordRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.encode(NEW_PASSWORD))
                    .thenReturn(ENCODED_NEW_PASSWORD);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            userService.setPassword(
                    request,
                    USER_EMAIL
            );

            assertThat(user.getPasswordChangedAt())
                    .isNotNull();

            verify(userRepository)
                    .save(user);
        }

        private SetPasswordRequestDto validSetPasswordRequest() {

            SetPasswordRequestDto request =
                    new SetPasswordRequestDto();

            request.setNewPassword(NEW_PASSWORD);
            request.setConfirmNewPassword(NEW_PASSWORD);

            return request;
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePasswordTests {

        @Test
        void shouldRejectWhenNewPasswordMatchesCurrentPassword() {

            ChangePasswordRequestDto request =
                    ChangePasswordRequestTestFactory.validRequest();

            request.setNewPassword(CURRENT_PASSWORD);
            request.setConfirmNewPassword(CURRENT_PASSWORD);

            assertThatThrownBy(
                    () -> userService.changePassword(
                            request,
                            USER_EMAIL
                    )
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex ->
                            assertThat(
                                    ((BusinessException) ex)
                                            .getErrorCode()
                            ).isEqualTo(PASSWORD_MISMATCH)
                    );

            verifyNoInteractions(
                    userRepository,
                    passwordEncoder
            );
        }

        @Test
        void shouldRejectWhenCurrentPasswordIsInvalid() {

            User user = UserTestFactory.activeUser();

            ChangePasswordRequestDto request =
                    ChangePasswordRequestTestFactory.validRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.matches(
                    CURRENT_PASSWORD,
                    ENCODED_PASSWORD
            )).thenReturn(false);

            assertThatThrownBy(
                    () -> userService.changePassword(
                            request,
                            USER_EMAIL
                    )
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex ->
                            assertThat(
                                    ((BusinessException) ex)
                                            .getErrorCode()
                            ).isEqualTo(INVALID_CURRENT_PASSWORD)
                    );

            verify(userRepository, never())
                    .save(any(User.class));

            verify(passwordEncoder, never())
                    .encode(anyString());
        }

        @Test
        void shouldRejectWhenUserDoesNotExist() {

            ChangePasswordRequestDto request =
                    ChangePasswordRequestTestFactory.validRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> userService.changePassword(
                            request,
                            USER_EMAIL
                    )
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex ->
                            assertThat(
                                    ((BusinessException) ex)
                                            .getErrorCode()
                            ).isEqualTo(USER_NOT_FOUND)
                    );

            verifyNoInteractions(passwordEncoder);

            verify(userRepository, never())
                    .save(any(User.class));
        }

        @Test
        void shouldEncodeAndSaveNewPassword() {

            User user = UserTestFactory.activeUser();

            user.setPassword(ENCODED_PASSWORD);
            user.setFailedLoginAttempts(3);
            user.setTemporaryLockedUntil(
                    LocalDateTime.now().plusMinutes(10)
            );
            user.setCredentialsExpired(true);

            ChangePasswordRequestDto request =
                    ChangePasswordRequestTestFactory.validRequest();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.matches(
                    CURRENT_PASSWORD,
                    ENCODED_PASSWORD
            )).thenReturn(true);

            when(passwordEncoder.encode(NEW_PASSWORD))
                    .thenReturn(ENCODED_NEW_PASSWORD);

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            userService.changePassword(
                    request,
                    USER_EMAIL
            );

            assertThat(user.getPassword())
                    .isEqualTo(ENCODED_NEW_PASSWORD);

            assertThat(user.getPasswordChangedAt())
                    .isNotNull();

            assertThat(user.isCredentialsExpired())
                    .isFalse();

            assertThat(user.getFailedLoginAttempts())
                    .isZero();

            assertThat(user.getTemporaryLockedUntil())
                    .isNull();

            verify(passwordEncoder)
                    .matches(
                            CURRENT_PASSWORD,
                            ENCODED_PASSWORD
                    );

            verify(passwordEncoder)
                    .encode(NEW_PASSWORD);

            verify(userRepository)
                    .save(user);
        }
    }

    @Nested
    @DisplayName("deactivateAccount")
    class DeactivateAccountTests {

        @Test
        void shouldDeactivateActiveAccount() {

            User user = UserTestFactory.activeUser();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            userService.deactivateAccount(USER_EMAIL);

            assertThat(user.isEnabled())
                    .isFalse();

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldRejectAlreadyDeactivatedAccount() {

            User user = UserTestFactory.inactiveUser();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            assertThatThrownBy(
                    () -> userService.deactivateAccount(USER_EMAIL)
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex ->
                            assertThat(
                                    ((BusinessException) ex)
                                            .getErrorCode()
                            ).isEqualTo(USER_ALREADY_DEACTIVATED)
                    );

            verify(userRepository, never())
                    .save(any(User.class));
        }

        @Test
        void shouldThrowWhenUserDoesNotExist() {

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> userService.deactivateAccount(USER_EMAIL)
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex ->
                            assertThat(
                                    ((BusinessException) ex)
                                            .getErrorCode()
                            ).isEqualTo(USER_NOT_FOUND)
                    );

            verify(userRepository, never())
                    .save(any(User.class));
        }
    }

    @Nested
    @DisplayName("reactivateAccount")
    class ReactivateAccountTests {

        @Test
        void shouldReactivateInactiveAccount() {

            User user = UserTestFactory.inactiveUser();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation ->
                            invocation.getArgument(0));

            userService.reactivateAccount(USER_EMAIL);

            assertThat(user.isEnabled())
                    .isTrue();

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldRejectAlreadyActiveAccount() {

            User user = UserTestFactory.activeUser();

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.of(user));

            assertThatThrownBy(
                    () -> userService.reactivateAccount(USER_EMAIL)
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex ->
                            assertThat(
                                    ((BusinessException) ex)
                                            .getErrorCode()
                            ).isEqualTo(USER_ALREADY_ACTIVATED)
                    );

            verify(userRepository, never())
                    .save(any(User.class));
        }

        @Test
        void shouldThrowWhenUserDoesNotExist() {

            when(userRepository.findByEmailIgnoreCase(USER_EMAIL))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> userService.reactivateAccount(USER_EMAIL)
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex ->
                            assertThat(
                                    ((BusinessException) ex)
                                            .getErrorCode()
                            ).isEqualTo(USER_NOT_FOUND)
                    );

            verify(userRepository, never())
                    .save(any(User.class));
        }
    }

    @Nested
    @DisplayName("deleteAccount")
    class DeleteAccountTests {

        @Test
        void shouldCurrentlyDoNothing() {

            Assertions.assertThatCode(
                    () -> userService.deleteAccount(USER_EMAIL)
            ).doesNotThrowAnyException();

            verifyNoInteractions(
                    userRepository,
                    passwordEncoder,
                    userMapper
            );
        }
    }
}