package com.agri.market.security.oauth2.service;

import com.agri.market.exception.BusinessException;
import com.agri.market.security.jwt.TokenHasher;
import com.agri.market.security.oauth2.entity.OAuthLoginCode;
import com.agri.market.security.oauth2.repository.OAuthLoginCodeRepository;
import com.agri.market.security.token.TokenGenerator;
import com.agri.market.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.agri.market.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuthLoginCodeServiceImpl")
class OAuthLoginCodeServiceImplTest {

    private static final String CODE =
            "oauth-login-code";

    private static final String HASHED_CODE =
            "hashed-oauth-login-code";

    private static final long EXPIRATION_SECONDS =
            60L;

    @Mock
    private OAuthLoginCodeRepository oauthLoginCodeRepository;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private TokenHasher tokenHasher;

    @Mock
    private User user;

    private OAuthLoginCodeServiceImpl service;

    @BeforeEach
    void setUp() {

        service = new OAuthLoginCodeServiceImpl(
                oauthLoginCodeRepository,
                tokenGenerator,
                tokenHasher
        );

        ReflectionTestUtils.setField(
                service,
                "loginCodeExpirationSeconds",
                EXPIRATION_SECONDS
        );
    }

    @Nested
    @DisplayName("createCode")
    class CreateCodeTests {

        @Test
        @DisplayName("should generate and save OAuth login code")
        void shouldGenerateAndSaveOAuthLoginCode() {

            when(tokenGenerator.generate())
                    .thenReturn(CODE);

            when(tokenHasher.hash(CODE))
                    .thenReturn(HASHED_CODE);

            String result =
                    service.createCode(user);

            assertThat(result)
                    .isEqualTo(CODE);

            verify(tokenGenerator)
                    .generate();

            verify(tokenHasher)
                    .hash(CODE);

            verify(oauthLoginCodeRepository)
                    .save(any(OAuthLoginCode.class));
        }

        @Test
        @DisplayName("should save hashed code")
        void shouldSaveHashedCode() {

            when(tokenGenerator.generate())
                    .thenReturn(CODE);

            when(tokenHasher.hash(CODE))
                    .thenReturn(HASHED_CODE);

            service.createCode(user);

            ArgumentCaptor<OAuthLoginCode> captor =
                    ArgumentCaptor.forClass(
                            OAuthLoginCode.class
                    );

            verify(oauthLoginCodeRepository)
                    .save(captor.capture());

            OAuthLoginCode savedCode =
                    captor.getValue();

            assertThat(savedCode.getCode())
                    .isEqualTo(HASHED_CODE);
        }

        @Test
        @DisplayName("should associate generated code with user")
        void shouldAssociateCodeWithUser() {

            when(tokenGenerator.generate())
                    .thenReturn(CODE);

            when(tokenHasher.hash(CODE))
                    .thenReturn(HASHED_CODE);

            service.createCode(user);

            ArgumentCaptor<OAuthLoginCode> captor =
                    ArgumentCaptor.forClass(
                            OAuthLoginCode.class
                    );

            verify(oauthLoginCodeRepository)
                    .save(captor.capture());

            OAuthLoginCode savedCode =
                    captor.getValue();

            assertThat(savedCode.getUser())
                    .isSameAs(user);
        }

        @Test
        @DisplayName("should create unused login code")
        void shouldCreateUnusedLoginCode() {

            when(tokenGenerator.generate())
                    .thenReturn(CODE);

            when(tokenHasher.hash(CODE))
                    .thenReturn(HASHED_CODE);

            service.createCode(user);

            ArgumentCaptor<OAuthLoginCode> captor =
                    ArgumentCaptor.forClass(
                            OAuthLoginCode.class
                    );

            verify(oauthLoginCodeRepository)
                    .save(captor.capture());

            OAuthLoginCode savedCode =
                    captor.getValue();

            assertThat(savedCode.isUsed())
                    .isFalse();
        }

