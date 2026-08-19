package com.agri.market.auth.service;

import com.agri.market.auth.dto.*;
import com.agri.market.exception.BusinessException;
import com.agri.market.role.entity.Role;
import com.agri.market.role.repository.RoleRepository;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.agri.market.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationServiceImpl")
class AuthenticationServiceImplTest {

    private static final String EMAIL = "user@example.com";
    private static final String RAW_EMAIL = "  User@Example.COM  ";
    private static final String PHONE = "9876543210";
    private static final String FULL_NAME = "Test User";
    private static final String PASSWORD = "Password@123";
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String USER_ID = "user-id";
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RefreshTokenSessionService refreshTokenSessionService;
    @Mock
    private EmailVerificationService emailVerificationService;
    @Mock
    private AuthenticationTokenService authenticationTokenService;
    @Mock
    private RegistrationRequest registrationRequest;
    @Mock
    private AuthenticationRequest authenticationRequest;
    @Mock
    private RefreshTokenRequest refreshTokenRequest;
    @Mock
    private ClientInfo clientInfo;
    @Mock
    private Role userRole;
    @Mock
    private User savedUser;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Nested
    @DisplayName("register")
    class RegisterTests {

        @Test
        @DisplayName("should register user and send verification email")
        void shouldRegisterUserAndSendVerificationEmail() {
            when(registrationRequest.getEmail())
                    .thenReturn(EMAIL);
            when(registrationRequest.getPhoneNumber())
                    .thenReturn(PHONE);
            when(registrationRequest.getPassword())
                    .thenReturn(PASSWORD);
            when(registrationRequest.getConfirmPassword())
                    .thenReturn(PASSWORD);
            when(registrationRequest.getFullName())
                    .thenReturn(FULL_NAME);

            when(userRepository.existsByEmailIgnoreCase(EMAIL))
                    .thenReturn(false);
            when(userRepository.existsByPhoneNumberIgnoreCase(PHONE))
                    .thenReturn(false);

            when(roleRepository.findByName("USER"))
                    .thenReturn(Optional.of(userRole));

            when(passwordEncoder.encode(PASSWORD))
                    .thenReturn(ENCODED_PASSWORD);

            when(userRepository.save(any(User.class)))
                    .thenReturn(savedUser);

            RegistrationResponse response =
                    authenticationService.register(
                            registrationRequest,
                            clientInfo
                    );

            assertEquals(
                    "Registration successful. Please verify your email address before logging in.",
                    response.getMessage()
            );

            ArgumentCaptor<User> userCaptor =
                    ArgumentCaptor.forClass(User.class);

            verify(userRepository)
                    .save(userCaptor.capture());

            User user = userCaptor.getValue();

            assertEquals(FULL_NAME, user.getFullName());
            assertEquals(EMAIL, user.getEmail());
            assertEquals(PHONE, user.getPhoneNumber());
            assertEquals(ENCODED_PASSWORD, user.getPassword());
            assertEquals(List.of(userRole), user.getRoles());
            assertFalse(user.isEmailVerified());
            assertFalse(user.isEnabled());

            verify(passwordEncoder).encode(PASSWORD);
            verify(emailVerificationService)
                    .sendVerificationEmail(savedUser);
        }

