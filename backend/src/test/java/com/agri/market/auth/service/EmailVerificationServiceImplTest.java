package com.agri.market.auth.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.email.config.EmailProperties;
import com.agri.market.email.dto.EmailVerificationRequest;
import com.agri.market.email.entity.EmailVerificationToken;
import com.agri.market.email.repository.EmailVerificationTokenRepository;
import com.agri.market.email.service.EmailService;
import com.agri.market.email.service.EmailVerificationServiceImpl;
import com.agri.market.security.jwt.TokenHasher;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static com.agri.market.common.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailVerificationServiceImpl")
class EmailVerificationServiceImplTest {

    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailProperties emailProperties;

    @Mock
    private TokenHasher tokenHasher;

    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user-id");
        user.setEmail("user@example.com");
        user.setEmailVerified(false);
        user.setEnabled(false);
    }

    @Nested
    @DisplayName("sendVerificationEmail")
    class SendVerificationEmailTests {

        @Test
        void shouldDeleteExistingTokenAndSendVerificationEmail() {

            when(tokenHasher.hash(anyString()))
                    .thenReturn("hashed-token");

            when(emailProperties.getFrontendUrl())
                    .thenReturn("http://localhost:3000");

            emailVerificationService.sendVerificationEmail(user);

            verify(tokenRepository)
                    .deleteByUser(user);

            verify(tokenRepository)
                    .save(any(EmailVerificationToken.class));

            verify(emailService)
                    .sendVerificationEmail(
                            eq("user@example.com"),
                            contains(
                                    "http://localhost:3000/verify-email?token="
                            )
                    );
        }

        @Test
        void shouldStoreHashedTokenInsteadOfRawToken() {

            when(tokenHasher.hash(anyString()))
                    .thenReturn("hashed-token");

            when(emailProperties.getFrontendUrl())
                    .thenReturn("http://localhost:3000");

            ArgumentCaptor<EmailVerificationToken> captor =
                    ArgumentCaptor.forClass(
                            EmailVerificationToken.class
                    );

            emailVerificationService.sendVerificationEmail(user);

            verify(tokenRepository)
                    .save(captor.capture());

            EmailVerificationToken token =
                    captor.getValue();

            assertEquals(
                    "hashed-token",
                    token.getTokenHash()
            );

            assertFalse(token.isUsed());
            assertEquals(user, token.getUser());
            assertNotNull(token.getExpiresAt());
        }

        @Test
        void shouldSetTokenExpirationApproximatelyFifteenMinutes() {

            when(tokenHasher.hash(anyString()))
                    .thenReturn("hashed-token");

            ArgumentCaptor<EmailVerificationToken> captor =
                    ArgumentCaptor.forClass(
                            EmailVerificationToken.class
                    );

            LocalDateTime before =
                    LocalDateTime.now().plusMinutes(15);

            emailVerificationService.sendVerificationEmail(user);

            LocalDateTime after =
                    LocalDateTime.now().plusMinutes(15);

            verify(tokenRepository)
                    .save(captor.capture());

            LocalDateTime expiresAt =
                    captor.getValue().getExpiresAt();

            assertFalse(expiresAt.isBefore(before));
            assertFalse(expiresAt.isAfter(after));
        }
    }

    @Nested
    @DisplayName("verifyEmail")
    class VerifyEmailTests {

        @Test
        void shouldVerifyEmailSuccessfully() {

            EmailVerificationToken token =
                    EmailVerificationToken.builder()
                            .id("token-id")
                            .user(user)
                            .tokenHash("hashed-token")
                            .expiresAt(
                                    LocalDateTime.now().plusMinutes(10)
                            )
                            .used(false)
                            .build();

            when(tokenHasher.hash("raw-token"))
                    .thenReturn("hashed-token");

            when(tokenRepository.findByTokenHash("hashed-token"))
                    .thenReturn(Optional.of(token));

            emailVerificationService.verifyEmail("raw-token");

            assertTrue(user.isEmailVerified());
            assertTrue(user.isEnabled());
            assertTrue(token.isUsed());

            verify(userRepository)
                    .save(user);

            verify(tokenRepository)
                    .save(token);
        }

        @Test
        void shouldRejectInvalidToken() {

            when(tokenHasher.hash("invalid-token"))
                    .thenReturn("hashed-token");

            when(tokenRepository.findByTokenHash("hashed-token"))
                    .thenReturn(Optional.empty());

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> emailVerificationService
                                    .verifyEmail("invalid-token")
                    );

            assertEquals(
                    INVALID_VERIFICATION_TOKEN,
                    exception.getErrorCode()
            );

            verifyNoInteractions(userRepository);
            verify(tokenRepository, never())
                    .save(any());
        }

        @Test
        void shouldRejectAlreadyUsedToken() {

            EmailVerificationToken token =
                    EmailVerificationToken.builder()
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

            when(tokenRepository.findByTokenHash("hashed-token"))
                    .thenReturn(Optional.of(token));

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> emailVerificationService
                                    .verifyEmail("raw-token")
                    );

            assertEquals(
                    VERIFICATION_TOKEN_ALREADY_USED,
                    exception.getErrorCode()
            );

            verifyNoInteractions(userRepository);
            verify(tokenRepository, never())
                    .save(any());
        }

        @Test
        void shouldRejectExpiredToken() {

            EmailVerificationToken token =
                    EmailVerificationToken.builder()
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

            when(tokenRepository.findByTokenHash("hashed-token"))
                    .thenReturn(Optional.of(token));

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> emailVerificationService
                                    .verifyEmail("raw-token")
                    );

            assertEquals(
                    VERIFICATION_TOKEN_EXPIRED,
                    exception.getErrorCode()
            );

            verifyNoInteractions(userRepository);
            verify(tokenRepository, never())
                    .save(any());
        }

        @Test
        void shouldNotModifyUserWhenTokenIsInvalid() {

            when(tokenHasher.hash(anyString()))
                    .thenReturn("hashed-token");

            when(tokenRepository.findByTokenHash(anyString()))
                    .thenReturn(Optional.empty());

            assertThrows(
                    BusinessException.class,
                    () -> emailVerificationService
                            .verifyEmail("invalid-token")
            );

            assertFalse(user.isEmailVerified());
            assertFalse(user.isEnabled());
        }
    }

    @Nested
    @DisplayName("resendVerificationEmail")
    class ResendVerificationEmailTests {

        @Test
        void shouldResendVerificationEmailForUnverifiedUser() {

            EmailVerificationRequest request =
                    new EmailVerificationRequest(
                            "  USER@example.com  "
                    );

            when(userRepository.findByEmailIgnoreCase(
                    "USER@example.com"
            )).thenReturn(Optional.of(user));

            when(tokenHasher.hash(anyString()))
                    .thenReturn("hashed-token");

            when(emailProperties.getFrontendUrl())
                    .thenReturn("http://localhost:3000");

            emailVerificationService
                    .resendVerificationEmail(request);

            verify(userRepository)
                    .findByEmailIgnoreCase(
                            "USER@example.com"
                    );

            verify(tokenRepository)
                    .deleteByUser(user);

            verify(emailService)
                    .sendVerificationEmail(
                            eq("user@example.com"),
                            contains("/verify-email?token=")
                    );
        }

        @Test
        void shouldDoNothingForUnknownEmail() {

            EmailVerificationRequest request =
                    new EmailVerificationRequest(
                            "unknown@example.com"
                    );

            when(userRepository.findByEmailIgnoreCase(
                    "unknown@example.com"
            )).thenReturn(Optional.empty());

            emailVerificationService
                    .resendVerificationEmail(request);

            verify(userRepository)
                    .findByEmailIgnoreCase(
                            "unknown@example.com"
                    );

            verifyNoInteractions(
                    tokenRepository,
                    emailService,
                    tokenHasher
            );
        }

        @Test
        void shouldDoNothingForAlreadyVerifiedUser() {

            user.setEmailVerified(true);

            EmailVerificationRequest request =
                    new EmailVerificationRequest(
                            "user@example.com"
                    );

            when(userRepository.findByEmailIgnoreCase(
                    "user@example.com"
            )).thenReturn(Optional.of(user));

            emailVerificationService
                    .resendVerificationEmail(request);

            verify(userRepository)
                    .findByEmailIgnoreCase(
                            "user@example.com"
                    );

            verifyNoInteractions(
                    tokenRepository,
                    emailService,
                    tokenHasher
            );
        }

        @Test
        void shouldTrimEmailBeforeLookup() {

            EmailVerificationRequest request =
                    new EmailVerificationRequest(
                            "   user@example.com   "
                    );

            when(userRepository.findByEmailIgnoreCase(
                    "user@example.com"
            )).thenReturn(Optional.empty());

            emailVerificationService
                    .resendVerificationEmail(request);

            verify(userRepository)
                    .findByEmailIgnoreCase(
                            "user@example.com"
                    );
        }
    }
}