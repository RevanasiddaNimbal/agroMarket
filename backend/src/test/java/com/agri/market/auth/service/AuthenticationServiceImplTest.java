package com.agri.market.auth.service;

import com.agri.market.auth.dto.*;
import com.agri.market.auth.entity.RefreshTokenSession;
import com.agri.market.exception.BusinessException;
import com.agri.market.role.entity.Role;
import com.agri.market.role.repository.RoleRepository;
import com.agri.market.security.jwt.JwtService;
import com.agri.market.support.*;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.agri.market.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationServiceImpl")
class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
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

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private ClientInfo clientInfo;
    private Role userRole;
    private User savedUser;

    @BeforeEach
    void setUp() {
        clientInfo = ClientInfoTestFactory.deviceA();
        userRole = RoleTestFactory.userRole();
        savedUser = UserTestFactory.activeUser();
        savedUser.setId("user-id");
    }

    @Nested
    @DisplayName("register")
    class RegisterTests {

        @Test
        void shouldRegisterUserAndSendVerificationEmail() {

            RegistrationRequest request =
                    RegistrationRequestTestFactory.validRequest();

            when(roleRepository.findByName("USER"))
                    .thenReturn(Optional.of(userRole));

            when(userRepository.existsByEmailIgnoreCase(
                    "revanasidda@gmail.com"
            )).thenReturn(false);

            when(userRepository.existsByPhoneNumberIgnoreCase(
                    "+919876543210"
            )).thenReturn(false);

            when(passwordEncoder.encode("P@ssword123"))
                    .thenReturn("encoded-password");

            when(userRepository.save(any(User.class)))
                    .thenAnswer(invocation -> {
                        User user = invocation.getArgument(0);
                        user.setId("user-id");
                        return user;
                    });

            RegistrationResponse response =
                    authenticationService.register(
                            request,
                            clientInfo
                    );

            ArgumentCaptor<User> userCaptor =
                    ArgumentCaptor.forClass(User.class);

            verify(userRepository).save(userCaptor.capture());

            User saved = userCaptor.getValue();

            assertThat(saved.getEmail())
                    .isEqualTo("revanasidda@gmail.com");

            assertThat(saved.getFullName())
                    .isEqualTo(request.getFullName());

            assertThat(saved.getPhoneNumber())
                    .isEqualTo("+919876543210");

            assertThat(saved.isEnabled())
                    .isFalse();

            assertThat(saved.isEmailVerified())
                    .isFalse();

            assertThat(saved.getPassword())
                    .isEqualTo("encoded-password");

            assertThat(saved.getRoles())
                    .containsExactly(userRole);

            verify(emailVerificationService)
                    .sendVerificationEmail(saved);

            assertThat(response.getMessage())
                    .isEqualTo(
                            "Registration successful. " +
                                    "Please verify your email address before logging in."
                    );
        }
        @Test
        void shouldRejectRegistrationWhenEmailAlreadyExists() {
            RegistrationRequest request = RegistrationRequestTestFactory.validRequest();
            when(userRepository.existsByEmailIgnoreCase("revanasidda@gmail.com")).thenReturn(true);

            Assertions.assertThatThrownBy(() -> authenticationService.register(request, clientInfo))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException businessException = (BusinessException) ex;
                        assertThat(businessException.getErrorCode()).isEqualTo(EMAIL_ALREADY_EXISTS);
                    });

            verify(userRepository, never()).save(any());
            verifyNoInteractions(refreshTokenSessionService, passwordEncoder, roleRepository);
        }

        @Test
        void shouldRejectRegistrationWhenPhoneAlreadyExists() {
            RegistrationRequest request = RegistrationRequestTestFactory.validRequest();
            when(userRepository.existsByEmailIgnoreCase("revanasidda@gmail.com")).thenReturn(false);
            when(userRepository.existsByPhoneNumberIgnoreCase("+919876543210")).thenReturn(true);

            assertThatThrownBy(() -> authenticationService.register(request, clientInfo))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(PHONE_ALREADY_EXISTS));

            verify(userRepository, never()).save(any());
            verifyNoInteractions(refreshTokenSessionService, passwordEncoder);
            verify(roleRepository, never()).findByName(any());
        }

        @Test
        void shouldRejectRegistrationWhenPasswordsDoNotMatch() {
            RegistrationRequest request = RegistrationRequestTestFactory.validRequest();
            request.setConfirmPassword("Different1!");
            when(userRepository.existsByEmailIgnoreCase("revanasidda@gmail.com")).thenReturn(false);
            when(userRepository.existsByPhoneNumberIgnoreCase("+919876543210")).thenReturn(false);

            assertThatThrownBy(() -> authenticationService.register(request, clientInfo))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(PASSWORD_MISMATCH));

            verify(userRepository, never()).save(any());
            verifyNoInteractions(refreshTokenSessionService, passwordEncoder, roleRepository);
        }

        @Test
        void shouldRejectRegistrationWhenDefaultRoleIsMissing() {
            RegistrationRequest request = RegistrationRequestTestFactory.validRequest();
            when(userRepository.existsByEmailIgnoreCase("revanasidda@gmail.com")).thenReturn(false);
            when(userRepository.existsByPhoneNumberIgnoreCase("+919876543210")).thenReturn(false);
            when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authenticationService.register(request, clientInfo))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(ROLE_NOT_FOUND));

            verify(userRepository, never()).save(any());
            verifyNoInteractions(refreshTokenSessionService, passwordEncoder);
        }

        @Test
        void shouldPropagateRepositoryFailureDuringRegistrationAndNotCreateSession() {
            RegistrationRequest request = RegistrationRequestTestFactory.validRequest();
            when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
            when(userRepository.existsByEmailIgnoreCase("revanasidda@gmail.com")).thenReturn(false);
            when(userRepository.existsByPhoneNumberIgnoreCase("+919876543210")).thenReturn(false);
            when(passwordEncoder.encode("P@ssword123")).thenReturn("encoded-password");
            when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("db down"));

            assertThatThrownBy(() -> authenticationService.register(request, clientInfo))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("db down");

            verifyNoInteractions(refreshTokenSessionService);
        }
    }

    @Nested
    @DisplayName("login")
    class LoginTests {

        @Test
        void shouldLoginUserAndCreateAuthenticationSession() {
            AuthenticationRequest request = AuthenticationRequestTestFactory.validRequest();
            User principal = UserTestFactory.activeUser();
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(jwtService.generateAccessToken(principal.getUsername())).thenReturn("access-token");
            when(jwtService.generateRefreshToken(principal.getUsername())).thenReturn("refresh-token");

            AuthenticationResponse response = authenticationService.login(request, clientInfo);

            ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor =
                    ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
            verify(authenticationManager).authenticate(tokenCaptor.capture());
            assertThat(tokenCaptor.getValue().getPrincipal()).isEqualTo("revanasidda@gmail.com");
            assertThat(tokenCaptor.getValue().getCredentials()).isEqualTo("P@ssword123");
            verify(refreshTokenSessionService).createSession("refresh-token", principal, clientInfo);
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            assertThat(response.getAccessToken()).isEqualTo("access-token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        }

        @Test
        void shouldPropagateAuthenticationFailureDuringLogin() {
            AuthenticationRequest request = AuthenticationRequestTestFactory.validRequest();
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("bad credentials"));

            assertThatThrownBy(() -> authenticationService.login(request, clientInfo))
                    .isInstanceOf(BadCredentialsException.class);

            verifyNoInteractions(refreshTokenSessionService, jwtService);
        }
    }

    @Nested
    @DisplayName("refreshToken")
    class RefreshTokenTests {

        @Test
        void shouldRotateSessionAndReturnNewTokenPair() {
            User user = UserTestFactory.activeUser();
            RefreshTokenSession session = RefreshTokenSessionTestFactory.activeSession(
                    user,
                    "old-refresh-hash",
                    "Device A"
            );
            RefreshTokenRequest request = RefreshTokenRequestTestFactory.validRequest();

            when(refreshTokenSessionService.findValidSession("refresh-token")).thenReturn(session);
            doNothing().when(jwtService).validateRefreshToken("refresh-token");
            when(jwtService.generateAccessToken(user.getUsername())).thenReturn("new-access");
            when(jwtService.generateRefreshToken(user.getUsername())).thenReturn("new-refresh");

            AuthenticationResponse response = authenticationService.refreshToken(request);

            verify(refreshTokenSessionService).rotateSession(session, "new-refresh");
            assertThat(response.getAccessToken()).isEqualTo("new-access");
            assertThat(response.getRefreshToken()).isEqualTo("new-refresh");
        }

        @Test
        void shouldPropagateJwtValidationFailureDuringRefresh() {
            User user = UserTestFactory.activeUser();
            RefreshTokenSession session = RefreshTokenSessionTestFactory.activeSession(
                    user,
                    "old-refresh-hash",
                    "Device A"
            );
            RefreshTokenRequest request = RefreshTokenRequestTestFactory.validRequest();

            when(refreshTokenSessionService.findValidSession("refresh-token")).thenReturn(session);
            doThrow(new io.jsonwebtoken.JwtException("invalid refresh token"))
                    .when(jwtService).validateRefreshToken("refresh-token");

            assertThatThrownBy(() -> authenticationService.refreshToken(request))
                    .isInstanceOf(io.jsonwebtoken.JwtException.class)
                    .hasMessageContaining("invalid refresh token");

            verify(refreshTokenSessionService, never()).rotateSession(any(), any());
        }

        @Test
        void shouldRejectUnknownRefreshToken() {
            RefreshTokenRequest request = RefreshTokenRequestTestFactory.validRequest();
            when(refreshTokenSessionService.findValidSession("refresh-token"))
                    .thenThrow(new BusinessException(INVALID_REFRESH_TOKEN));

            assertThatThrownBy(() -> authenticationService.refreshToken(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(INVALID_REFRESH_TOKEN));

            verifyNoInteractions(jwtService);
            verify(refreshTokenSessionService, never()).rotateSession(any(), any());
        }
    }

    @Nested
    @DisplayName("logout")
    class LogoutTests {

        @Test
        void shouldRevokeCurrentSession() {
            authenticationService.logout("refresh-token");

            verify(refreshTokenSessionService).revokeSession("refresh-token");
        }
    }

    @Nested
    @DisplayName("logoutAll")
    class LogoutAllTests {

        @Test
        void shouldRevokeAllSessionsForUser() {
            authenticationService.logoutAll("user-id");

            verify(refreshTokenSessionService).revokeAllSessions("user-id");
        }
    }
}