        @Test
        @DisplayName("should normalize email before checking and saving user")
        void shouldNormalizeEmailBeforeCheckingAndSavingUser() {
            when(registrationRequest.getEmail())
                    .thenReturn(RAW_EMAIL);
            when(registrationRequest.getPhoneNumber())
                    .thenReturn(PHONE);
            when(registrationRequest.getPassword())
                    .thenReturn(PASSWORD);
            when(registrationRequest.getConfirmPassword())
                    .thenReturn(PASSWORD);
            when(registrationRequest.getFullName())
                    .thenReturn(FULL_NAME);

            when(userRepository.existsByEmailIgnoreCase(EMAIL))
                    .thenReturn(false);
            when(userRepository.existsByPhoneNumberIgnoreCase(PHONE))
                    .thenReturn(false);
            when(roleRepository.findByName("USER"))
                    .thenReturn(Optional.of(userRole));
            when(passwordEncoder.encode(PASSWORD))
                    .thenReturn(ENCODED_PASSWORD);
            when(userRepository.save(any(User.class)))
                    .thenReturn(savedUser);

            authenticationService.register(
                    registrationRequest,
                    clientInfo
            );

            ArgumentCaptor<User> userCaptor =
                    ArgumentCaptor.forClass(User.class);

            verify(userRepository)
                    .save(userCaptor.capture());

            assertEquals(
                    EMAIL,
                    userCaptor.getValue().getEmail()
            );

            verify(userRepository)
                    .existsByEmailIgnoreCase(EMAIL);
        }

        @Test
        @DisplayName("should reject registration when email already exists")
        void shouldRejectRegistrationWhenEmailAlreadyExists() {
            when(registrationRequest.getEmail())
                    .thenReturn(RAW_EMAIL);
            when(userRepository.existsByEmailIgnoreCase(EMAIL))
                    .thenReturn(true);

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> authenticationService.register(
                                    registrationRequest,
                                    clientInfo
                            )
                    );

            assertEquals(
                    EMAIL_ALREADY_EXISTS,
                    exception.getErrorCode()
            );

            verify(userRepository)
                    .existsByEmailIgnoreCase(EMAIL);
            verify(userRepository, never())
                    .existsByPhoneNumberIgnoreCase(anyString());
            verify(roleRepository, never())
                    .findByName(anyString());
            verify(passwordEncoder, never())
                    .encode(anyString());
            verify(userRepository, never())
                    .save(any(User.class));
            verify(emailVerificationService, never())
                    .sendVerificationEmail(any(User.class));
        }

        @Test
        @DisplayName("should reject registration when phone number already exists")
        void shouldRejectRegistrationWhenPhoneNumberAlreadyExists() {
            when(registrationRequest.getEmail())
                    .thenReturn(EMAIL);
            when(registrationRequest.getPhoneNumber())
                    .thenReturn(PHONE);

            when(userRepository.existsByEmailIgnoreCase(EMAIL))
                    .thenReturn(false);
            when(userRepository.existsByPhoneNumberIgnoreCase(PHONE))
                    .thenReturn(true);

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> authenticationService.register(
                                    registrationRequest,
                                    clientInfo
                            )
                    );

            assertEquals(
                    PHONE_ALREADY_EXISTS,
                    exception.getErrorCode()
            );

