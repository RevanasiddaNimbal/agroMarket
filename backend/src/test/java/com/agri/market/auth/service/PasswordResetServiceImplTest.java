package com.agri.market.auth.service;

import com.agri.market.auth.dto.ForgotPasswordRequest;
import com.agri.market.auth.dto.ResetPasswordRequest;
import com.agri.market.auth.entity.PasswordResetToken;
import com.agri.market.auth.repository.PasswordResetTokenRepository;
import com.agri.market.email.config.EmailProperties;
import com.agri.market.email.service.EmailService;
import com.agri.market.exception.BusinessException;
import com.agri.market.security.jwt.TokenHasher;
import com.agri.market.security.token.TokenGenerator;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.agri.market.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordResetServiceImpl")
class PasswordResetServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private RefreshTokenSessionService refreshTokenSessionServiceImpl;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenHasher tokenHasher;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailProperties emailProperties;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();

        user.setId("user-id");
        user.setEmail("user@example.com");
        user.setPassword("old-password");
    }

    @Nested
    @DisplayName("forgotPassword")
    class ForgotPasswordTests {

        @Test
        void shouldCreatePasswordResetTokenAndSendEmail() {

            ForgotPasswordRequest request =
                    new ForgotPasswordRequest(
                            "  user@example.com  "
                    );

            when(userRepository.findByEmailIgnoreCase(
                    "user@example.com"
            )).thenReturn(Optional.of(user));

            when(tokenGenerator.generate())
                    .thenReturn("raw-token");

            when(tokenHasher.hash("raw-token"))
                    .thenReturn("hashed-token");

            when(emailProperties
                    .getPasswordResetTokenExpirationMinutes())
                    .thenReturn(30);

            when(emailProperties.getFrontendUrl())
                    .thenReturn("http://localhost:3000");

            passwordResetService
                    .forgotPassword(request);

            verify(passwordResetTokenRepository)
                    .deleteAllByUser(user);

            verify(tokenGenerator)
                    .generate();

            verify(tokenHasher)
                    .hash("raw-token");

            verify(passwordResetTokenRepository)
                    .save(any(PasswordResetToken.class));

            verify(emailService)
                    .sendPasswordResetEmail(
                            eq("user@example.com"),
                            contains(
                                    "/auth/reset-password?token=raw-token"
                            )
                    );
        }

        @Test
        void shouldStoreHashedToken() {

            ForgotPasswordRequest request =
                    new ForgotPasswordRequest(
                            "user@example.com"
                    );

            when(userRepository.findByEmailIgnoreCase(
                    "user@example.com"
            )).thenReturn(Optional.of(user));

            when(tokenGenerator.generate())
                    .thenReturn("raw-token");

            when(tokenHasher.hash("raw-token"))
                    .thenReturn("hashed-token");

            when(emailProperties
                    .getPasswordResetTokenExpirationMinutes())
                    .thenReturn(30);

            ArgumentCaptor<PasswordResetToken> captor =
                    ArgumentCaptor.forClass(
                            PasswordResetToken.class
                    );

            passwordResetService
                    .forgotPassword(request);

            verify(passwordResetTokenRepository)
                    .save(captor.capture());

            PasswordResetToken token =
                    captor.getValue();

            assertEquals(
                    "hashed-token",
                    token.getTokenHash()
            );

            assertEquals(user, token.getUser());
            assertFalse(token.isUsed());
            assertNotNull(token.getExpiresAt());
        }

        @Test
        void shouldUseConfiguredExpirationTime() {

            ForgotPasswordRequest request =
                    new ForgotPasswordRequest(
                            "user@example.com"
                    );

            when(userRepository.findByEmailIgnoreCase(
                    "user@example.com"
            )).thenReturn(Optional.of(user));

            when(tokenGenerator.generate())
                    .thenReturn("raw-token");

            when(tokenHasher.hash("raw-token"))
                    .thenReturn("hashed-token");

            when(emailProperties
                    .getPasswordResetTokenExpirationMinutes())
                    .thenReturn(30);

            ArgumentCaptor<PasswordResetToken> captor =
                    ArgumentCaptor.forClass(
                            PasswordResetToken.class
                    );

            LocalDateTime before =
                    LocalDateTime.now().plusMinutes(30);

            passwordResetService
                    .forgotPassword(request);

            LocalDateTime after =
                    LocalDateTime.now().plusMinutes(30);

            verify(passwordResetTokenRepository)
                    .save(captor.capture());

            LocalDateTime expiresAt =
                    captor.getValue().getExpiresAt();

            assertFalse(expiresAt.isBefore(before));
            assertFalse(expiresAt.isAfter(after));
        }

        @Test
        void shouldDoNothingForUnknownEmail() {

            ForgotPasswordRequest request =
                    new ForgotPasswordRequest(
                            "unknown@example.com"
                    );

            when(userRepository.findByEmailIgnoreCase(
                    "unknown@example.com"
            )).thenReturn(Optional.empty());

            passwordResetService
                    .forgotPassword(request);

            verify(userRepository)
                    .findByEmailIgnoreCase(
                            "unknown@example.com"
                    );

            verifyNoInteractions(
                    passwordResetTokenRepository,
                    tokenGenerator,
                    tokenHasher,
                    emailService
            );
        }

        @Test
        void shouldTrimEmailBeforeLookup() {

            ForgotPasswordRequest request =
                    new ForgotPasswordRequest(
                            "   user@example.com   "
                    );

            when(userRepository.findByEmailIgnoreCase(
                    "user@example.com"
            )).thenReturn(Optional.empty());

            passwordResetService
                    .forgotPassword(request);

            verify(userRepository)
                    .findByEmailIgnoreCase(
                            "user@example.com"
                    );
        }
    }

    @Nested
    @DisplayName("resetPassword")
    class ResetPasswordTests {

        @Test
        void shouldResetPasswordSuccessfully() {

            ResetPasswordRequest request =
                    new ResetPasswordRequest(
                            "raw-token",
                            "new-password",
                            "new-password"
                    );

            PasswordResetToken token =
                    PasswordResetToken.builder()
                            .id("token-id")
                            .user(user)
                            .tokenHash("hashed-token")
                            .expiresAt(
                                    LocalDateTime.now().plusMinutes(30)
                            )
                            .used(false)
                            .build();

            when(tokenHasher.hash("raw-token"))
                    .thenReturn("hashed-token");

            when(passwordResetTokenRepository
                    .findByTokenHash("hashed-token"))
                    .thenReturn(Optional.of(token));

            when(passwordEncoder.encode("new-password"))
                    .thenReturn("encoded-password");

            passwordResetService
                    .resetPassword(request);

            verify(passwordEncoder)
                    .encode("new-password");

            assertEquals(
                    "encoded-password",
                    user.getPassword()
            );

            assertTrue(token.isUsed());

            verify(userRepository)
                    .save(user);

            verify(passwordResetTokenRepository)
                    .save(token);

            verify(refreshTokenSessionServiceImpl)
                    .revokeAllSessions("user-id");
        }

        @Test
        void shouldRejectPasswordMismatch() {

            ResetPasswordRequest request =
                    new ResetPasswordRequest(
                            "raw-token",
                            "password-one",
                            "password-two"
                    );

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> passwordResetService
                                    .resetPassword(request)
                    );

            assertEquals(
                    PASSWORD_MISMATCH,
                    exception.getErrorCode()
            );

            verifyNoInteractions(
                    tokenHasher,
                    passwordResetTokenRepository,
                    passwordEncoder,
                    userRepository,
                    refreshTokenSessionServiceImpl
            );
        }

        @Test
        void shouldRejectInvalidResetToken() {

            ResetPasswordRequest request =
                    new ResetPasswordRequest(
                            "invalid-token",
                            "new-password",
                            "new-password"
                    );

            when(tokenHasher.hash("invalid-token"))
                    .thenReturn("hashed-token");

            when(passwordResetTokenRepository
                    .findByTokenHash("hashed-token"))
                    .thenReturn(Optional.empty());

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> passwordResetService
                                    .resetPassword(request)
                    );

            assertEquals(
                    INVALID_PASSWORD_RESET_TOKEN,
                    exception.getErrorCode()
            );

            verifyNoInteractions(
                    passwordEncoder,
                    userRepository,
                    refreshTokenSessionServiceImpl
            );
        }

        @Test
        void shouldRejectAlreadyUsedToken() {

            ResetPasswordRequest request =
                    new ResetPasswordRequest(
                            "raw-token",
                            "new-password",
                            "new-password"
                    );

            PasswordResetToken token =
                    PasswordResetToken.builder()
                            .id("token-id")
                            .user(user)
                            .tokenHash("hashed-token")
                            .expiresAt(
                                    LocalDateTime.now().plusMinutes(10)
                            )
                            .used(true)
                            .build();

            when(tokenHasher.hash("raw-token"))
                    .thenReturn("hashed-token");

            when(passwordResetTokenRepository
                    .findByTokenHash("hashed-token"))
                    .thenReturn(Optional.of(token));

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> passwordResetService
                                    .resetPassword(request)
                    );

            assertEquals(
                    PASSWORD_RESET_TOKEN_ALREADY_USED,
                    exception.getErrorCode()
            );

            verifyNoInteractions(
                    passwordEncoder,
                    userRepository,
                    refreshTokenSessionServiceImpl
            );
        }

        @Test
        void shouldRejectExpiredToken() {

            ResetPasswordRequest request =
                    new ResetPasswordRequest(
                            "raw-token",
                            "new-password",
                            "new-password"
                    );

            PasswordResetToken token =
                    PasswordResetToken.builder()
                            .id("token-id")
                            .user(user)
                            .tokenHash("hashed-token")
                            .expiresAt(
                                    LocalDateTime.now().minusMinutes(1)
                            )
                            .used(false)
                            .build();

            when(tokenHasher.hash("raw-token"))
                    .thenReturn("hashed-token");

            when(passwordResetTokenRepository
                    .findByTokenHash("hashed-token"))
                    .thenReturn(Optional.of(token));

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> passwordResetService
                                    .resetPassword(request)
                    );

            assertEquals(
                    PASSWORD_RESET_TOKEN_EXPIRED,
                    exception.getErrorCode()
            );

            verifyNoInteractions(
                    passwordEncoder,
                    userRepository,
                    refreshTokenSessionServiceImpl
            );
        }

        @Test
        void shouldNotRevokeSessionsWhenResetFails() {

            ResetPasswordRequest request =
                    new ResetPasswordRequest(
                            "raw-token",
                            "new-password",
                            "new-password"
                    );

            when(tokenHasher.hash("raw-token"))
                    .thenReturn("hashed-token");

            when(passwordResetTokenRepository
                    .findByTokenHash("hashed-token"))
                    .thenReturn(Optional.empty());

            assertThrows(
                    BusinessException.class,
                    () -> passwordResetService
                            .resetPassword(request)
            );

            verifyNoInteractions(
                    passwordEncoder,
                    refreshTokenSessionServiceImpl
            );
        }

        @Test
        void shouldNotSaveUserWhenPasswordMismatch() {

            ResetPasswordRequest request =
                    new ResetPasswordRequest(
                            "raw-token",
                            "password-one",
                            "password-two"
                    );

            assertThrows(
                    BusinessException.class,
                    () -> passwordResetService
                            .resetPassword(request)
            );

            verifyNoInteractions(userRepository);
            verifyNoInteractions(
                    passwordResetTokenRepository
            );
        }
    }
}