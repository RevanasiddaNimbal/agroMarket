package com.agri.market.auth.service;

import com.agri.market.auth.dto.AuthenticationResponse;
import com.agri.market.auth.dto.ClientInfo;
import com.agri.market.auth.dto.RefreshTokenRequest;
import com.agri.market.auth.entity.RefreshTokenSession;
import com.agri.market.security.jwt.JwtService;
import com.agri.market.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationTokenServiceImpl")
class AuthenticationTokenServiceImplTest {

    private static final String USERNAME = "user@example.com";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String NEW_ACCESS_TOKEN = "new-access-token";
    private static final String NEW_REFRESH_TOKEN = "new-refresh-token";
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenSessionService refreshTokenSessionService;
    @Mock
    private User user;
    @Mock
    private ClientInfo clientInfo;
    @Mock
    private RefreshTokenRequest refreshTokenRequest;
    @Mock
    private RefreshTokenSession currentSession;
    @InjectMocks
    private AuthenticationTokenServiceImpl authenticationTokenService;

    @Nested
    @DisplayName("createAuthenticationSession")
    class CreateAuthenticationSessionTests {

        @Test
        @DisplayName("should create authentication session and return tokens")
        void shouldCreateAuthenticationSessionAndReturnTokens() {
            when(user.getUsername()).thenReturn(USERNAME);
            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(REFRESH_TOKEN);

            AuthenticationResponse response =
                    authenticationTokenService.createAuthenticationSession(
                            user,
                            clientInfo
                    );

            assertEquals(ACCESS_TOKEN, response.getAccessToken());
            assertEquals(REFRESH_TOKEN, response.getRefreshToken());
            assertEquals("Bearer", response.getTokenType());

            verify(jwtService).generateAccessToken(USERNAME);
            verify(jwtService).generateRefreshToken(USERNAME);
            verify(refreshTokenSessionService)
                    .createSession(
                            REFRESH_TOKEN,
                            user,
                            clientInfo
                    );
        }

        @Test
        @DisplayName("should generate tokens before creating refresh session")
        void shouldGenerateTokensBeforeCreatingRefreshSession() {
            when(user.getUsername()).thenReturn(USERNAME);
            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(REFRESH_TOKEN);

            authenticationTokenService.createAuthenticationSession(
                    user,
                    clientInfo
            );

            InOrder inOrder =
                    inOrder(jwtService, refreshTokenSessionService);

            inOrder.verify(jwtService)
                    .generateAccessToken(USERNAME);
            inOrder.verify(jwtService)
                    .generateRefreshToken(USERNAME);
            inOrder.verify(refreshTokenSessionService)
                    .createSession(
                            REFRESH_TOKEN,
                            user,
                            clientInfo
                    );
        }

        @Test
        @DisplayName("should not generate refresh token when access token generation fails")
        void shouldNotGenerateRefreshTokenWhenAccessTokenGenerationFails() {
            when(user.getUsername()).thenReturn(USERNAME);

            RuntimeException exception =
                    new RuntimeException("Access token generation failed");

            when(jwtService.generateAccessToken(USERNAME))
                    .thenThrow(exception);

            assertThrows(
                    RuntimeException.class,
                    () -> authenticationTokenService.createAuthenticationSession(
                            user,
                            clientInfo
                    )
            );

            verify(jwtService).generateAccessToken(USERNAME);
            verify(jwtService, never())
                    .generateRefreshToken(anyString());
            verify(refreshTokenSessionService, never())
                    .createSession(
                            anyString(),
                            any(User.class),
                            any(ClientInfo.class)
                    );
        }

        @Test
        @DisplayName("should not create session when refresh token generation fails")
        void shouldNotCreateSessionWhenRefreshTokenGenerationFails() {
            when(user.getUsername()).thenReturn(USERNAME);
            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(ACCESS_TOKEN);

            when(jwtService.generateRefreshToken(USERNAME))
                    .thenThrow(
                            new RuntimeException(
                                    "Refresh token generation failed"
                            )
                    );

            assertThrows(
                    RuntimeException.class,
                    () -> authenticationTokenService.createAuthenticationSession(
                            user,
                            clientInfo
                    )
            );

            verify(jwtService).generateAccessToken(USERNAME);
            verify(jwtService).generateRefreshToken(USERNAME);
            verify(refreshTokenSessionService, never())
                    .createSession(
                            anyString(),
                            any(User.class),
                            any(ClientInfo.class)
                    );
        }

