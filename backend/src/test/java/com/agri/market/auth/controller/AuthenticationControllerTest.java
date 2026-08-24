package com.agri.market.auth.controller;

import com.agri.market.auth.dto.*;
import com.agri.market.auth.service.AuthenticationCookieService;
import com.agri.market.auth.service.AuthenticationService;
import com.agri.market.security.client.ClientInfo;
import com.agri.market.security.client.ClientInfoResolver;
import com.agri.market.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private ClientInfoResolver clientInfoResolver;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private AuthenticationCookieService authenticationCookieService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private ClientInfo clientInfo;

    @Nested
    class Register {

        @Test
        void shouldRegisterUserSuccessfully() {
            final RegistrationRequest request = RegistrationRequest.builder().build();
            final RegistrationResponse expectedResponse = RegistrationResponse.builder().build();

            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(authenticationService.register(request, clientInfo)).thenReturn(expectedResponse);

            final ResponseEntity<RegistrationResponse> response =
                    authenticationController.register(request, httpServletRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isEqualTo(expectedResponse);
            verify(clientInfoResolver).resolve(httpServletRequest);
            verify(authenticationService).register(request, clientInfo);
        }

        @Test
        void shouldResolveClientInfoBeforeRegistering() {
            final RegistrationRequest request = RegistrationRequest.builder().build();

            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(authenticationService.register(any(), any())).thenReturn(RegistrationResponse.builder().build());

            authenticationController.register(request, httpServletRequest);

            verify(authenticationService).register(eq(request), eq(clientInfo));
        }

        @Test
        void shouldPropagateExceptionFromService() {
            final RegistrationRequest request = RegistrationRequest.builder().build();

            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(authenticationService.register(request, clientInfo))
                    .thenThrow(new RuntimeException("email already exists"));

            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> authenticationController.register(request, httpServletRequest)
            );
        }
    }

    @Nested
    class Login {

        @Test
        void shouldAuthenticateUserSuccessfully() {
            final AuthenticationRequest request = AuthenticationRequest.builder().build();
            final AuthenticationResult result = AuthenticationResult.builder()
                    .accessToken("access-token")
                    .refreshToken("refresh-token")
                    .hasPassword(true)
                    .build();

            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(authenticationService.login(request, clientInfo)).thenReturn(result);

            final ResponseEntity<AuthenticationResponse> response =
                    authenticationController.login(request, httpServletRequest, httpServletResponse);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().isHasPassword()).isTrue();
            assertThat(response.getBody().getMessage()).isEqualTo("User authenticated successfully");
            verify(authenticationCookieService).addAuthenticationCookies(
                    httpServletResponse, "access-token", "refresh-token"
            );
        }

        @Test
        void shouldReturnHasPasswordFalseWhenUserHasNoPassword() {
            final AuthenticationRequest request = AuthenticationRequest.builder().build();
            final AuthenticationResult result = AuthenticationResult.builder()
                    .accessToken("access-token")
                    .refreshToken("refresh-token")
                    .hasPassword(false)
                    .build();

            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(authenticationService.login(request, clientInfo)).thenReturn(result);

            final ResponseEntity<AuthenticationResponse> response =
                    authenticationController.login(request, httpServletRequest, httpServletResponse);

            assertThat(response.getBody().isHasPassword()).isFalse();
        }

        @Test
        void shouldSetAuthenticationCookiesOnSuccessfulLogin() {
            final AuthenticationRequest request = AuthenticationRequest.builder().build();
            final AuthenticationResult result = AuthenticationResult.builder()
                    .accessToken("access-token")
                    .refreshToken("refresh-token")
                    .hasPassword(true)
                    .build();

            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(authenticationService.login(request, clientInfo)).thenReturn(result);

            authenticationController.login(request, httpServletRequest, httpServletResponse);

            verify(authenticationCookieService).addAuthenticationCookies(
                    eq(httpServletResponse), eq("access-token"), eq("refresh-token")
            );
        }

        @Test
        void shouldNotSetCookiesWhenServiceThrowsInvalidCredentials() {
            final AuthenticationRequest request = AuthenticationRequest.builder().build();

            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(authenticationService.login(request, clientInfo))
                    .thenThrow(new RuntimeException("Invalid credentials"));

            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> authenticationController.login(request, httpServletRequest, httpServletResponse)
            );

            verifyNoInteractions(authenticationCookieService);
        }
    }

    @Nested
    class RefreshToken {

        @Test
        void shouldRefreshTokensSuccessfully() {
            final AuthenticationResult result = AuthenticationResult.builder()
                    .accessToken("new-access-token")
                    .refreshToken("new-refresh-token")
                    .hasPassword(true)
                    .build();

            when(authenticationCookieService.getRefreshToken(httpServletRequest)).thenReturn("old-refresh-token");
            when(authenticationService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(result);

            final ResponseEntity<AuthenticationResponse> response =
                    authenticationController.refreshToken(httpServletRequest, httpServletResponse);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getMessage()).isEqualTo("Authentication tokens refreshed successfully");
            verify(authenticationCookieService).addAuthenticationCookies(
                    httpServletResponse, "new-access-token", "new-refresh-token"
            );
        }

        @Test
        void shouldExtractRefreshTokenFromCookieBeforeCallingService() {

            when(authenticationCookieService.getRefreshToken(httpServletRequest))
                    .thenReturn("old-refresh-token");

            when(authenticationService.refreshToken(any(RefreshTokenRequest.class)))
                    .thenReturn(
                            AuthenticationResult.builder()
                                    .accessToken("access-token")
                                    .refreshToken("refresh-token")
                                    .hasPassword(true)
                                    .build()
                    );

            authenticationController.refreshToken(
                    httpServletRequest,
                    httpServletResponse
            );

            ArgumentCaptor<RefreshTokenRequest> captor =
                    ArgumentCaptor.forClass(RefreshTokenRequest.class);

            verify(authenticationService)
                    .refreshToken(captor.capture());

            assertThat(captor.getValue().getRefreshToken())
                    .isEqualTo("old-refresh-token");
        }

        @Test
        void shouldThrowWhenRefreshTokenIsInvalidOrExpired() {
            when(authenticationCookieService.getRefreshToken(httpServletRequest)).thenReturn("expired-token");
            when(authenticationService.refreshToken(any(RefreshTokenRequest.class)))
                    .thenThrow(new RuntimeException("Invalid or expired refresh token"));

            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> authenticationController.refreshToken(httpServletRequest, httpServletResponse)
            );

            verify(authenticationCookieService, never()).addAuthenticationCookies(any(), any(), any());
        }
    }

    @Nested
    class Logout {

        @Test
        void shouldLogoutSuccessfully() {
            when(authenticationCookieService.getRefreshToken(httpServletRequest)).thenReturn("refresh-token");

            final ResponseEntity<Void> response =
                    authenticationController.logout(httpServletRequest, httpServletResponse);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(authenticationService).logout("refresh-token");
            verify(authenticationCookieService).clearAuthenticationCookies(httpServletResponse);
        }

        @Test
        void shouldClearCookiesAfterRevokingSession() {
            when(authenticationCookieService.getRefreshToken(httpServletRequest)).thenReturn("refresh-token");

            authenticationController.logout(httpServletRequest, httpServletResponse);

            verify(authenticationCookieService).clearAuthenticationCookies(eq(httpServletResponse));
        }

        @Test
        void shouldNotClearCookiesWhenLogoutServiceThrows() {
            when(authenticationCookieService.getRefreshToken(httpServletRequest)).thenReturn("invalid-token");
            org.mockito.Mockito.doThrow(new RuntimeException("Invalid or expired refresh token"))
                    .when(authenticationService).logout("invalid-token");

            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> authenticationController.logout(httpServletRequest, httpServletResponse)
            );

            verify(authenticationCookieService, never()).clearAuthenticationCookies(any());
        }
    }

    @Nested
    class LogoutAll {

        @Test
        void shouldLogoutFromAllDevicesSuccessfully() {
            final User user = new User();

            final ResponseEntity<Void> response =
                    authenticationController.logoutAll(user, httpServletResponse);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(authenticationService).logoutAll(user.getId());
            verify(authenticationCookieService).clearAuthenticationCookies(httpServletResponse);
        }

        @Test
        void shouldClearCookiesAfterRevokingAllSessions() {
            final UUID userId = UUID.randomUUID();
            final User user = new User();
            user.setId(user.getId());

            authenticationController.logoutAll(user, httpServletResponse);

            verify(authenticationCookieService).clearAuthenticationCookies(eq(httpServletResponse));
        }

        @Test
        void shouldNotClearCookiesWhenLogoutAllServiceThrows() {
            final UUID userId = UUID.randomUUID();
            final User user = new User();
            user.setId(user.getId());

            org.mockito.Mockito.doThrow(new RuntimeException("failure"))
                    .when(authenticationService).logoutAll(user.getId());

            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> authenticationController.logoutAll(user, httpServletResponse)
            );

            verify(authenticationCookieService, never()).clearAuthenticationCookies(any());
        }
    }
}