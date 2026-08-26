package com.agri.market.auth.service;

import com.agri.market.auth.dto.AuthenticationResult;
import com.agri.market.auth.dto.RefreshTokenRequest;
import com.agri.market.auth.entity.RefreshTokenSession;
import com.agri.market.common.exception.BusinessException;
import com.agri.market.security.client.ClientInfo;
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

import static com.agri.market.common.exception.ErrorCode.ERR_USER_DISABLED;
import static com.agri.market.common.exception.ErrorCode.PERMANENT_ACCOUNT_LOCKED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationTokenServiceImpl")
class AuthenticationTokenServiceImplTest {

    private static final String USERNAME = "user@example.com";

    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";

    private static final String NEW_ACCESS_TOKEN = "new-access-token";
    private static final String NEW_REFRESH_TOKEN = "new-refresh-token";

    private static final String PASSWORD_HASH =
            "$2a$10$some-valid-password-hash";

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenSessionService refreshTokenSessionServiceImpl;

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


    // ============================================================
    // CREATE AUTHENTICATION SESSION
    // ============================================================

    @Nested
    @DisplayName("createAuthenticationSession")
    class CreateAuthenticationSessionTests {

        @Test
        @DisplayName("should create authentication session for active user with password")
        void shouldCreateAuthenticationSessionForActiveUserWithPassword() {

            when(user.isEnabled()).thenReturn(true);
            when(user.isAccountLocked()).thenReturn(false);
            when(user.getUsername()).thenReturn(USERNAME);
            when(user.getPassword()).thenReturn(PASSWORD_HASH);

            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(ACCESS_TOKEN);

            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(REFRESH_TOKEN);

            AuthenticationResult response =
                    authenticationTokenService.createAuthenticationSession(
                            user,
                            clientInfo
                    );

            assertEquals(
                    ACCESS_TOKEN,
                    response.getAccessToken()
            );

            assertEquals(
                    REFRESH_TOKEN,
                    response.getRefreshToken()
            );

            assertEquals(
                    "Bearer",
                    response.getTokenType()
            );

            assertTrue(response.isHasPassword());

            verify(jwtService)
                    .generateAccessToken(USERNAME);

            verify(jwtService)
                    .generateRefreshToken(USERNAME);

            verify(refreshTokenSessionServiceImpl)
                    .createSession(
                            REFRESH_TOKEN,
                            user,
                            clientInfo
                    );
        }


        @Test
        @DisplayName("should create authentication session for active OAuth user without password")
        void shouldCreateAuthenticationSessionForActiveOAuthUserWithoutPassword() {

            when(user.isEnabled()).thenReturn(true);
            when(user.isAccountLocked()).thenReturn(false);
            when(user.getUsername()).thenReturn(USERNAME);
            when(user.getPassword()).thenReturn(null);

            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(ACCESS_TOKEN);

            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(REFRESH_TOKEN);

            AuthenticationResult response =
                    authenticationTokenService.createAuthenticationSession(
                            user,
                            clientInfo
                    );

            assertEquals(
                    ACCESS_TOKEN,
                    response.getAccessToken()
            );

            assertEquals(
                    REFRESH_TOKEN,
                    response.getRefreshToken()
            );

            assertEquals(
                    "Bearer",
                    response.getTokenType()
            );

            assertFalse(response.isHasPassword());

            verify(jwtService)
                    .generateAccessToken(USERNAME);

            verify(jwtService)
                    .generateRefreshToken(USERNAME);

            verify(refreshTokenSessionServiceImpl)
                    .createSession(
                            REFRESH_TOKEN,
                            user,
                            clientInfo
                    );
        }


        @Test
        @DisplayName("should return hasPassword false when password is blank")
        void shouldReturnHasPasswordFalseWhenPasswordIsBlank() {

            when(user.isEnabled()).thenReturn(true);
            when(user.isAccountLocked()).thenReturn(false);
            when(user.getUsername()).thenReturn(USERNAME);
            when(user.getPassword()).thenReturn("   ");

            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(ACCESS_TOKEN);

            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(REFRESH_TOKEN);

            AuthenticationResult response =
                    authenticationTokenService.createAuthenticationSession(
                            user,
                            clientInfo
                    );

            assertFalse(response.isHasPassword());

            verify(jwtService)
                    .generateAccessToken(USERNAME);

            verify(jwtService)
                    .generateRefreshToken(USERNAME);

            verify(refreshTokenSessionServiceImpl)
                    .createSession(
                            REFRESH_TOKEN,
                            user,
                            clientInfo
                    );
        }