        @Test
        @DisplayName("should propagate session creation exception")
        void shouldPropagateSessionCreationException() {
            when(user.getUsername()).thenReturn(USERNAME);
            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(REFRESH_TOKEN);

            RuntimeException exception =
                    new RuntimeException("Session creation failed");

            doThrow(exception)
                    .when(refreshTokenSessionService)
                    .createSession(
                            REFRESH_TOKEN,
                            user,
                            clientInfo
                    );

            assertThrows(
                    RuntimeException.class,
                    () -> authenticationTokenService.createAuthenticationSession(
                            user,
                            clientInfo
                    )
            );

            verify(refreshTokenSessionService)
                    .createSession(
                            REFRESH_TOKEN,
                            user,
                            clientInfo
                    );
        }
    }

    @Nested
    @DisplayName("refreshAuthenticationSession")
    class RefreshAuthenticationSessionTests {

        @Test
        @DisplayName("should refresh authentication session and return new tokens")
        void shouldRefreshAuthenticationSessionAndReturnNewTokens() {
            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);
            when(refreshTokenSessionService.findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);
            when(currentSession.getUser())
                    .thenReturn(user);
            when(user.getUsername())
                    .thenReturn(USERNAME);
            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(NEW_ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(NEW_REFRESH_TOKEN);

            AuthenticationResponse response =
                    authenticationTokenService.refreshAuthenticationSession(
                            refreshTokenRequest
                    );

            assertEquals(
                    NEW_ACCESS_TOKEN,
                    response.getAccessToken()
            );
            assertEquals(
                    NEW_REFRESH_TOKEN,
                    response.getRefreshToken()
            );
            assertEquals("Bearer", response.getTokenType());

            verify(refreshTokenRequest)
                    .getRefreshToken();
            verify(refreshTokenSessionService)
                    .findValidSession(REFRESH_TOKEN);
            verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);
            verify(jwtService)
                    .generateAccessToken(USERNAME);
            verify(jwtService)
                    .generateRefreshToken(USERNAME);
            verify(refreshTokenSessionService)
                    .rotateSession(
                            currentSession,
                            NEW_REFRESH_TOKEN
                    );
        }

