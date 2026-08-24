package com.agri.market.security.oauth2.service;

import com.agri.market.auth.dto.AuthenticationResult;
import com.agri.market.auth.service.AuthenticationTokenService;
import com.agri.market.security.client.ClientInfo;
import com.agri.market.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2TokenExchangeServiceImpl")
class OAuth2TokenExchangeServiceImplTest {

    private static final String CODE =
            "oauth-login-code";

    @Mock
    private OAuthLoginCodeService oauthLoginCodeService;

    @Mock
    private AuthenticationTokenService authenticationTokenService;

    @Mock
    private User user;

    @Mock
    private ClientInfo clientInfo;

    @Mock
    private AuthenticationResult authenticationResult;

    private OAuth2TokenExchangeServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OAuth2TokenExchangeServiceImpl(
                oauthLoginCodeService,
                authenticationTokenService
        );
    }

    @Nested
    @DisplayName("exchange")
    class ExchangeTests {

        @Test
        @DisplayName("should exchange OAuth login code and create authentication session")
        void shouldExchangeCodeAndCreateAuthenticationSession() {

            when(oauthLoginCodeService.exchangeCode(CODE))
                    .thenReturn(user);

            when(authenticationTokenService.createAuthenticationSession(
                    user,
                    clientInfo
            )).thenReturn(authenticationResult);

            AuthenticationResult result =
                    service.exchange(
                            CODE,
                            clientInfo
                    );

            assertThat(result)
                    .isSameAs(authenticationResult);

            verify(oauthLoginCodeService)
                    .exchangeCode(CODE);

            verify(authenticationTokenService)
                    .createAuthenticationSession(
                            user,
                            clientInfo
                    );
        }

        @Test
        @DisplayName("should pass the exact OAuth login code to code service")
        void shouldPassExactCodeToCodeService() {

            String code = "AbC-123_XYZ";

            when(oauthLoginCodeService.exchangeCode(code))
                    .thenReturn(user);

            when(authenticationTokenService.createAuthenticationSession(
                    user,
                    clientInfo
            )).thenReturn(authenticationResult);

            AuthenticationResult result =
                    service.exchange(
                            code,
                            clientInfo
                    );

            assertThat(result)
                    .isSameAs(authenticationResult);

            verify(oauthLoginCodeService)
                    .exchangeCode(code);

            verify(authenticationTokenService)
                    .createAuthenticationSession(
                            user,
                            clientInfo
                    );
        }

        @Test
        @DisplayName("should pass resolved user and client information to token service")
        void shouldPassUserAndClientInfoToTokenService() {

            when(oauthLoginCodeService.exchangeCode(CODE))
                    .thenReturn(user);

            when(authenticationTokenService.createAuthenticationSession(
                    user,
                    clientInfo
            )).thenReturn(authenticationResult);

            service.exchange(
                    CODE,
                    clientInfo
            );

            verify(authenticationTokenService)
                    .createAuthenticationSession(
                            user,
                            clientInfo
                    );
        }

        @Test
        @DisplayName("should return authentication response from token service")
        void shouldReturnAuthenticationResponse() {

            when(oauthLoginCodeService.exchangeCode(CODE))
                    .thenReturn(user);

            when(authenticationTokenService.createAuthenticationSession(
                    user,
                    clientInfo
            )).thenReturn(authenticationResult);

            AuthenticationResult result =
                    service.exchange(
                            CODE,
                            clientInfo
                    );

            assertThat(result)
                    .isEqualTo(authenticationResult);
        }

        @Test
        @DisplayName("should not create authentication session when OAuth code exchange fails")
        void shouldNotCreateAuthenticationSessionWhenCodeExchangeFails() {

            RuntimeException exception =
                    new RuntimeException("Invalid OAuth login code");

            when(oauthLoginCodeService.exchangeCode(CODE))
                    .thenThrow(exception);

            org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                            service.exchange(
                                    CODE,
                                    clientInfo
                            )
                    )
                    .isSameAs(exception);

            verify(oauthLoginCodeService)
                    .exchangeCode(CODE);

            verifyNoInteractions(authenticationTokenService);
        }
    }
}