        @Test
        @DisplayName("should generate access token before refresh token")
        void shouldGenerateAccessTokenBeforeRefreshToken() {

            when(user.isEnabled()).thenReturn(true);
            when(user.isAccountLocked()).thenReturn(false);
            when(user.getUsername()).thenReturn(USERNAME);
            when(user.getPassword()).thenReturn(PASSWORD_HASH);

            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(ACCESS_TOKEN);

            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(REFRESH_TOKEN);

            authenticationTokenService.createAuthenticationSession(
                    user,
                    clientInfo
            );

            InOrder inOrder =
                    inOrder(
                            jwtService,
                            refreshTokenSessionServiceImpl
                    );

            inOrder.verify(jwtService)
                    .generateAccessToken(USERNAME);

            inOrder.verify(jwtService)
                    .generateRefreshToken(USERNAME);

            inOrder.verify(refreshTokenSessionServiceImpl)
                    .createSession(
                            REFRESH_TOKEN,
                            user,
                            clientInfo
                    );
        }


        @Test
        @DisplayName("should not generate tokens when user is null")
        void shouldNotGenerateTokensWhenUserIsNull() {

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> authenticationTokenService
                                    .createAuthenticationSession(
                                            null,
                                            clientInfo
                                    )
                    );

            assertEquals(
                    PERMANENT_ACCOUNT_LOCKED,
                    exception.getErrorCode()
            );

            verify(jwtService, never())
                    .generateAccessToken(anyString());

            verify(jwtService, never())
                    .generateRefreshToken(anyString());

            verify(refreshTokenSessionServiceImpl, never())
                    .createSession(
                            anyString(),
                            any(User.class),
                            any(ClientInfo.class)
                    );
        }


        @Test
        @DisplayName("should not generate tokens when user is disabled")
        void shouldNotGenerateTokensWhenUserIsDisabled() {

            when(user.isEnabled()).thenReturn(false);

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> authenticationTokenService
                                    .createAuthenticationSession(
                                            user,
                                            clientInfo
                                    )
                    );

            assertEquals(
                    ERR_USER_DISABLED,
                    exception.getErrorCode()
            );

            verify(jwtService, never())
                    .generateAccessToken(anyString());

            verify(jwtService, never())
                    .generateRefreshToken(anyString());

