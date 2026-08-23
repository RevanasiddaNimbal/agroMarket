package com.agri.market.security.oauth2.controller;

import com.agri.market.auth.dto.AuthenticationResult;
import com.agri.market.auth.dto.ClientInfo;
import com.agri.market.security.client.ClientInfoResolver;
import com.agri.market.security.oauth2.dto.OAuthCodeExchangeRequest;
import com.agri.market.security.oauth2.service.OAuth2TokenExchangeService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2TokenExchangeControllerTest {

    @Mock
    private OAuth2TokenExchangeService oauth2TokenExchangeService;

    @Mock
    private ClientInfoResolver clientInfoResolver;

    @InjectMocks
    private OAuth2TokenExchangeController controller;

    private HttpServletRequest httpServletRequest;
    private OAuthCodeExchangeRequest exchangeRequest;
    private ClientInfo clientInfo;
    private AuthenticationResult authenticationResult;

    @BeforeEach
    void setUp() {
        httpServletRequest = mock(HttpServletRequest.class);

        exchangeRequest = new OAuthCodeExchangeRequest();
        exchangeRequest.setCode("valid-oauth-code");

        clientInfo = ClientInfo.builder()
                .ipAddress("127.0.0.1")
                .deviceName("JUnit-Agent")
                .build();

        authenticationResult = AuthenticationResult.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();
    }

    @Nested
    @DisplayName("Exchange endpoint - success scenarios")
    class ExchangeSuccessTests {

        @Test
        @DisplayName("Should return 200 OK with authentication response on valid code")
        void shouldReturnOkWithAuthenticationResponse() {
            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(oauth2TokenExchangeService.exchange(exchangeRequest.getCode(), clientInfo))
                    .thenReturn(authenticationResult);

            ResponseEntity<AuthenticationResult> result =
                    controller.exchange(exchangeRequest, httpServletRequest);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isEqualTo(authenticationResult);
        }

        @Test
        @DisplayName("Should resolve client info before invoking exchange service")
        void shouldResolveClientInfoBeforeExchange() {
            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(oauth2TokenExchangeService.exchange(anyString(), any(ClientInfo.class)))
                    .thenReturn(authenticationResult);

            controller.exchange(exchangeRequest, httpServletRequest);

            verify(clientInfoResolver, times(1)).resolve(httpServletRequest);
            verify(oauth2TokenExchangeService, times(1))
                    .exchange(eq(exchangeRequest.getCode()), eq(clientInfo));
        }

        @Test
        @DisplayName("Should pass exact code from request body to service")
        void shouldPassExactCodeToService() {
            OAuthCodeExchangeRequest customRequest = new OAuthCodeExchangeRequest();
            customRequest.setCode("another-code-123");

            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(oauth2TokenExchangeService.exchange(anyString(), any(ClientInfo.class)))
                    .thenReturn(authenticationResult);

            controller.exchange(customRequest, httpServletRequest);

            verify(oauth2TokenExchangeService).exchange(eq("another-code-123"), eq(clientInfo));
        }

        @Test
        @DisplayName("Should return response body matching service output")
        void shouldReturnResponseBodyMatchingServiceOutput() {
            AuthenticationResult customResponse = AuthenticationResult.builder()
                    .accessToken("custom-access-token")
                    .refreshToken("custom-refresh-token")
                    .build();

            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(oauth2TokenExchangeService.exchange(anyString(), any(ClientInfo.class)))
                    .thenReturn(customResponse);

            ResponseEntity<AuthenticationResult> result =
                    controller.exchange(exchangeRequest, httpServletRequest);

            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getAccessToken()).isEqualTo("custom-access-token");
            assertThat(result.getBody().getRefreshToken()).isEqualTo("custom-refresh-token");
        }

        @Test
        @DisplayName("Should call dependencies exactly once and no more interactions")
        void shouldCallDependenciesExactlyOnce() {
            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(oauth2TokenExchangeService.exchange(anyString(), any(ClientInfo.class)))
                    .thenReturn(authenticationResult);

            controller.exchange(exchangeRequest, httpServletRequest);

            verify(clientInfoResolver, times(1)).resolve(any(HttpServletRequest.class));
            verify(oauth2TokenExchangeService, times(1))
                    .exchange(anyString(), any(ClientInfo.class));
            verifyNoMoreInteractions(clientInfoResolver, oauth2TokenExchangeService);
        }
    }

    @Nested
    @DisplayName("Exchange endpoint - failure scenarios")
    class ExchangeFailureTests {

        @Test
        @DisplayName("Should propagate exception when service throws IllegalArgumentException")
        void shouldPropagateExceptionOnInvalidCode() {
            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(oauth2TokenExchangeService.exchange(anyString(), any(ClientInfo.class)))
                    .thenThrow(new IllegalArgumentException("Invalid or expired code"));

            org.junit.jupiter.api.function.Executable executable =
                    () -> controller.exchange(exchangeRequest, httpServletRequest);

            org.junit.jupiter.api.Assertions.assertThrows(
                    IllegalArgumentException.class, executable
            );
        }

        @Test
        @DisplayName("Should propagate exception when service throws authentication failure")
        void shouldPropagateExceptionOnAuthenticationFailure() {
            when(clientInfoResolver.resolve(httpServletRequest)).thenReturn(clientInfo);
            when(oauth2TokenExchangeService.exchange(anyString(), any(ClientInfo.class)))
                    .thenThrow(new SecurityException("OAuth2 authentication failed"));

            org.junit.jupiter.api.function.Executable executable =
                    () -> controller.exchange(exchangeRequest, httpServletRequest);

            org.junit.jupiter.api.Assertions.assertThrows(
                    SecurityException.class, executable
            );
        }

        @Test
        @DisplayName("Should not call exchange service when client info resolution fails")
        void shouldNotCallExchangeServiceWhenClientInfoResolutionFails() {
            when(clientInfoResolver.resolve(httpServletRequest))
                    .thenThrow(new RuntimeException("Unable to resolve client info"));

            org.junit.jupiter.api.function.Executable executable =
                    () -> controller.exchange(exchangeRequest, httpServletRequest);

            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class, executable
            );
            verify(oauth2TokenExchangeService, never()).exchange(anyString(), any(ClientInfo.class));
        }
    }
}