        @Test
        @DisplayName("should set expiration time")
        void shouldSetExpirationTime() {

            when(tokenGenerator.generate())
                    .thenReturn(CODE);

            when(tokenHasher.hash(CODE))
                    .thenReturn(HASHED_CODE);

            LocalDateTime before =
                    LocalDateTime.now()
                            .plusSeconds(EXPIRATION_SECONDS);

            service.createCode(user);

            LocalDateTime after =
                    LocalDateTime.now()
                            .plusSeconds(EXPIRATION_SECONDS);

            ArgumentCaptor<OAuthLoginCode> captor =
                    ArgumentCaptor.forClass(
                            OAuthLoginCode.class
                    );

            verify(oauthLoginCodeRepository)
                    .save(captor.capture());

            LocalDateTime expiresAt =
                    captor.getValue().getExpiresAt();

            assertThat(expiresAt)
                    .isBetween(before, after);
        }
    }

    @Nested
    @DisplayName("exchangeCode")
    class ExchangeCodeTests {

        @Test
        @DisplayName("should exchange valid OAuth login code")
        void shouldExchangeValidCode() {

            OAuthLoginCode loginCode =
                    OAuthLoginCode.builder()
                            .code(HASHED_CODE)
                            .user(user)
                            .expiresAt(
                                    LocalDateTime.now()
                                            .plusSeconds(60)
                            )
                            .used(false)
                            .build();

            when(tokenHasher.hash(CODE))
                    .thenReturn(HASHED_CODE);

            when(oauthLoginCodeRepository.findByCode(HASHED_CODE))
                    .thenReturn(Optional.of(loginCode));

            User result =
                    service.exchangeCode(CODE);

            assertThat(result)
                    .isSameAs(user);

            assertThat(loginCode.isUsed())
                    .isTrue();

            verify(tokenHasher)
                    .hash(CODE);

            verify(oauthLoginCodeRepository)
                    .findByCode(HASHED_CODE);

            verify(oauthLoginCodeRepository)
                    .save(loginCode);
        }

        @Test
        @DisplayName("should throw exception when OAuth login code does not exist")
        void shouldThrowExceptionWhenCodeDoesNotExist() {

            when(tokenHasher.hash(CODE))
                    .thenReturn(HASHED_CODE);

            when(oauthLoginCodeRepository.findByCode(HASHED_CODE))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    service.exchangeCode(CODE)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            INVALID_OAUTH_LOGIN_CODE
                    );

            verify(tokenHasher)
                    .hash(CODE);

            verify(oauthLoginCodeRepository)
                    .findByCode(HASHED_CODE);

            verify(oauthLoginCodeRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("should throw exception when OAuth login code is already used")
        void shouldThrowExceptionWhenCodeAlreadyUsed() {

            OAuthLoginCode loginCode =
                    OAuthLoginCode.builder()
                            .code(HASHED_CODE)
                            .user(user)
                            .expiresAt(
                                    LocalDateTime.now()
                                            .plusSeconds(60)
                            )
                            .used(true)
                            .build();

            when(tokenHasher.hash(CODE))
                    .thenReturn(HASHED_CODE);

            when(oauthLoginCodeRepository.findByCode(HASHED_CODE))
                    .thenReturn(Optional.of(loginCode));

            assertThatThrownBy(() ->
                    service.exchangeCode(CODE)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            OAUTH_LOGIN_CODE_ALREADY_USED
                    );

            verify(oauthLoginCodeRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("should throw exception when OAuth login code is expired")
        void shouldThrowExceptionWhenCodeIsExpired() {

            OAuthLoginCode loginCode =
                    OAuthLoginCode.builder()
                            .code(HASHED_CODE)
                            .user(user)
                            .expiresAt(
                                    LocalDateTime.now()
                                            .minusSeconds(1)
                            )
                            .used(false)
                            .build();

            when(tokenHasher.hash(CODE))
                    .thenReturn(HASHED_CODE);

            when(oauthLoginCodeRepository.findByCode(HASHED_CODE))
                    .thenReturn(Optional.of(loginCode));

            assertThatThrownBy(() ->
                    service.exchangeCode(CODE)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue(
                            "errorCode",
                            OAUTH_LOGIN_CODE_EXPIRED
                    );

            verify(oauthLoginCodeRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("should hash raw code before repository lookup")
        void shouldHashRawCodeBeforeRepositoryLookup() {

            when(tokenHasher.hash(CODE))
                    .thenReturn(HASHED_CODE);

            when(oauthLoginCodeRepository.findByCode(HASHED_CODE))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    service.exchangeCode(CODE)
            )
                    .isInstanceOf(BusinessException.class);

            verify(tokenHasher)
                    .hash(CODE);

            verify(oauthLoginCodeRepository)
                    .findByCode(HASHED_CODE);
        }
    }
}