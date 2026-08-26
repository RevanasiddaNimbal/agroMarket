package com.agri.market.admin.service;

import com.agri.market.admin.dto.AdminUserDetailResponseDto;
import com.agri.market.admin.dto.AdminUserSearchRequestDto;
import com.agri.market.admin.dto.AdminUserStatusResponseDto;
import com.agri.market.admin.dto.AdminUserSummaryResponseDto;
import com.agri.market.admin.mapper.AdminUserMapper;
import com.agri.market.common.exception.BusinessException;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminUserMapper adminUserMapper;

    @InjectMocks
    private AdminUserServiceImpl adminUserService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user-123");
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setEmailVerified(true);
        user.setPhoneVerified(false);
    }

    @Nested
    class SearchUsersTests {

        @Test
        void shouldReturnUsersWhenSearchRequestIsValid() {
            AdminUserSearchRequestDto request =
                    mock(AdminUserSearchRequestDto.class);

            when(request.getPage()).thenReturn(0);
            when(request.getSize()).thenReturn(10);
            when(request.getSearch()).thenReturn(null);
            when(request.getRole()).thenReturn(null);
            when(request.getEnabled()).thenReturn(null);
            when(request.getEmailVerified()).thenReturn(null);
            when(request.getPhoneVerified()).thenReturn(null);

            AdminUserSummaryResponseDto response =
                    mock(AdminUserSummaryResponseDto.class);

            Page<User> userPage =
                    new PageImpl<>(List.of(user));

            when(userRepository.findAll(
                    any(Specification.class),
                    any(Pageable.class)
            )).thenReturn(userPage);

            when(adminUserMapper.toSummaryResponseDto(user))
                    .thenReturn(response);

            Page<AdminUserSummaryResponseDto> result =
                    adminUserService.searchUsers(request);

            assertThat(result).isNotNull();
            assertThat(result.getContent())
                    .hasSize(1)
                    .containsExactly(response);

            verify(userRepository).findAll(
                    any(Specification.class),
                    eq(PageRequest.of(0, 10))
            );

            verify(adminUserMapper)
                    .toSummaryResponseDto(user);
        }

        @Test
        void shouldReturnEmptyPageWhenNoUsersMatch() {
            AdminUserSearchRequestDto request =
                    mock(AdminUserSearchRequestDto.class);

            when(request.getPage()).thenReturn(0);
            when(request.getSize()).thenReturn(10);
            when(request.getSearch()).thenReturn(null);
            when(request.getRole()).thenReturn(null);
            when(request.getEnabled()).thenReturn(null);
            when(request.getEmailVerified()).thenReturn(null);
            when(request.getPhoneVerified()).thenReturn(null);

            when(userRepository.findAll(
                    any(Specification.class),
                    any(Pageable.class)
            )).thenReturn(Page.empty());

            Page<AdminUserSummaryResponseDto> result =
                    adminUserService.searchUsers(request);

            assertThat(result).isEmpty();

            verify(userRepository).findAll(
                    any(Specification.class),
                    eq(PageRequest.of(0, 10))
            );

            verifyNoInteractions(adminUserMapper);
        }

        @Test
        void shouldApplySearchFilter() {
            AdminUserSearchRequestDto request =
                    mock(AdminUserSearchRequestDto.class);

            when(request.getPage()).thenReturn(0);
            when(request.getSize()).thenReturn(20);
            when(request.getSearch()).thenReturn("test");
            when(request.getRole()).thenReturn(null);
            when(request.getEnabled()).thenReturn(null);
            when(request.getEmailVerified()).thenReturn(null);
            when(request.getPhoneVerified()).thenReturn(null);

            when(userRepository.findAll(
                    any(Specification.class),
                    any(Pageable.class)
            )).thenReturn(Page.empty());

            adminUserService.searchUsers(request);

            ArgumentCaptor<Specification<User>> captor =
                    ArgumentCaptor.forClass(Specification.class);

            verify(userRepository).findAll(
                    captor.capture(),
                    eq(PageRequest.of(0, 20))
            );

            assertThat(captor.getValue()).isNotNull();
        }

        @Test
        void shouldApplyRoleFilter() {
            AdminUserSearchRequestDto request =
                    mock(AdminUserSearchRequestDto.class);

            when(request.getPage()).thenReturn(0);
            when(request.getSize()).thenReturn(10);
            when(request.getSearch()).thenReturn(null);
            when(request.getRole()).thenReturn("FARMER");
            when(request.getEnabled()).thenReturn(null);
            when(request.getEmailVerified()).thenReturn(null);
            when(request.getPhoneVerified()).thenReturn(null);

            when(userRepository.findAll(
                    any(Specification.class),
                    any(Pageable.class)
            )).thenReturn(Page.empty());

            adminUserService.searchUsers(request);

            verify(userRepository).findAll(
                    any(Specification.class),
                    eq(PageRequest.of(0, 10))
            );
        }

        @Test
        void shouldApplyEnabledFilter() {
            AdminUserSearchRequestDto request =
                    mock(AdminUserSearchRequestDto.class);

            when(request.getPage()).thenReturn(0);
            when(request.getSize()).thenReturn(10);
            when(request.getSearch()).thenReturn(null);
            when(request.getRole()).thenReturn(null);
            when(request.getEnabled()).thenReturn(false);
            when(request.getEmailVerified()).thenReturn(null);
            when(request.getPhoneVerified()).thenReturn(null);

            when(userRepository.findAll(
                    any(Specification.class),
                    any(Pageable.class)
            )).thenReturn(Page.empty());

            adminUserService.searchUsers(request);

            verify(userRepository).findAll(
                    any(Specification.class),
                    eq(PageRequest.of(0, 10))
            );
        }

        @Test
        void shouldApplyEmailVerifiedFilter() {
            AdminUserSearchRequestDto request =
                    mock(AdminUserSearchRequestDto.class);

            when(request.getPage()).thenReturn(0);
            when(request.getSize()).thenReturn(10);
            when(request.getSearch()).thenReturn(null);
            when(request.getRole()).thenReturn(null);
            when(request.getEnabled()).thenReturn(null);
            when(request.getEmailVerified()).thenReturn(true);
            when(request.getPhoneVerified()).thenReturn(null);

            when(userRepository.findAll(
                    any(Specification.class),
                    any(Pageable.class)
            )).thenReturn(Page.empty());

            adminUserService.searchUsers(request);

            verify(userRepository).findAll(
                    any(Specification.class),
                    eq(PageRequest.of(0, 10))
            );
        }

        @Test
        void shouldApplyPhoneVerifiedFilter() {
            AdminUserSearchRequestDto request =
                    mock(AdminUserSearchRequestDto.class);

            when(request.getPage()).thenReturn(0);
            when(request.getSize()).thenReturn(10);
            when(request.getSearch()).thenReturn(null);
            when(request.getRole()).thenReturn(null);
            when(request.getEnabled()).thenReturn(null);
            when(request.getEmailVerified()).thenReturn(null);
            when(request.getPhoneVerified()).thenReturn(true);

            when(userRepository.findAll(
                    any(Specification.class),
                    any(Pageable.class)
            )).thenReturn(Page.empty());

            adminUserService.searchUsers(request);

            verify(userRepository).findAll(
                    any(Specification.class),
                    eq(PageRequest.of(0, 10))
            );
        }

        @Test
        void shouldApplyAllFilters() {
            AdminUserSearchRequestDto request =
                    mock(AdminUserSearchRequestDto.class);

            when(request.getPage()).thenReturn(1);
            when(request.getSize()).thenReturn(25);
            when(request.getSearch()).thenReturn("test");
            when(request.getRole()).thenReturn("FARMER");
            when(request.getEnabled()).thenReturn(true);
            when(request.getEmailVerified()).thenReturn(true);
            when(request.getPhoneVerified()).thenReturn(false);

            when(userRepository.findAll(
                    any(Specification.class),
                    any(Pageable.class)
            )).thenReturn(Page.empty());

            adminUserService.searchUsers(request);

            verify(userRepository).findAll(
                    any(Specification.class),
                    eq(PageRequest.of(1, 25))
            );
        }
    }

    @Nested
    class GetUserByIdTests {

        @Test
        void shouldReturnUserDetailsWhenUserExists() {
            AdminUserDetailResponseDto response =
                    mock(AdminUserDetailResponseDto.class);

            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.of(user));

            when(adminUserMapper.toDetailResponseDto(user))
                    .thenReturn(response);

            AdminUserDetailResponseDto result =
                    adminUserService.getUserById("user-123");

            assertThat(result)
                    .isSameAs(response);

            verify(userRepository)
                    .findById("user-123");

            verify(adminUserMapper)
                    .toDetailResponseDto(user);
        }

        @Test
        void shouldThrowExceptionWhenUserDoesNotExist() {
            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    adminUserService.getUserById("user-123")
            )
                    .isInstanceOf(BusinessException.class);

            verify(userRepository)
                    .findById("user-123");

            verifyNoInteractions(adminUserMapper);
        }
    }

    @Nested
    class ActivateUserTests {

        @Test
        void shouldActivateInactiveUser() {
            user.setEnabled(false);

            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(user))
                    .thenReturn(user);

            AdminUserStatusResponseDto result =
                    adminUserService.activateUser("user-123");

            assertThat(user.isEnabled())
                    .isTrue();

            assertThat(result)
                    .isNotNull();

            assertThat(result.getUserId())
                    .isEqualTo("user-123");

            assertThat(result.isEnabled())
                    .isTrue();

            assertThat(result.isAccountLocked())
                    .isFalse();

            assertThat(result.getMessage())
                    .isEqualTo("User account activated successfully.");

            verify(userRepository)
                    .findById("user-123");

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldThrowExceptionWhenUserIsAlreadyActive() {
            user.setEnabled(true);

            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.of(user));

            assertThatThrownBy(() ->
                    adminUserService.activateUser("user-123")
            )
                    .isInstanceOf(BusinessException.class);

            verify(userRepository)
                    .findById("user-123");

            verify(userRepository, never())
                    .save(any(User.class));
        }

        @Test
        void shouldThrowExceptionWhenUserDoesNotExist() {
            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    adminUserService.activateUser("user-123")
            )
                    .isInstanceOf(BusinessException.class);

            verify(userRepository)
                    .findById("user-123");

            verify(userRepository, never())
                    .save(any(User.class));
        }
    }

    @Nested
    class DeactivateUserTests {

        @Test
        void shouldDeactivateActiveUser() {
            user.setEnabled(true);

            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(user))
                    .thenReturn(user);

            AdminUserStatusResponseDto result =
                    adminUserService.deactivateUser("user-123");

            assertThat(user.isEnabled())
                    .isFalse();

            assertThat(result)
                    .isNotNull();

            assertThat(result.getUserId())
                    .isEqualTo("user-123");

            assertThat(result.isEnabled())
                    .isFalse();

            assertThat(result.getMessage())
                    .isEqualTo("User account deactivated successfully.");

            verify(userRepository)
                    .findById("user-123");

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldThrowExceptionWhenUserIsAlreadyInactive() {
            user.setEnabled(false);

            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.of(user));

            assertThatThrownBy(() ->
                    adminUserService.deactivateUser("user-123")
            )
                    .isInstanceOf(BusinessException.class);

            verify(userRepository)
                    .findById("user-123");

            verify(userRepository, never())
                    .save(any(User.class));
        }

        @Test
        void shouldThrowExceptionWhenUserDoesNotExist() {
            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    adminUserService.deactivateUser("user-123")
            )
                    .isInstanceOf(BusinessException.class);

            verify(userRepository)
                    .findById("user-123");

            verify(userRepository, never())
                    .save(any(User.class));
        }
    }

    @Nested
    class LockUserTests {

        @Test
        void shouldLockUnlockedUser() {
            user.setAccountLocked(false);

            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(user))
                    .thenReturn(user);

            AdminUserStatusResponseDto result =
                    adminUserService.lockUser("user-123");

            assertThat(user.isAccountLocked())
                    .isTrue();

            assertThat(result)
                    .isNotNull();

            assertThat(result.getUserId())
                    .isEqualTo("user-123");

            assertThat(result.isAccountLocked())
                    .isTrue();

            assertThat(result.isEnabled())
                    .isTrue();

            assertThat(result.getMessage())
                    .isEqualTo("User account locked successfully.");

            verify(userRepository)
                    .findById("user-123");

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldThrowExceptionWhenUserIsAlreadyLocked() {
            user.setAccountLocked(true);

            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.of(user));

            assertThatThrownBy(() ->
                    adminUserService.lockUser("user-123")
            )
                    .isInstanceOf(BusinessException.class);

            verify(userRepository)
                    .findById("user-123");

            verify(userRepository, never())
                    .save(any(User.class));
        }

        @Test
        void shouldThrowExceptionWhenUserDoesNotExist() {
            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    adminUserService.lockUser("user-123")
            )
                    .isInstanceOf(BusinessException.class);

            verify(userRepository)
                    .findById("user-123");

            verify(userRepository, never())
                    .save(any(User.class));
        }
    }

    @Nested
    class UnlockUserTests {

        @Test
        void shouldUnlockLockedUser() {
            user.setAccountLocked(true);

            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(user))
                    .thenReturn(user);

            AdminUserStatusResponseDto result =
                    adminUserService.unlockUser("user-123");

            assertThat(user.isAccountLocked())
                    .isFalse();

            assertThat(result)
                    .isNotNull();

            assertThat(result.getUserId())
                    .isEqualTo("user-123");

            assertThat(result.isAccountLocked())
                    .isFalse();

            assertThat(result.isEnabled())
                    .isTrue();

            assertThat(result.getMessage())
                    .isEqualTo("User account unlocked successfully.");

            verify(userRepository)
                    .findById("user-123");

            verify(userRepository)
                    .save(user);
        }

        @Test
        void shouldThrowExceptionWhenUserIsAlreadyUnlocked() {
            user.setAccountLocked(false);

            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.of(user));

            assertThatThrownBy(() ->
                    adminUserService.unlockUser("user-123")
            )
                    .isInstanceOf(BusinessException.class);

            verify(userRepository)
                    .findById("user-123");

            verify(userRepository, never())
                    .save(any(User.class));
        }

        @Test
        void shouldThrowExceptionWhenUserDoesNotExist() {
            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    adminUserService.unlockUser("user-123")
            )
                    .isInstanceOf(BusinessException.class);

            verify(userRepository)
                    .findById("user-123");

            verify(userRepository, never())
                    .save(any(User.class));
        }
    }

    @Nested
    class StatusResponseTests {

        @Test
        void shouldReturnCorrectStatusAfterActivation() {
            user.setEnabled(false);
            user.setAccountLocked(true);

            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(user))
                    .thenReturn(user);

            AdminUserStatusResponseDto result =
                    adminUserService.activateUser("user-123");

            assertThat(result.getUserId())
                    .isEqualTo("user-123");

            assertThat(result.isEnabled())
                    .isTrue();

            assertThat(result.isAccountLocked())
                    .isTrue();

            assertThat(result.getMessage())
                    .isEqualTo("User account activated successfully.");
        }

        @Test
        void shouldReturnCorrectStatusAfterDeactivation() {
            user.setEnabled(true);
            user.setAccountLocked(false);

            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(user))
                    .thenReturn(user);

            AdminUserStatusResponseDto result =
                    adminUserService.deactivateUser("user-123");

            assertThat(result.getUserId())
                    .isEqualTo("user-123");

            assertThat(result.isEnabled())
                    .isFalse();

            assertThat(result.isAccountLocked())
                    .isFalse();

            assertThat(result.getMessage())
                    .isEqualTo("User account deactivated successfully.");
        }

        @Test
        void shouldReturnCorrectStatusAfterLocking() {
            user.setEnabled(true);
            user.setAccountLocked(false);

            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(user))
                    .thenReturn(user);

            AdminUserStatusResponseDto result =
                    adminUserService.lockUser("user-123");

            assertThat(result.getUserId())
                    .isEqualTo("user-123");

            assertThat(result.isEnabled())
                    .isTrue();

            assertThat(result.isAccountLocked())
                    .isTrue();

            assertThat(result.getMessage())
                    .isEqualTo("User account locked successfully.");
        }

        @Test
        void shouldReturnCorrectStatusAfterUnlocking() {
            user.setEnabled(true);
            user.setAccountLocked(true);

            when(userRepository.findById("user-123"))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(user))
                    .thenReturn(user);

            AdminUserStatusResponseDto result =
                    adminUserService.unlockUser("user-123");

            assertThat(result.getUserId())
                    .isEqualTo("user-123");

            assertThat(result.isEnabled())
                    .isTrue();

            assertThat(result.isAccountLocked())
                    .isFalse();

            assertThat(result.getMessage())
                    .isEqualTo("User account unlocked successfully.");
        }
    }
}