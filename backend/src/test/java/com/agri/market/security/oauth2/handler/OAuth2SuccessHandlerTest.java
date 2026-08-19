package com.agri.market.security.oauth2.handler;

import com.agri.market.security.oauth2.model.OAuthProviderContext;
import com.agri.market.security.oauth2.service.OAuth2AuthenticationService;
import com.agri.market.security.oauth2.service.OAuthLoginCodeService;
import com.agri.market.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    private static final String SUCCESS_URL = "https://frontend.agri-market.com/oauth2/redirect";
    private static final String REGISTRATION_ID = "google";
    private static final String GENERATED_CODE = "generated-login-code";

    @Mock
    private OAuth2AuthenticationService oauth2AuthenticationService;

    @Mock
    private OAuthLoginCodeService oauthLoginCodeService;

    @Mock
    private OAuth2AuthorizedClientService authorizedClientService;

    @Mock
    private RedirectStrategy redirectStrategy;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private OAuth2AuthenticationToken oauth2AuthenticationToken;

    @Mock
    private OAuth2User oauth2User;

    @Mock
    private OAuth2AuthorizedClient authorizedClient;

    @Mock
    private User user;

    private OAuth2SuccessHandler handler;

    private static int anyInt() {
        return org.mockito.ArgumentMatchers.anyInt();
    }

    @BeforeEach
    void setUp() {
        handler = new OAuth2SuccessHandler(
                oauth2AuthenticationService,
                oauthLoginCodeService,
                authorizedClientService
        );

        ReflectionTestUtils.setField(handler, "oauth2SuccessUrl", SUCCESS_URL);
        handler.setRedirectStrategy(redirectStrategy);
    }

    @Nested
    @DisplayName("onAuthenticationSuccess - invalid authentication type")
    class InvalidAuthenticationTypeTests {

        @Test
        @DisplayName("Should return 401 when authentication is not OAuth2AuthenticationToken")
        void shouldReturnUnauthorizedForNonOAuth2Authentication() throws Exception {
            Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);

            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

            verify(httpServletResponse).sendError(
                    eq(HttpServletResponse.SC_UNAUTHORIZED),
                    eq("Invalid OAuth authentication")
            );
        }

        @Test
        @DisplayName("Should not invoke authorized client service for non OAuth2 authentication")
        void shouldNotInvokeAuthorizedClientServiceForNonOAuth2Authentication() throws Exception {
            Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);

            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

            verifyNoInteractions(authorizedClientService);
        }

        @Test
        @DisplayName("Should not invoke authentication service for non OAuth2 authentication")
        void shouldNotInvokeAuthenticationServiceForNonOAuth2Authentication() throws Exception {
            Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);

            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

            verifyNoInteractions(oauth2AuthenticationService);
        }

        @Test
        @DisplayName("Should not attempt redirect for non OAuth2 authentication")
        void shouldNotRedirectForNonOAuth2Authentication() throws Exception {
            Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);

            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

            verifyNoInteractions(redirectStrategy);
        }
    }

    @Nested
    @DisplayName("onAuthenticationSuccess - missing authorized client")
    class MissingAuthorizedClientTests {

        @BeforeEach
        void stubOAuth2Token() {
            when(oauth2AuthenticationToken.getAuthorizedClientRegistrationId())
                    .thenReturn(REGISTRATION_ID);
            when(oauth2AuthenticationToken.getName()).thenReturn("user-name");
        }

        @Test
        @DisplayName("Should return 401 when authorized client is not found")
        void shouldReturnUnauthorizedWhenAuthorizedClientNotFound() throws Exception {
            when(authorizedClientService.loadAuthorizedClient(REGISTRATION_ID, "user-name"))
                    .thenReturn(null);

            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, oauth2AuthenticationToken);

            verify(httpServletResponse).sendError(
                    eq(HttpServletResponse.SC_UNAUTHORIZED),
                    eq("OAuth authorization client not found")
            );
        }

        @Test
        @DisplayName("Should not call authentication service when authorized client is missing")
        void shouldNotCallAuthenticationServiceWhenAuthorizedClientMissing() throws Exception {
            when(authorizedClientService.loadAuthorizedClient(REGISTRATION_ID, "user-name"))
                    .thenReturn(null);

            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, oauth2AuthenticationToken);

            verifyNoInteractions(oauth2AuthenticationService);
        }

        @Test
        @DisplayName("Should not generate login code when authorized client is missing")
        void shouldNotGenerateLoginCodeWhenAuthorizedClientMissing() throws Exception {
            when(authorizedClientService.loadAuthorizedClient(REGISTRATION_ID, "user-name"))
                    .thenReturn(null);

            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, oauth2AuthenticationToken);

            verifyNoInteractions(oauthLoginCodeService);
        }

        @Test
        @DisplayName("Should not attempt redirect when authorized client is missing")
        void shouldNotRedirectWhenAuthorizedClientMissing() throws Exception {
            when(authorizedClientService.loadAuthorizedClient(REGISTRATION_ID, "user-name"))
                    .thenReturn(null);

            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, oauth2AuthenticationToken);

            verifyNoInteractions(redirectStrategy);
        }
    }

    @Nested
    @DisplayName("onAuthenticationSuccess - successful authentication flow")
    class SuccessfulAuthenticationTests {

        private final String userId = "tfyguhjkl";

        @BeforeEach
        void stubHappyPath() {
            when(oauth2AuthenticationToken.getAuthorizedClientRegistrationId())
                    .thenReturn(REGISTRATION_ID);
            when(oauth2AuthenticationToken.getName()).thenReturn("user-name");
            when(oauth2AuthenticationToken.getPrincipal()).thenReturn(oauth2User);

            when(authorizedClientService.loadAuthorizedClient(REGISTRATION_ID, "user-name"))
                    .thenReturn(authorizedClient);

            when(oauth2AuthenticationService.authenticate(eq(REGISTRATION_ID), any(OAuthProviderContext.class)))
                    .thenReturn(user);

            when(user.getId()).thenReturn(userId);

            when(oauthLoginCodeService.createCode(user)).thenReturn(GENERATED_CODE);

            when(httpServletRequest.getSession(false)).thenReturn(null);
        }

        @Test
        @DisplayName("Should authenticate user via oauth2AuthenticationService with correct registration id")
        void shouldAuthenticateUserWithCorrectRegistrationId() throws Exception {
            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, oauth2AuthenticationToken);

            verify(oauth2AuthenticationService, times(1))
                    .authenticate(eq(REGISTRATION_ID), any(OAuthProviderContext.class));
        }

        @Test
        @DisplayName("Should build OAuthProviderContext with resolved oauth2User and authorizedClient")
        void shouldBuildContextWithCorrectFields() throws Exception {
            ArgumentCaptor<OAuthProviderContext> contextCaptor =
                    ArgumentCaptor.forClass(OAuthProviderContext.class);

            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, oauth2AuthenticationToken);

            verify(oauth2AuthenticationService)
                    .authenticate(eq(REGISTRATION_ID), contextCaptor.capture());

            OAuthProviderContext capturedContext = contextCaptor.getValue();

            assertThat(capturedContext.getOauth2User()).isEqualTo(oauth2User);
            assertThat(capturedContext.getAuthorizedClient()).isEqualTo(authorizedClient);
        }

        @Test
        @DisplayName("Should generate login code for authenticated user")
        void shouldGenerateLoginCodeForAuthenticatedUser() throws Exception {
            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, oauth2AuthenticationToken);

            verify(oauthLoginCodeService, times(1)).createCode(user);
        }

        @Test
        @DisplayName("Should redirect to frontend success url appended with generated code")
        void shouldRedirectWithGeneratedCode() throws Exception {
            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, oauth2AuthenticationToken);

            verify(redirectStrategy, times(1)).sendRedirect(
                    httpServletRequest,
                    httpServletResponse,
                    SUCCESS_URL + "?code=" + GENERATED_CODE
            );
        }

        @Test
        @DisplayName("Should not send error response on successful flow")
        void shouldNotSendErrorOnSuccessfulFlow() throws Exception {
            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, oauth2AuthenticationToken);

            verify(httpServletResponse, never()).sendError(anyInt(), anyString());
        }

        @Test
        @DisplayName("Should load authorized client using registration id and principal name")
        void shouldLoadAuthorizedClientUsingRegistrationIdAndPrincipalName() throws Exception {
            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, oauth2AuthenticationToken);

            verify(authorizedClientService, times(1))
                    .loadAuthorizedClient(REGISTRATION_ID, "user-name");
        }

        @Test
        @DisplayName("Should complete full flow exactly once per invocation")
        void shouldCompleteFullFlowExactlyOnce() throws Exception {
            handler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, oauth2AuthenticationToken);

            verify(authorizedClientService, times(1))
                    .loadAuthorizedClient(anyString(), anyString());
            verify(oauth2AuthenticationService, times(1))
                    .authenticate(anyString(), any(OAuthProviderContext.class));
            verify(oauthLoginCodeService, times(1)).createCode(any(User.class));
            verify(redirectStrategy, times(1))
                    .sendRedirect(any(HttpServletRequest.class), any(HttpServletResponse.class), anyString());
        }
    }
}