            verify(userRepository)
                    .existsByEmailIgnoreCase(EMAIL);
            verify(userRepository)
                    .existsByPhoneNumberIgnoreCase(PHONE);
            verify(roleRepository, never())
                    .findByName(anyString());
            verify(passwordEncoder, never())
                    .encode(anyString());
            verify(userRepository, never())
                    .save(any(User.class));
            verify(emailVerificationService, never())
                    .sendVerificationEmail(any(User.class));
        }

        @Test
        @DisplayName("should reject registration when passwords do not match")
        void shouldRejectRegistrationWhenPasswordsDoNotMatch() {
            when(registrationRequest.getEmail())
                    .thenReturn(EMAIL);
            when(registrationRequest.getPhoneNumber())
                    .thenReturn(PHONE);
            when(registrationRequest.getPassword())
                    .thenReturn(PASSWORD);
            when(registrationRequest.getConfirmPassword())
                    .thenReturn("DifferentPassword@123");

            when(userRepository.existsByEmailIgnoreCase(EMAIL))
                    .thenReturn(false);
            when(userRepository.existsByPhoneNumberIgnoreCase(PHONE))
                    .thenReturn(false);

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> authenticationService.register(
                                    registrationRequest,
                                    clientInfo
                            )
                    );

            assertEquals(
                    PASSWORD_MISMATCH,
                    exception.getErrorCode()
            );

            verify(userRepository)
                    .existsByEmailIgnoreCase(EMAIL);
            verify(userRepository)
                    .existsByPhoneNumberIgnoreCase(PHONE);
            verify(roleRepository, never())
                    .findByName(anyString());
            verify(passwordEncoder, never())
                    .encode(anyString());
            verify(userRepository, never())
                    .save(any(User.class));
            verify(emailVerificationService, never())
                    .sendVerificationEmail(any(User.class));
        }

        @Test
        @DisplayName("should reject registration when default user role does not exist")
        void shouldRejectRegistrationWhenDefaultUserRoleDoesNotExist() {
            when(registrationRequest.getEmail())
                    .thenReturn(EMAIL);
            when(registrationRequest.getPhoneNumber())
                    .thenReturn(PHONE);
            when(registrationRequest.getPassword())
                    .thenReturn(PASSWORD);
            when(registrationRequest.getConfirmPassword())
                    .thenReturn(PASSWORD);

            when(userRepository.existsByEmailIgnoreCase(EMAIL))
                    .thenReturn(false);
            when(userRepository.existsByPhoneNumberIgnoreCase(PHONE))
                    .thenReturn(false);
            when(roleRepository.findByName("USER"))
                    .thenReturn(Optional.empty());

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> authenticationService.register(
                                    registrationRequest,
                                    clientInfo
                            )
                    );

            assertEquals(
                    ROLE_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(roleRepository)
                    .findByName("USER");
            verify(passwordEncoder, never())
                    .encode(anyString());
            verify(userRepository, never())
                    .save(any(User.class));
            verify(emailVerificationService, never())
                    .sendVerificationEmail(any(User.class));
        }

        @Test
        @DisplayName("should encode password before saving user")
        void shouldEncodePasswordBeforeSavingUser() {
            when(registrationRequest.getEmail())
                    .thenReturn(EMAIL);
            when(registrationRequest.getPhoneNumber())
                    .thenReturn(PHONE);
            when(registrationRequest.getPassword())
                    .thenReturn(PASSWORD);
            when(registrationRequest.getConfirmPassword())
                    .thenReturn(PASSWORD);
            when(registrationRequest.getFullName())
                    .thenReturn(FULL_NAME);

            when(userRepository.existsByEmailIgnoreCase(EMAIL))
                    .thenReturn(false);
            when(userRepository.existsByPhoneNumberIgnoreCase(PHONE))
                    .thenReturn(false);
            when(roleRepository.findByName("USER"))
                    .thenReturn(Optional.of(userRole));
            when(passwordEncoder.encode(PASSWORD))
                    .thenReturn(ENCODED_PASSWORD);
            when(userRepository.save(any(User.class)))
                    .thenReturn(savedUser);

            authenticationService.register(
                    registrationRequest,
                    clientInfo
            );

            verify(passwordEncoder)
                    .encode(PASSWORD);

            ArgumentCaptor<User> userCaptor =
                    ArgumentCaptor.forClass(User.class);

            verify(userRepository)
                    .save(userCaptor.capture());

            assertEquals(
                    ENCODED_PASSWORD,
                    userCaptor.getValue().getPassword()
            );
        }

        @Test
        @DisplayName("should send verification email to saved user")
        void shouldSendVerificationEmailToSavedUser() {
            when(registrationRequest.getEmail())
                    .thenReturn(EMAIL);
            when(registrationRequest.getPhoneNumber())
                    .thenReturn(PHONE);
            when(registrationRequest.getPassword())
                    .thenReturn(PASSWORD);
            when(registrationRequest.getConfirmPassword())
                    .thenReturn(PASSWORD);
            when(registrationRequest.getFullName())
                    .thenReturn(FULL_NAME);

            when(userRepository.existsByEmailIgnoreCase(EMAIL))
                    .thenReturn(false);
            when(userRepository.existsByPhoneNumberIgnoreCase(PHONE))
                    .thenReturn(false);
            when(roleRepository.findByName("USER"))
                    .thenReturn(Optional.of(userRole));
            when(passwordEncoder.encode(PASSWORD))
                    .thenReturn(ENCODED_PASSWORD);
            when(userRepository.save(any(User.class)))
                    .thenReturn(savedUser);

            authenticationService.register(
                    registrationRequest,
                    clientInfo
            );

            verify(emailVerificationService)
                    .sendVerificationEmail(savedUser);
        }
    }

    @Nested
    @DisplayName("login")
    class LoginTests {

        @Test
        @DisplayName("should authenticate user and create authentication session")
        void shouldAuthenticateUserAndCreateAuthenticationSession() {
            when(authenticationRequest.getEmail())
                    .thenReturn(RAW_EMAIL);
            when(authenticationRequest.getPassword())
                    .thenReturn(PASSWORD);
            when(authentication.getPrincipal())
                    .thenReturn(savedUser);

            when(authenticationManager.authenticate(any(
                    UsernamePasswordAuthenticationToken.class
            ))).thenReturn(authentication);

            AuthenticationResponse expectedResponse =
                    mock(AuthenticationResponse.class);

            when(authenticationTokenService.createAuthenticationSession(
                    savedUser,
                    clientInfo
            )).thenReturn(expectedResponse);

            AuthenticationResponse response =
                    authenticationService.login(
                            authenticationRequest,
                            clientInfo
                    );

            assertEquals(expectedResponse, response);

            ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                    ArgumentCaptor.forClass(
                            UsernamePasswordAuthenticationToken.class
                    );

            verify(authenticationManager)
                    .authenticate(captor.capture());

            UsernamePasswordAuthenticationToken token =
                    captor.getValue();

            assertEquals(EMAIL, token.getPrincipal());
            assertEquals(PASSWORD, token.getCredentials());

            verify(authenticationTokenService)
                    .createAuthenticationSession(
                            savedUser,
                            clientInfo
                    );
        }

        @Test
        @DisplayName("should normalize email before authentication")
        void shouldNormalizeEmailBeforeAuthentication() {
            when(authenticationRequest.getEmail())
                    .thenReturn(RAW_EMAIL);
            when(authenticationRequest.getPassword())
                    .thenReturn(PASSWORD);
            when(authentication.getPrincipal())
                    .thenReturn(savedUser);

            when(authenticationManager.authenticate(any(
                    UsernamePasswordAuthenticationToken.class
            ))).thenReturn(authentication);

            when(authenticationTokenService.createAuthenticationSession(
                    savedUser,
                    clientInfo
            )).thenReturn(mock(AuthenticationResponse.class));

            authenticationService.login(
                    authenticationRequest,
                    clientInfo
            );

            ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                    ArgumentCaptor.forClass(
                            UsernamePasswordAuthenticationToken.class
                    );

            verify(authenticationManager)
                    .authenticate(captor.capture());

            assertEquals(
                    EMAIL,
                    captor.getValue().getPrincipal()
            );
        }

        @Test
        @DisplayName("should propagate authentication failure")
        void shouldPropagateAuthenticationFailure() {
            when(authenticationRequest.getEmail())
                    .thenReturn(EMAIL);
            when(authenticationRequest.getPassword())
                    .thenReturn(PASSWORD);

            AuthenticationException exception =
                    mock(AuthenticationException.class);

            when(authenticationManager.authenticate(any(
                    UsernamePasswordAuthenticationToken.class
            ))).thenThrow(exception);

            assertThrows(
                    AuthenticationException.class,
                    () -> authenticationService.login(
                            authenticationRequest,
                            clientInfo
                    )
            );

            verify(authenticationManager)
                    .authenticate(any(
                            UsernamePasswordAuthenticationToken.class
                    ));

            verify(authenticationTokenService, never())
                    .createAuthenticationSession(
                            any(User.class),
                            any(ClientInfo.class)
                    );
        }

        @Test
        @DisplayName("should not create session when authentication fails")
        void shouldNotCreateSessionWhenAuthenticationFails() {
            when(authenticationRequest.getEmail())
                    .thenReturn(EMAIL);
            when(authenticationRequest.getPassword())
                    .thenReturn(PASSWORD);

            when(authenticationManager.authenticate(any(
                    UsernamePasswordAuthenticationToken.class
            ))).thenThrow(
                    mock(AuthenticationException.class)
            );

            assertThrows(
                    AuthenticationException.class,
                    () -> authenticationService.login(
                            authenticationRequest,
                            clientInfo
                    )
            );

            verify(authenticationTokenService, never())
                    .createAuthenticationSession(
                            any(User.class),
                            any(ClientInfo.class)
                    );
        }
    }

    @Nested
    @DisplayName("refreshToken")
    class RefreshTokenTests {

        @Test
        @DisplayName("should delegate refresh token request to authentication token service")
        void shouldDelegateRefreshTokenRequestToAuthenticationTokenService() {
            AuthenticationResponse expectedResponse =
                    mock(AuthenticationResponse.class);

            when(authenticationTokenService
                    .refreshAuthenticationSession(refreshTokenRequest))
                    .thenReturn(expectedResponse);

            AuthenticationResponse response =
                    authenticationService.refreshToken(
                            refreshTokenRequest
                    );

            assertEquals(expectedResponse, response);

            verify(authenticationTokenService)
                    .refreshAuthenticationSession(
                            refreshTokenRequest
                    );
        }

        @Test
        @DisplayName("should propagate refresh session failure")
        void shouldPropagateRefreshSessionFailure() {
            RuntimeException exception =
                    new RuntimeException("Refresh failed");

            when(authenticationTokenService
                    .refreshAuthenticationSession(refreshTokenRequest))
                    .thenThrow(exception);

            assertThrows(
                    RuntimeException.class,
                    () -> authenticationService.refreshToken(
                            refreshTokenRequest
                    )
            );

            verify(authenticationTokenService)
                    .refreshAuthenticationSession(
                            refreshTokenRequest
                    );
        }
    }

    @Nested
    @DisplayName("logout")
    class LogoutTests {

        @Test
        @DisplayName("should revoke requested session")
        void shouldRevokeRequestedSession() {
            authenticationService.logout(REFRESH_TOKEN);

            verify(refreshTokenSessionService)
                    .revokeSession(REFRESH_TOKEN);
        }

        @Test
        @DisplayName("should propagate session revocation failure")
        void shouldPropagateSessionRevocationFailure() {
            RuntimeException exception =
                    new RuntimeException("Logout failed");

            doThrow(exception)
                    .when(refreshTokenSessionService)
                    .revokeSession(REFRESH_TOKEN);

            assertThrows(
                    RuntimeException.class,
                    () -> authenticationService.logout(
                            REFRESH_TOKEN
                    )
            );

            verify(refreshTokenSessionService)
                    .revokeSession(REFRESH_TOKEN);
        }
    }

    @Nested
    @DisplayName("logoutAll")
    class LogoutAllTests {

        @Test
        @DisplayName("should revoke all sessions for user")
        void shouldRevokeAllSessionsForUser() {
            authenticationService.logoutAll(USER_ID);

            verify(refreshTokenSessionService)
                    .revokeAllSessions(USER_ID);
        }

        @Test
        @DisplayName("should propagate logout all sessions failure")
        void shouldPropagateLogoutAllSessionsFailure() {
            RuntimeException exception =
                    new RuntimeException("Logout all failed");

            doThrow(exception)
                    .when(refreshTokenSessionService)
                    .revokeAllSessions(USER_ID);

            assertThrows(
                    RuntimeException.class,
                    () -> authenticationService.logoutAll(
                            USER_ID
                    )
            );

            verify(refreshTokenSessionService)
                    .revokeAllSessions(USER_ID);
        }
    }
}