        @Test
        @DisplayName("should find session before validating refresh token")
        void shouldFindSessionBeforeValidatingRefreshToken() {
            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);
            when(refreshTokenSessionService.findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);
            when(currentSession.getUser())
                    .thenReturn(user);
            when(user.getUsername())
                    .thenReturn(USERNAME);
            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(NEW_ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(NEW_REFRESH_TOKEN);

            authenticationTokenService.refreshAuthenticationSession(
                    refreshTokenRequest
            );

            InOrder inOrder =
                    inOrder(
                            refreshTokenSessionService,
                            jwtService
                    );

            inOrder.verify(refreshTokenSessionService)
                    .findValidSession(REFRESH_TOKEN);
            inOrder.verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);
        }

        @Test
        @DisplayName("should rotate session after generating new tokens")
        void shouldRotateSessionAfterGeneratingNewTokens() {
            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);
            when(refreshTokenSessionService.findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);
            when(currentSession.getUser())
                    .thenReturn(user);
            when(user.getUsername())
                    .thenReturn(USERNAME);
            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(NEW_ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(NEW_REFRESH_TOKEN);

            authenticationTokenService.refreshAuthenticationSession(
                    refreshTokenRequest
            );

            InOrder inOrder =
                    inOrder(
                            jwtService,
                            refreshTokenSessionService
                    );

            inOrder.verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);
            inOrder.verify(jwtService)
                    .generateAccessToken(USERNAME);
            inOrder.verify(jwtService)
                    .generateRefreshToken(USERNAME);
            inOrder.verify(refreshTokenSessionService)
                    .rotateSession(
                            currentSession,
                            NEW_REFRESH_TOKEN
                    );
        }

        @Test
        @DisplayName("should not validate token when session lookup fails")
        void shouldNotValidateTokenWhenSessionLookupFails() {
            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);

            RuntimeException exception =
                    new RuntimeException("Session not found");

            when(refreshTokenSessionService.findValidSession(REFRESH_TOKEN))
                    .thenThrow(exception);

            assertThrows(
                    RuntimeException.class,
                    () -> authenticationTokenService.refreshAuthenticationSession(
                            refreshTokenRequest
                    )
            );

            verify(refreshTokenSessionService)
                    .findValidSession(REFRESH_TOKEN);
            verify(jwtService, never())
                    .validateRefreshToken(anyString());
            verify(jwtService, never())
                    .generateAccessToken(anyString());
            verify(jwtService, never())
                    .generateRefreshToken(anyString());
            verify(refreshTokenSessionService, never())
                    .rotateSession(
                            any(RefreshTokenSession.class),
                            anyString()
                    );
        }

        @Test
        @DisplayName("should not generate new tokens when refresh token validation fails")
        void shouldNotGenerateNewTokensWhenRefreshTokenValidationFails() {
            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);
            when(refreshTokenSessionService.findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);

            doThrow(
                    new IllegalArgumentException(
                            "Invalid refresh token"
                    )
            )
                    .when(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);

            assertThrows(
                    IllegalArgumentException.class,
                    () -> authenticationTokenService.refreshAuthenticationSession(
                            refreshTokenRequest
                    )
            );

            verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);
            verify(jwtService, never())
                    .generateAccessToken(anyString());
            verify(jwtService, never())
                    .generateRefreshToken(anyString());
            verify(refreshTokenSessionService, never())
                    .rotateSession(
                            any(RefreshTokenSession.class),
                            anyString()
                    );
        }

        @Test
        @DisplayName("should not rotate session when access token generation fails")
        void shouldNotRotateSessionWhenAccessTokenGenerationFails() {
            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);
            when(refreshTokenSessionService.findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);
            when(currentSession.getUser())
                    .thenReturn(user);
            when(user.getUsername())
                    .thenReturn(USERNAME);
            when(jwtService.generateAccessToken(USERNAME))
                    .thenThrow(
                            new RuntimeException(
                                    "Access token generation failed"
                            )
                    );

            assertThrows(
                    RuntimeException.class,
                    () -> authenticationTokenService.refreshAuthenticationSession(
                            refreshTokenRequest
                    )
            );

            verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);
            verify(jwtService)
                    .generateAccessToken(USERNAME);
            verify(jwtService, never())
                    .generateRefreshToken(anyString());
            verify(refreshTokenSessionService, never())
                    .rotateSession(
                            any(RefreshTokenSession.class),
                            anyString()
                    );
        }

        @Test
        @DisplayName("should not rotate session when refresh token generation fails")
        void shouldNotRotateSessionWhenRefreshTokenGenerationFails() {
            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);
            when(refreshTokenSessionService.findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);
            when(currentSession.getUser())
                    .thenReturn(user);
            when(user.getUsername())
                    .thenReturn(USERNAME);
            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(NEW_ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(USERNAME))
                    .thenThrow(
                            new RuntimeException(
                                    "Refresh token generation failed"
                            )
                    );

            assertThrows(
                    RuntimeException.class,
                    () -> authenticationTokenService.refreshAuthenticationSession(
                            refreshTokenRequest
                    )
            );

            verify(jwtService)
                    .generateAccessToken(USERNAME);
            verify(jwtService)
                    .generateRefreshToken(USERNAME);
            verify(refreshTokenSessionService, never())
                    .rotateSession(
                            any(RefreshTokenSession.class),
                            anyString()
                    );
        }

        @Test
        @DisplayName("should propagate session rotation exception")
        void shouldPropagateSessionRotationException() {
            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);
            when(refreshTokenSessionService.findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);
            when(currentSession.getUser())
                    .thenReturn(user);
            when(user.getUsername())
                    .thenReturn(USERNAME);
            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(NEW_ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(NEW_REFRESH_TOKEN);

            doThrow(
                    new RuntimeException("Session rotation failed")
            )
                    .when(refreshTokenSessionService)
                    .rotateSession(
                            currentSession,
                            NEW_REFRESH_TOKEN
                    );

            assertThrows(
                    RuntimeException.class,
                    () -> authenticationTokenService.refreshAuthenticationSession(
                            refreshTokenRequest
                    )
            );

            verify(refreshTokenSessionService)
                    .rotateSession(
                            currentSession,
                            NEW_REFRESH_TOKEN
                    );
        }
    }
}