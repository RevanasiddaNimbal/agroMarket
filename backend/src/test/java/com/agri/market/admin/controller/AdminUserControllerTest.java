package com.agri.market.admin.controller;

import com.agri.market.admin.dto.AdminUserDetailResponseDto;
import com.agri.market.admin.dto.AdminUserSearchRequestDto;
import com.agri.market.admin.dto.AdminUserStatusResponseDto;
import com.agri.market.admin.dto.AdminUserSummaryResponseDto;
import com.agri.market.admin.service.AdminUserService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private AdminUserService adminUserService;

    @InjectMocks
    private AdminUserController adminUserController;

    // =========================================================================
    // SEARCH USERS
    // =========================================================================

    @Nested
    class SearchUsers {

        @Test
        void shouldSearchUsersSuccessfully() {

            final AdminUserSearchRequestDto request =
                    new AdminUserSearchRequestDto();

            final AdminUserSummaryResponseDto user =
                    new AdminUserSummaryResponseDto();

            final Page<AdminUserSummaryResponseDto> expectedPage =
                    new PageImpl<>(
                            Collections.singletonList(user),
                            PageRequest.of(0, 10),
                            1
                    );

            when(adminUserService.searchUsers(request))
                    .thenReturn(expectedPage);

            final ResponseEntity<Page<AdminUserSummaryResponseDto>> response =
                    adminUserController.searchUsers(request);

            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getBody())
                    .isEqualTo(expectedPage);

            assertThat(response.getBody().getContent())
                    .containsExactly(user);

            verify(adminUserService)
                    .searchUsers(request);
        }

        @Test
        void shouldReturnEmptyPageWhenNoUsersFound() {

            final AdminUserSearchRequestDto request =
                    new AdminUserSearchRequestDto();

            final Page<AdminUserSummaryResponseDto> emptyPage =
                    new PageImpl<>(
                            Collections.emptyList(),
                            PageRequest.of(0, 10),
                            0
                    );

            when(adminUserService.searchUsers(request))
                    .thenReturn(emptyPage);

            final ResponseEntity<Page<AdminUserSummaryResponseDto>> response =
                    adminUserController.searchUsers(request);

            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getBody())
                    .isEqualTo(emptyPage);

            assertThat(response.getBody().getContent())
                    .isEmpty();

            verify(adminUserService)
                    .searchUsers(request);
        }

        @Test
        void shouldPassExactSearchRequestToService() {

            final AdminUserSearchRequestDto request =
                    new AdminUserSearchRequestDto();

            when(adminUserService.searchUsers(any(AdminUserSearchRequestDto.class)))
                    .thenReturn(
                            new PageImpl<>(
                                    Collections.emptyList()
                            )
                    );

            adminUserController.searchUsers(request);

            final ArgumentCaptor<AdminUserSearchRequestDto> captor =
                    ArgumentCaptor.forClass(AdminUserSearchRequestDto.class);

            verify(adminUserService)
                    .searchUsers(captor.capture());

            assertThat(captor.getValue())
                    .isSameAs(request);
        }

        @Test
        void shouldPropagateExceptionFromService() {

            final AdminUserSearchRequestDto request =
                    new AdminUserSearchRequestDto();

            final RuntimeException exception =
                    new RuntimeException("Search failed");

            when(adminUserService.searchUsers(request))
                    .thenThrow(exception);

            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> adminUserController.searchUsers(request)
            );

            verify(adminUserService)
                    .searchUsers(request);
        }
    }

    // =========================================================================
    // GET USER BY ID
    // =========================================================================

    @Nested
    class GetUserById {

        @Test
        void shouldGetUserByIdSuccessfully() {

            final String userId =
                    UUID.randomUUID().toString();

            final AdminUserDetailResponseDto expectedResponse =
                    new AdminUserDetailResponseDto();

            when(adminUserService.getUserById(userId))
                    .thenReturn(expectedResponse);

            final ResponseEntity<AdminUserDetailResponseDto> response =
                    adminUserController.getUserById(userId);

            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getBody())
                    .isEqualTo(expectedResponse);

            verify(adminUserService)
                    .getUserById(userId);
        }

        @Test
        void shouldPassCorrectUserIdToService() {

            final String userId =
                    UUID.randomUUID().toString();

            when(adminUserService.getUserById(any()))
                    .thenReturn(
                            new AdminUserDetailResponseDto()
                    );

            adminUserController.getUserById(userId);

            verify(adminUserService)
                    .getUserById(eq(userId));
        }

        @Test
        void shouldPropagateExceptionFromService() {

            final String userId =
                    UUID.randomUUID().toString();

            final RuntimeException exception =
                    new RuntimeException("User not found");

            when(adminUserService.getUserById(userId))
                    .thenThrow(exception);

            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> adminUserController.getUserById(userId)
            );

            verify(adminUserService)
                    .getUserById(userId);
        }
    }

    // =========================================================================
    // ACTIVATE USER
    // =========================================================================

    @Nested
    class ActivateUser {

        @Test
        void shouldActivateUserSuccessfully() {

            final String userId =
                    UUID.randomUUID().toString();

            final AdminUserStatusResponseDto expectedResponse =
                    new AdminUserStatusResponseDto();

            when(adminUserService.activateUser(userId))
                    .thenReturn(expectedResponse);

            final ResponseEntity<AdminUserStatusResponseDto> response =
                    adminUserController.activateUser(userId);

            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getBody())
                    .isEqualTo(expectedResponse);

            verify(adminUserService)
                    .activateUser(userId);
        }

        @Test
        void shouldPassCorrectUserIdToService() {

            final String userId =
                    UUID.randomUUID().toString();

            when(adminUserService.activateUser(any()))
                    .thenReturn(
                            new AdminUserStatusResponseDto()
                    );

            adminUserController.activateUser(userId);

            verify(adminUserService)
                    .activateUser(eq(userId));
        }

        @Test
        void shouldPropagateExceptionFromService() {

            final String userId =
                    UUID.randomUUID().toString();

            final RuntimeException exception =
                    new RuntimeException("User is already active");

            when(adminUserService.activateUser(userId))
                    .thenThrow(exception);

            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> adminUserController.activateUser(userId)
            );

            verify(adminUserService)
                    .activateUser(userId);
        }
    }

    // =========================================================================
    // DEACTIVATE USER
    // =========================================================================

    @Nested
    class DeactivateUser {

        @Test
        void shouldDeactivateUserSuccessfully() {

            final String userId =
                    UUID.randomUUID().toString();

            final AdminUserStatusResponseDto expectedResponse =
                    new AdminUserStatusResponseDto();

            when(adminUserService.deactivateUser(userId))
                    .thenReturn(expectedResponse);

            final ResponseEntity<AdminUserStatusResponseDto> response =
                    adminUserController.deactivateUser(userId);

            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getBody())
                    .isEqualTo(expectedResponse);

            verify(adminUserService)
                    .deactivateUser(userId);
        }

        @Test
        void shouldPassCorrectUserIdToService() {

            final String userId =
                    UUID.randomUUID().toString();

            when(adminUserService.deactivateUser(any()))
                    .thenReturn(
                            new AdminUserStatusResponseDto()
                    );

            adminUserController.deactivateUser(userId);

            verify(adminUserService)
                    .deactivateUser(eq(userId));
        }

        @Test
        void shouldPropagateExceptionFromService() {

            final String userId =
                    UUID.randomUUID().toString();

            final RuntimeException exception =
                    new RuntimeException("User is already inactive");

            when(adminUserService.deactivateUser(userId))
                    .thenThrow(exception);

            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> adminUserController.deactivateUser(userId)
            );

            verify(adminUserService)
                    .deactivateUser(userId);
        }
    }

    // =========================================================================
    // LOCK USER
    // =========================================================================

    @Nested
    class LockUser {

        @Test
        void shouldLockUserSuccessfully() {

            final String userId =
                    UUID.randomUUID().toString();

            final AdminUserStatusResponseDto expectedResponse =
                    new AdminUserStatusResponseDto();

            when(adminUserService.lockUser(userId))
                    .thenReturn(expectedResponse);

            final ResponseEntity<AdminUserStatusResponseDto> response =
                    adminUserController.lockUser(userId);

            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getBody())
                    .isEqualTo(expectedResponse);

            verify(adminUserService)
                    .lockUser(userId);
        }

        @Test
        void shouldPassCorrectUserIdToService() {

            final String userId =
                    UUID.randomUUID().toString();

            when(adminUserService.lockUser(any()))
                    .thenReturn(
                            new AdminUserStatusResponseDto()
                    );

            adminUserController.lockUser(userId);

            verify(adminUserService)
                    .lockUser(eq(userId));
        }

        @Test
        void shouldPropagateExceptionFromService() {

            final String userId =
                    UUID.randomUUID().toString();

            final RuntimeException exception =
                    new RuntimeException("User is already locked");

            when(adminUserService.lockUser(userId))
                    .thenThrow(exception);

            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> adminUserController.lockUser(userId)
            );

            verify(adminUserService)
                    .lockUser(userId);
        }
    }

    // =========================================================================
    // UNLOCK USER
    // =========================================================================

    @Nested
    class UnlockUser {

        @Test
        void shouldUnlockUserSuccessfully() {

            final String userId =
                    UUID.randomUUID().toString();

            final AdminUserStatusResponseDto expectedResponse =
                    new AdminUserStatusResponseDto();

            when(adminUserService.unlockUser(userId))
                    .thenReturn(expectedResponse);

            final ResponseEntity<AdminUserStatusResponseDto> response =
                    adminUserController.unlockUser(userId);

            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getBody())
                    .isEqualTo(expectedResponse);

            verify(adminUserService)
                    .unlockUser(userId);
        }

        @Test
        void shouldPassCorrectUserIdToService() {

            final String userId =
                    UUID.randomUUID().toString();

            when(adminUserService.unlockUser(any()))
                    .thenReturn(
                            new AdminUserStatusResponseDto()
                    );

            adminUserController.unlockUser(userId);

            verify(adminUserService)
                    .unlockUser(eq(userId));
        }

        @Test
        void shouldPropagateExceptionFromService() {

            final String userId =
                    UUID.randomUUID().toString();

            final RuntimeException exception =
                    new RuntimeException("User is already unlocked");

            when(adminUserService.unlockUser(userId))
                    .thenThrow(exception);

            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> adminUserController.unlockUser(userId)
            );

            verify(adminUserService)
                    .unlockUser(userId);
        }
    }

    // =========================================================================
    // COMMON CONTROLLER BEHAVIOUR
    // =========================================================================

    @Nested
    class CommonBehaviour {

        @Test
        void shouldNotInteractWithServiceWhenControllerIsCreated() {

            verifyNoInteractions(adminUserService);
        }

        @Test
        void shouldCallSearchServiceOnlyOnce() {

            final AdminUserSearchRequestDto request =
                    new AdminUserSearchRequestDto();

            when(adminUserService.searchUsers(request))
                    .thenReturn(
                            new PageImpl<>(
                                    Collections.emptyList()
                            )
                    );

            adminUserController.searchUsers(request);

            verify(adminUserService, times(1))
                    .searchUsers(request);

            verifyNoMoreInteractions(adminUserService);
        }

        @Test
        void shouldCallGetUserByIdServiceOnlyOnce() {

            final String userId =
                    UUID.randomUUID().toString();

            when(adminUserService.getUserById(userId))
                    .thenReturn(
                            new AdminUserDetailResponseDto()
                    );

            adminUserController.getUserById(userId);

            verify(adminUserService, times(1))
                    .getUserById(userId);

            verifyNoMoreInteractions(adminUserService);
        }

        @Test
        void shouldCallActivateServiceOnlyOnce() {

            final String userId =
                    UUID.randomUUID().toString();

            when(adminUserService.activateUser(userId))
                    .thenReturn(
                            new AdminUserStatusResponseDto()
                    );

            adminUserController.activateUser(userId);

            verify(adminUserService, times(1))
                    .activateUser(userId);

            verifyNoMoreInteractions(adminUserService);
        }

        @Test
        void shouldCallDeactivateServiceOnlyOnce() {

            final String userId =
                    UUID.randomUUID().toString();

            when(adminUserService.deactivateUser(userId))
                    .thenReturn(
                            new AdminUserStatusResponseDto()
                    );

            adminUserController.deactivateUser(userId);

            verify(adminUserService, times(1))
                    .deactivateUser(userId);

            verifyNoMoreInteractions(adminUserService);
        }

        @Test
        void shouldCallLockServiceOnlyOnce() {

            final String userId =
                    UUID.randomUUID().toString();

            when(adminUserService.lockUser(userId))
                    .thenReturn(
                            new AdminUserStatusResponseDto()
                    );

            adminUserController.lockUser(userId);

            verify(adminUserService, times(1))
                    .lockUser(userId);

            verifyNoMoreInteractions(adminUserService);
        }

        @Test
        void shouldCallUnlockServiceOnlyOnce() {

            final String userId =
                    UUID.randomUUID().toString();

            when(adminUserService.unlockUser(userId))
                    .thenReturn(
                            new AdminUserStatusResponseDto()
                    );

            adminUserController.unlockUser(userId);

            verify(adminUserService, times(1))
                    .unlockUser(userId);

            verifyNoMoreInteractions(adminUserService);
        }
    }
}