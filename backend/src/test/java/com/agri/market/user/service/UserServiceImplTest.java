package com.agri.market.user.service;

import com.agri.market.exception.BusinessException;
import com.agri.market.support.ChangePasswordRequestTestFactory;
import com.agri.market.support.ProfileUpdateRequestTestFactory;
import com.agri.market.support.UserTestFactory;
import com.agri.market.user.dto.ChangePasswordRequestDto;
import com.agri.market.user.dto.ProfileUpdateRequestDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.agri.market.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
class UserServiceImplTest {

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
            when(userRepository.findByEmailIgnoreCase("revanasidda@mail.com"))
                    .thenReturn(Optional.of(user));

            assertThat(userService.loadUserByUsername("revanasidda@mail.com"))
                    .isSameAs(user);
        }

        @Test
        void shouldThrowWhenUserDoesNotExist() {
            when(userRepository.findByEmailIgnoreCase("missing@mail.com"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.loadUserByUsername("missing@mail.com"))
                    .isInstanceOf(UsernameNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateProfileInfo")
    class UpdateProfileInfoTests {

        @Test
        void shouldUpdateAndSaveProfile() {
            User user = UserTestFactory.activeUser();
            ProfileUpdateRequestDto request = ProfileUpdateRequestTestFactory.validRequest();
            when(userRepository.findByEmailIgnoreCase("revanasidda@mail.com"))
                    .thenReturn(Optional.of(user));
            doAnswer(invocation -> {
                User target = invocation.getArgument(1);
                target.setFullName("Updated Name");
                target.setProfilePictureUrl("https://example.com/profile.jpg");
                return null;
            }).when(userMapper).updateUserFromProfileRequest(request, user);
            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            userService.updateProfileInfo(request, "revanasidda@mail.com");

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userMapper).updateUserFromProfileRequest(request, user);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().getFullName()).isEqualTo("Updated Name");
            assertThat(captor.getValue().getProfilePictureUrl()).isEqualTo("https://example.com/profile.jpg");
        }

        @Test
        void shouldPropagateRepositoryFailureDuringProfileUpdate() {
            User user = UserTestFactory.activeUser();
            ProfileUpdateRequestDto request = ProfileUpdateRequestTestFactory.validRequest();
            when(userRepository.findByEmailIgnoreCase("revanasidda@mail.com"))
                    .thenReturn(Optional.of(user));
            doThrow(new RuntimeException("db down")).when(userRepository).save(user);

            assertThatThrownBy(() -> userService.updateProfileInfo(request, "revanasidda@mail.com"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("db down");
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePasswordTests {

        @Test
        void shouldRejectWhenNewPasswordMatchesCurrentPassword() {
            ChangePasswordRequestDto request = ChangePasswordRequestTestFactory.validRequest();
            request.setNewPassword("Old@Password123");
            request.setConfirmNewPassword("Old@Password123");

            assertThatThrownBy(() -> userService.changePassword(request, "revanasidda@mail.com"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(PASSWORD_MISMATCH));

            verifyNoInteractions(userRepository, passwordEncoder);
        }

        @Test
        void shouldRejectWhenCurrentPasswordIsInvalid() {
            User user = UserTestFactory.activeUser();
            ChangePasswordRequestDto request = ChangePasswordRequestTestFactory.validRequest();
            when(userRepository.findByEmailIgnoreCase("revanasidda@mail.com"))
                    .thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Old@Password123", "encoded-password"))
                    .thenReturn(false);

            assertThatThrownBy(() -> userService.changePassword(request, "revanasidda@mail.com"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(INVALID_CURRENT_PASSWORD));

            verify(userRepository, never()).save(any());
        }

        @Test
        void shouldEncodeAndSaveNewPassword() {
            User user = UserTestFactory.activeUser();
            ChangePasswordRequestDto request = ChangePasswordRequestTestFactory.validRequest();
            when(userRepository.findByEmailIgnoreCase("revanasidda@mail.com"))
                    .thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Old@Password123", "encoded-password"))
                    .thenReturn(true);
            when(passwordEncoder.encode("New@Password123")).thenReturn("encoded-new-password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.changePassword(request, "revanasidda@mail.com");

            assertThat(user.getPassword()).isEqualTo("encoded-new-password");
            verify(userRepository).save(user);
        }
    }

    @Nested
    @DisplayName("deactivateAccount")
    class DeactivateAccountTests {

        @Test
        void shouldDeactivateActiveAccount() {
            User user = UserTestFactory.activeUser();
            when(userRepository.findByEmailIgnoreCase("revanasidda@mail.com"))
                    .thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.deactivateAccount("revanasidda@mail.com");

            assertThat(user.isEnabled()).isFalse();
            verify(userRepository).save(user);
        }

        @Test
        void shouldRejectAlreadyDeactivatedAccount() {
            User user = UserTestFactory.inactiveUser();
            when(userRepository.findByEmailIgnoreCase("revanasidda@mail.com"))
                    .thenReturn(Optional.of(user));

            assertThatThrownBy(() -> userService.deactivateAccount("revanasidda@mail.com"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(USER_ALREADY_DEACTIVATED));

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("reactivateAccount")
    class ReactivateAccountTests {

        @Test
        void shouldReactivateInactiveAccount() {
            User user = UserTestFactory.inactiveUser();
            when(userRepository.findByEmailIgnoreCase("revanasidda@mail.com"))
                    .thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.reactivateAccount("revanasidda@mail.com");

            assertThat(user.isEnabled()).isTrue();
            verify(userRepository).save(user);
        }

        @Test
        void shouldRejectAlreadyActiveAccount() {
            User user = UserTestFactory.activeUser();
            when(userRepository.findByEmailIgnoreCase("revanasidda@mail.com"))
                    .thenReturn(Optional.of(user));

            assertThatThrownBy(() -> userService.reactivateAccount("revanasidda@mail.com"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(USER_ALREADY_ACTIVATED));

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteAccount")
    class DeleteAccountTests {

        @Test
        void shouldCurrentlyDoNothing() {
            Assertions.assertThatCode(() -> userService.deleteAccount("revanasidda@mail.com"))
                    .doesNotThrowAnyException();
            verifyNoInteractions(userRepository, passwordEncoder, userMapper);
        }
    }
}