            verify(refreshTokenSessionServiceImpl, never())
                    .createSession(
                            anyString(),
                            any(User.class),
                            any(ClientInfo.class)
                    );
        }


        @Test
        @DisplayName("should not generate tokens when user is permanently locked")
        void shouldNotGenerateTokensWhenUserIsPermanentlyLocked() {

            when(user.isEnabled()).thenReturn(true);
            when(user.isAccountLocked()).thenReturn(true);

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> authenticationTokenService
                                    .createAuthenticationSession(
                                            user,
                                            clientInfo
                                    )
                    );

            assertEquals(
                    PERMANENT_ACCOUNT_LOCKED,
                    exception.getErrorCode()
            );

            verify(jwtService, never())
                    .generateAccessToken(anyString());

            verify(jwtService, never())
                    .generateRefreshToken(anyString());

            verify(refreshTokenSessionServiceImpl, never())
                    .createSession(
                            anyString(),
                            any(User.class),
                            any(ClientInfo.class)
                    );
        }


        @Test
        @DisplayName("should not generate refresh token when access token generation fails")
        void shouldNotGenerateRefreshTokenWhenAccessTokenGenerationFails() {

            when(user.isEnabled()).thenReturn(true);
            when(user.isAccountLocked()).thenReturn(false);
            when(user.getUsername()).thenReturn(USERNAME);

            RuntimeException exception =
                    new RuntimeException(
                            "Access token generation failed"
                    );

            when(jwtService.generateAccessToken(USERNAME))
                    .thenThrow(exception);

            assertThrows(
                    RuntimeException.class,
                    () -> authenticationTokenService
                            .createAuthenticationSession(
                                    user,
                                    clientInfo
                            )
            );

            verify(jwtService)
                    .generateAccessToken(USERNAME);

            verify(jwtService, never())
                    .generateRefreshToken(anyString());

            verify(refreshTokenSessionServiceImpl, never())
                    .createSession(
                            anyString(),
                            any(User.class),
                            any(ClientInfo.class)
                    );
        }


        @Test
        @DisplayName("should not create session when refresh token generation fails")
        void shouldNotCreateSessionWhenRefreshTokenGenerationFails() {

            when(user.isEnabled()).thenReturn(true);
            when(user.isAccountLocked()).thenReturn(false);
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
                    () -> authenticationTokenService
                            .createAuthenticationSession(
                                    user,
                                    clientInfo
                            )
            );

            verify(jwtService)
                    .generateAccessToken(USERNAME);

            verify(jwtService)
                    .generateRefreshToken(USERNAME);

            verify(refreshTokenSessionServiceImpl, never())
                    .createSession(
                            anyString(),
                            any(User.class),
                            any(ClientInfo.class)
                    );
        }


        @Test
        @DisplayName("should propagate session creation exception")
        void shouldPropagateSessionCreationException() {

            when(user.isEnabled()).thenReturn(true);
            when(user.isAccountLocked()).thenReturn(false);
            when(user.getUsername()).thenReturn(USERNAME);

            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(ACCESS_TOKEN);

            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(REFRESH_TOKEN);

            RuntimeException exception =
                    new RuntimeException(
                            "Session creation failed"
                    );

            doThrow(exception)
                    .when(refreshTokenSessionServiceImpl)
                    .createSession(
                            REFRESH_TOKEN,
                            user,
                            clientInfo
                    );

            assertThrows(
                    RuntimeException.class,
                    () -> authenticationTokenService
                            .createAuthenticationSession(
                                    user,
                                    clientInfo
                            )
            );

            verify(refreshTokenSessionServiceImpl)
                    .createSession(
                            REFRESH_TOKEN,
                            user,
                            clientInfo
                    );
        }
    }


    // ============================================================
    // REFRESH AUTHENTICATION SESSION
    // ============================================================

    @Nested
    @DisplayName("refreshAuthenticationSession")
    class RefreshAuthenticationSessionTests {

        @Test
        @DisplayName("should refresh authentication session successfully")
        void shouldRefreshAuthenticationSessionSuccessfully() {

            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);

            when(refreshTokenSessionServiceImpl
                    .findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);

            when(currentSession.getUser())
                    .thenReturn(user);

            when(user.isEnabled())
                    .thenReturn(true);

            when(user.isAccountLocked())
                    .thenReturn(false);

            when(user.getUsername())
                    .thenReturn(USERNAME);

            when(user.getPassword())
                    .thenReturn(PASSWORD_HASH);

            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(NEW_ACCESS_TOKEN);

            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(NEW_REFRESH_TOKEN);

            AuthenticationResult response =
                    authenticationTokenService
                            .refreshAuthenticationSession(
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

            assertEquals(
                    "Bearer",
                    response.getTokenType()
            );

            assertTrue(response.isHasPassword());

            verify(refreshTokenRequest)
                    .getRefreshToken();

            verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);

            verify(refreshTokenSessionServiceImpl)
                    .findValidSession(REFRESH_TOKEN);

            verify(jwtService)
                    .generateAccessToken(USERNAME);

            verify(jwtService)
                    .generateRefreshToken(USERNAME);

            verify(refreshTokenSessionServiceImpl)
                    .rotateSession(
                            currentSession,
                            NEW_REFRESH_TOKEN
                    );
        }


        @Test
        @DisplayName("should return hasPassword false for OAuth user during refresh")
        void shouldReturnHasPasswordFalseForOAuthUserDuringRefresh() {

            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);

            when(refreshTokenSessionServiceImpl
                    .findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);

            when(currentSession.getUser())
                    .thenReturn(user);

            when(user.isEnabled())
                    .thenReturn(true);

            when(user.isAccountLocked())
                    .thenReturn(false);

            when(user.getUsername())
                    .thenReturn(USERNAME);

            when(user.getPassword())
                    .thenReturn(null);

            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(NEW_ACCESS_TOKEN);

            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(NEW_REFRESH_TOKEN);

            AuthenticationResult response =
                    authenticationTokenService
                            .refreshAuthenticationSession(
                                    refreshTokenRequest
                            );

            assertFalse(response.isHasPassword());

            verify(refreshTokenSessionServiceImpl)
                    .rotateSession(
                            currentSession,
                            NEW_REFRESH_TOKEN
                    );
        }


        @Test
        @DisplayName("should validate refresh token before finding session")
        void shouldValidateRefreshTokenBeforeFindingSession() {

            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);

            when(refreshTokenSessionServiceImpl
                    .findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);

            when(currentSession.getUser())
                    .thenReturn(user);

            when(user.isEnabled())
                    .thenReturn(true);

            when(user.isAccountLocked())
                    .thenReturn(false);

            when(user.getUsername())
                    .thenReturn(USERNAME);

            when(user.getPassword())
                    .thenReturn(PASSWORD_HASH);

            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(NEW_ACCESS_TOKEN);

            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(NEW_REFRESH_TOKEN);

            authenticationTokenService
                    .refreshAuthenticationSession(
                            refreshTokenRequest
                    );

            InOrder inOrder =
                    inOrder(
                            jwtService,
                            refreshTokenSessionServiceImpl
                    );

            inOrder.verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);

            inOrder.verify(refreshTokenSessionServiceImpl)
                    .findValidSession(REFRESH_TOKEN);

            inOrder.verify(jwtService)
                    .generateAccessToken(USERNAME);

            inOrder.verify(jwtService)
                    .generateRefreshToken(USERNAME);

            inOrder.verify(refreshTokenSessionServiceImpl)
                    .rotateSession(
                            currentSession,
                            NEW_REFRESH_TOKEN
                    );
        }


        @Test
        @DisplayName("should validate user after finding refresh session")
        void shouldValidateUserAfterFindingRefreshSession() {

            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);

            when(refreshTokenSessionServiceImpl
                    .findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);

            when(currentSession.getUser())
                    .thenReturn(user);

            when(user.isEnabled())
                    .thenReturn(true);

            when(user.isAccountLocked())
                    .thenReturn(false);

            when(user.getUsername())
                    .thenReturn(USERNAME);

            when(user.getPassword())
                    .thenReturn(PASSWORD_HASH);

            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(NEW_ACCESS_TOKEN);

            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(NEW_REFRESH_TOKEN);

            authenticationTokenService
                    .refreshAuthenticationSession(
                            refreshTokenRequest
                    );

            InOrder inOrder =
                    inOrder(
                            refreshTokenSessionServiceImpl,
                            jwtService
                    );

            inOrder.verify(refreshTokenSessionServiceImpl)
                    .findValidSession(REFRESH_TOKEN);

            inOrder.verify(jwtService)
                    .generateAccessToken(USERNAME);

            inOrder.verify(jwtService)
                    .generateRefreshToken(USERNAME);

            inOrder.verify(refreshTokenSessionServiceImpl)
                    .rotateSession(
                            currentSession,
                            NEW_REFRESH_TOKEN
                    );
        }


        @Test
        @DisplayName("should not find session when refresh token validation fails")
        void shouldNotFindSessionWhenRefreshTokenValidationFails() {

            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);

            doThrow(
                    new IllegalArgumentException(
                            "Invalid refresh token"
                    )
            )
                    .when(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);

            assertThrows(
                    IllegalArgumentException.class,
                    () -> authenticationTokenService
                            .refreshAuthenticationSession(
                                    refreshTokenRequest
                            )
            );

            verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);

            verify(refreshTokenSessionServiceImpl, never())
                    .findValidSession(anyString());

            verify(jwtService, never())
                    .generateAccessToken(anyString());

            verify(jwtService, never())
                    .generateRefreshToken(anyString());

            verify(refreshTokenSessionServiceImpl, never())
                    .rotateSession(
                            any(RefreshTokenSession.class),
                            anyString()
                    );
        }


        @Test
        @DisplayName("should not generate new tokens when session lookup fails")
        void shouldNotGenerateNewTokensWhenSessionLookupFails() {

            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);

            when(refreshTokenSessionServiceImpl
                    .findValidSession(REFRESH_TOKEN))
                    .thenThrow(
                            new RuntimeException(
                                    "Session not found"
                            )
                    );

            assertThrows(
                    RuntimeException.class,
                    () -> authenticationTokenService
                            .refreshAuthenticationSession(
                                    refreshTokenRequest
                            )
            );

            verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);

            verify(refreshTokenSessionServiceImpl)
                    .findValidSession(REFRESH_TOKEN);

            verify(jwtService, never())
                    .generateAccessToken(anyString());

            verify(jwtService, never())
                    .generateRefreshToken(anyString());

            verify(refreshTokenSessionServiceImpl, never())
                    .rotateSession(
                            any(RefreshTokenSession.class),
                            anyString()
                    );
        }


        @Test
        @DisplayName("should reject refresh when session user is null")
        void shouldRejectRefreshWhenSessionUserIsNull() {

            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);

            when(refreshTokenSessionServiceImpl
                    .findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);

            when(currentSession.getUser())
                    .thenReturn(null);

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> authenticationTokenService
                                    .refreshAuthenticationSession(
                                            refreshTokenRequest
                                    )
                    );

            assertEquals(
                    PERMANENT_ACCOUNT_LOCKED,
                    exception.getErrorCode()
            );

            verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);

            verify(refreshTokenSessionServiceImpl)
                    .findValidSession(REFRESH_TOKEN);

            verify(jwtService, never())
                    .generateAccessToken(anyString());

            verify(jwtService, never())
                    .generateRefreshToken(anyString());

            verify(refreshTokenSessionServiceImpl, never())
                    .rotateSession(
                            any(RefreshTokenSession.class),
                            anyString()
                    );
        }


        @Test
        @DisplayName("should reject refresh when user is disabled")
        void shouldRejectRefreshWhenUserIsDisabled() {

            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);

            when(refreshTokenSessionServiceImpl
                    .findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);

            when(currentSession.getUser())
                    .thenReturn(user);

            when(user.isEnabled())
                    .thenReturn(false);

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> authenticationTokenService
                                    .refreshAuthenticationSession(
                                            refreshTokenRequest
                                    )
                    );

            assertEquals(
                    ERR_USER_DISABLED,
                    exception.getErrorCode()
            );

            verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);

            verify(refreshTokenSessionServiceImpl)
                    .findValidSession(REFRESH_TOKEN);

            verify(jwtService, never())
                    .generateAccessToken(anyString());

            verify(jwtService, never())
                    .generateRefreshToken(anyString());

            verify(refreshTokenSessionServiceImpl, never())
                    .rotateSession(
                            any(RefreshTokenSession.class),
                            anyString()
                    );
        }


        @Test
        @DisplayName("should reject refresh when user is permanently locked")
        void shouldRejectRefreshWhenUserIsPermanentlyLocked() {

            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);

            when(refreshTokenSessionServiceImpl
                    .findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);

            when(currentSession.getUser())
                    .thenReturn(user);

            when(user.isEnabled())
                    .thenReturn(true);

            when(user.isAccountLocked())
                    .thenReturn(true);

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> authenticationTokenService
                                    .refreshAuthenticationSession(
                                            refreshTokenRequest
                                    )
                    );

            assertEquals(
                    PERMANENT_ACCOUNT_LOCKED,
                    exception.getErrorCode()
            );

            verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);

            verify(refreshTokenSessionServiceImpl)
                    .findValidSession(REFRESH_TOKEN);

            verify(jwtService, never())
                    .generateAccessToken(anyString());

            verify(jwtService, never())
                    .generateRefreshToken(anyString());

            verify(refreshTokenSessionServiceImpl, never())
                    .rotateSession(
                            any(RefreshTokenSession.class),
                            anyString()
                    );
        }


        @Test
        @DisplayName("should not generate refresh token when access token generation fails")
        void shouldNotRotateSessionWhenAccessTokenGenerationFails() {

            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);

            when(refreshTokenSessionServiceImpl
                    .findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);

            when(currentSession.getUser())
                    .thenReturn(user);

            when(user.isEnabled())
                    .thenReturn(true);

            when(user.isAccountLocked())
                    .thenReturn(false);

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
                    () -> authenticationTokenService
                            .refreshAuthenticationSession(
                                    refreshTokenRequest
                            )
            );

            verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);

            verify(refreshTokenSessionServiceImpl)
                    .findValidSession(REFRESH_TOKEN);

            verify(jwtService)
                    .generateAccessToken(USERNAME);

            verify(jwtService, never())
                    .generateRefreshToken(anyString());

            verify(refreshTokenSessionServiceImpl, never())
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

            when(refreshTokenSessionServiceImpl
                    .findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);

            when(currentSession.getUser())
                    .thenReturn(user);

            when(user.isEnabled())
                    .thenReturn(true);

            when(user.isAccountLocked())
                    .thenReturn(false);

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
                    () -> authenticationTokenService
                            .refreshAuthenticationSession(
                                    refreshTokenRequest
                            )
            );

            verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);

            verify(refreshTokenSessionServiceImpl)
                    .findValidSession(REFRESH_TOKEN);

            verify(jwtService)
                    .generateAccessToken(USERNAME);

            verify(jwtService)
                    .generateRefreshToken(USERNAME);

            verify(refreshTokenSessionServiceImpl, never())
                    .rotateSession(
                            any(RefreshTokenSession.class),
                            anyString()
                    );
        }


        @Test
        @DisplayName("should rotate session after generating new tokens")
        void shouldRotateSessionAfterGeneratingNewTokens() {

            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);

            when(refreshTokenSessionServiceImpl
                    .findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);

            when(currentSession.getUser())
                    .thenReturn(user);

            when(user.isEnabled())
                    .thenReturn(true);

            when(user.isAccountLocked())
                    .thenReturn(false);

            when(user.getUsername())
                    .thenReturn(USERNAME);

            when(user.getPassword())
                    .thenReturn(PASSWORD_HASH);

            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(NEW_ACCESS_TOKEN);

            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(NEW_REFRESH_TOKEN);

            authenticationTokenService
                    .refreshAuthenticationSession(
                            refreshTokenRequest
                    );

            InOrder inOrder =
                    inOrder(
                            jwtService,
                            refreshTokenSessionServiceImpl
                    );

            inOrder.verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);

            inOrder.verify(refreshTokenSessionServiceImpl)
                    .findValidSession(REFRESH_TOKEN);

            inOrder.verify(jwtService)
                    .generateAccessToken(USERNAME);

            inOrder.verify(jwtService)
                    .generateRefreshToken(USERNAME);

            inOrder.verify(refreshTokenSessionServiceImpl)
                    .rotateSession(
                            currentSession,
                            NEW_REFRESH_TOKEN
                    );
        }


        @Test
        @DisplayName("should propagate session rotation exception")
        void shouldPropagateSessionRotationException() {

            when(refreshTokenRequest.getRefreshToken())
                    .thenReturn(REFRESH_TOKEN);

            when(refreshTokenSessionServiceImpl.findValidSession(REFRESH_TOKEN))
                    .thenReturn(currentSession);

            when(currentSession.getUser())
                    .thenReturn(user);

            when(user.isEnabled())
                    .thenReturn(true);

            when(user.isAccountLocked())
                    .thenReturn(false);

            when(user.getUsername())
                    .thenReturn(USERNAME);

            when(jwtService.generateAccessToken(USERNAME))
                    .thenReturn(NEW_ACCESS_TOKEN);

            when(jwtService.generateRefreshToken(USERNAME))
                    .thenReturn(NEW_REFRESH_TOKEN);

            RuntimeException exception =
                    new RuntimeException("Session rotation failed");

            doThrow(exception)
                    .when(refreshTokenSessionServiceImpl)
                    .rotateSession(
                            currentSession,
                            NEW_REFRESH_TOKEN
                    );

            RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> authenticationTokenService.refreshAuthenticationSession(
                            refreshTokenRequest
                    )
            );

            assertEquals(
                    "Session rotation failed",
                    thrown.getMessage()
            );

            verify(refreshTokenRequest)
                    .getRefreshToken();

            verify(jwtService)
                    .validateRefreshToken(REFRESH_TOKEN);

            verify(refreshTokenSessionServiceImpl)
                    .findValidSession(REFRESH_TOKEN);

            verify(currentSession)
                    .getUser();

            verify(user)
                    .isEnabled();

            verify(user)
                    .isAccountLocked();

            verify(user)
                    .getUsername();

            verify(jwtService)
                    .generateAccessToken(USERNAME);

            verify(jwtService)
                    .generateRefreshToken(USERNAME);

            verify(refreshTokenSessionServiceImpl)
                    .rotateSession(
                            currentSession,
                            NEW_REFRESH_TOKEN
                    );
        }
    }
}