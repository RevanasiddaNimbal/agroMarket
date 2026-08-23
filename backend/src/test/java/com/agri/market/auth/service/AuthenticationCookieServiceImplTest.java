package com.agri.market.auth.service;

import com.agri.market.security.properties.AuthenticationCookieProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationCookieServiceImpl")
class AuthenticationCookieServiceImplTest {

    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";

    @Mock
    private AuthenticationCookieProperties properties;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationCookieServiceImpl cookieService;

    @Nested
    @DisplayName("addAuthenticationCookies")
    class AddAuthenticationCookiesTests {

        @Test
        void shouldAddAccessAndRefreshTokenCookies() {

            when(properties.getAccessTokenMaxAge())
                    .thenReturn(Duration.ofMinutes(15));

            when(properties.getRefreshTokenMaxAge())
                    .thenReturn(Duration.ofDays(7));

            when(properties.isSecure())
                    .thenReturn(true);

            when(properties.getSameSite())
                    .thenReturn("Strict");

            cookieService.addAuthenticationCookies(
                    response,
                    ACCESS_TOKEN,
                    REFRESH_TOKEN
            );

            ArgumentCaptor<String> captor =
                    ArgumentCaptor.forClass(String.class);

            verify(response, times(2))
                    .addHeader(
                            eq(HttpHeaders.SET_COOKIE),
                            captor.capture()
                    );

            List<String> cookies = captor.getAllValues();

            assertThat(cookies)
                    .hasSize(2);

            assertThat(cookies.get(0))
                    .contains("access_token=access-token");

            assertThat(cookies.get(1))
                    .contains("refresh_token=refresh-token");
        }

        @Test
        void shouldCreateSecureHttpOnlyCookies() {

            when(properties.getAccessTokenMaxAge())
                    .thenReturn(Duration.ofMinutes(15));

            when(properties.getRefreshTokenMaxAge())
                    .thenReturn(Duration.ofDays(7));

            when(properties.isSecure())
                    .thenReturn(true);

            when(properties.getSameSite())
                    .thenReturn("Strict");

            cookieService.addAuthenticationCookies(
                    response,
                    ACCESS_TOKEN,
                    REFRESH_TOKEN
            );

            ArgumentCaptor<String> captor =
                    ArgumentCaptor.forClass(String.class);

            verify(response, times(2))
                    .addHeader(
                            eq(HttpHeaders.SET_COOKIE),
                            captor.capture()
                    );

            for (String cookie : captor.getAllValues()) {

                assertThat(cookie)
                        .contains("HttpOnly");

                assertThat(cookie)
                        .contains("Secure");

                assertThat(cookie)
                        .contains("SameSite=Strict");

                assertThat(cookie)
                        .contains("Path=/");
            }
        }

        @Test
        void shouldUseConfiguredAccessTokenMaxAge() {

            when(properties.getAccessTokenMaxAge())
                    .thenReturn(Duration.ofMinutes(30));

            when(properties.getRefreshTokenMaxAge())
                    .thenReturn(Duration.ofDays(7));

            when(properties.isSecure())
                    .thenReturn(false);

            when(properties.getSameSite())
                    .thenReturn("Lax");

            cookieService.addAuthenticationCookies(
                    response,
                    ACCESS_TOKEN,
                    REFRESH_TOKEN
            );

            ArgumentCaptor<String> captor =
                    ArgumentCaptor.forClass(String.class);

            verify(response, times(2))
                    .addHeader(
                            eq(HttpHeaders.SET_COOKIE),
                            captor.capture()
                    );

            assertThat(captor.getAllValues().get(0))
                    .contains("Max-Age=1800");
        }

        @Test
        void shouldUseConfiguredRefreshTokenMaxAge() {

            when(properties.getAccessTokenMaxAge())
                    .thenReturn(Duration.ofMinutes(15));

            when(properties.getRefreshTokenMaxAge())
                    .thenReturn(Duration.ofDays(14));

            when(properties.isSecure())
                    .thenReturn(false);

            when(properties.getSameSite())
                    .thenReturn("Lax");

            cookieService.addAuthenticationCookies(
                    response,
                    ACCESS_TOKEN,
                    REFRESH_TOKEN
            );

            ArgumentCaptor<String> captor =
                    ArgumentCaptor.forClass(String.class);

            verify(response, times(2))
                    .addHeader(
                            eq(HttpHeaders.SET_COOKIE),
                            captor.capture()
                    );

            assertThat(captor.getAllValues().get(1))
                    .contains("Max-Age=1209600");
        }

        @Test
        void shouldCreateNonSecureCookiesWhenSecureIsDisabled() {

            when(properties.getAccessTokenMaxAge())
                    .thenReturn(Duration.ofMinutes(15));

            when(properties.getRefreshTokenMaxAge())
                    .thenReturn(Duration.ofDays(7));

            when(properties.isSecure())
                    .thenReturn(false);

            when(properties.getSameSite())
                    .thenReturn("Lax");

            cookieService.addAuthenticationCookies(
                    response,
                    ACCESS_TOKEN,
                    REFRESH_TOKEN
            );

            ArgumentCaptor<String> captor =
                    ArgumentCaptor.forClass(String.class);

            verify(response, times(2))
                    .addHeader(
                            eq(HttpHeaders.SET_COOKIE),
                            captor.capture()
                    );

            assertThat(captor.getAllValues())
                    .allMatch(cookie -> !cookie.contains("Secure"));
        }

        @Test
        void shouldHandleNullAccessToken() {

            when(properties.getAccessTokenMaxAge())
                    .thenReturn(Duration.ofMinutes(15));

            when(properties.getRefreshTokenMaxAge())
                    .thenReturn(Duration.ofDays(7));

            when(properties.isSecure())
                    .thenReturn(false);

            when(properties.getSameSite())
                    .thenReturn("Lax");

            cookieService.addAuthenticationCookies(
                    response,
                    null,
                    REFRESH_TOKEN
            );

            verify(response, times(2))
                    .addHeader(
                            eq(HttpHeaders.SET_COOKIE),
                            anyString()
                    );
        }

        @Test
        void shouldHandleNullRefreshToken() {

            when(properties.getAccessTokenMaxAge())
                    .thenReturn(Duration.ofMinutes(15));

            when(properties.getRefreshTokenMaxAge())
                    .thenReturn(Duration.ofDays(7));

            when(properties.isSecure())
                    .thenReturn(false);

            when(properties.getSameSite())
                    .thenReturn("Lax");

            cookieService.addAuthenticationCookies(
                    response,
                    ACCESS_TOKEN,
                    null
            );

            verify(response, times(2))
                    .addHeader(
                            eq(HttpHeaders.SET_COOKIE),
                            anyString()
                    );
        }
    }

    @Nested
    @DisplayName("getRefreshToken")
    class GetRefreshTokenTests {

        @Test
        void shouldReturnRefreshTokenWhenCookieExists() {

            Cookie refreshCookie =
                    new Cookie(
                            "refresh_token",
                            REFRESH_TOKEN
                    );

            when(request.getCookies())
                    .thenReturn(new Cookie[]{refreshCookie});

            String result =
                    cookieService.getRefreshToken(request);

            assertThat(result)
                    .isEqualTo(REFRESH_TOKEN);
        }

        @Test
        void shouldReturnRefreshTokenWhenMultipleCookiesExist() {

            Cookie accessCookie =
                    new Cookie(
                            "access_token",
                            ACCESS_TOKEN
                    );

            Cookie refreshCookie =
                    new Cookie(
                            "refresh_token",
                            REFRESH_TOKEN
                    );

            Cookie otherCookie =
                    new Cookie(
                            "other",
                            "value"
                    );

            when(request.getCookies())
                    .thenReturn(
                            new Cookie[]{
                                    accessCookie,
                                    otherCookie,
                                    refreshCookie
                            }
                    );

            String result =
                    cookieService.getRefreshToken(request);

            assertThat(result)
                    .isEqualTo(REFRESH_TOKEN);
        }

        @Test
        void shouldReturnNullWhenCookiesAreNull() {

            when(request.getCookies())
                    .thenReturn(null);

            String result =
                    cookieService.getRefreshToken(request);

            assertThat(result)
                    .isNull();
        }

        @Test
        void shouldReturnNullWhenCookieArrayIsEmpty() {

            when(request.getCookies())
                    .thenReturn(new Cookie[0]);

            String result =
                    cookieService.getRefreshToken(request);

            assertThat(result)
                    .isNull();
        }

        @Test
        void shouldReturnNullWhenRefreshTokenCookieDoesNotExist() {

            Cookie accessCookie =
                    new Cookie(
                            "access_token",
                            ACCESS_TOKEN
                    );

            Cookie otherCookie =
                    new Cookie(
                            "other",
                            "value"
                    );

            when(request.getCookies())
                    .thenReturn(
                            new Cookie[]{
                                    accessCookie,
                                    otherCookie
                            }
                    );

            String result =
                    cookieService.getRefreshToken(request);

            assertThat(result)
                    .isNull();
        }

        @Test
        void shouldReturnEmptyValueWhenRefreshTokenCookieIsEmpty() {

            Cookie refreshCookie =
                    new Cookie(
                            "refresh_token",
                            ""
                    );

            when(request.getCookies())
                    .thenReturn(
                            new Cookie[]{refreshCookie}
                    );

            String result =
                    cookieService.getRefreshToken(request);

            assertThat(result)
                    .isEmpty();
        }

        @Test
        void shouldIgnoreAccessTokenCookie() {

            Cookie accessCookie =
                    new Cookie(
                            "access_token",
                            ACCESS_TOKEN
                    );

            when(request.getCookies())
                    .thenReturn(
                            new Cookie[]{accessCookie}
                    );

            String result =
                    cookieService.getRefreshToken(request);

            assertThat(result)
                    .isNull();
        }
    }

    @Nested
    @DisplayName("clearAuthenticationCookies")
    class ClearAuthenticationCookiesTests {

        @Test
        void shouldClearAccessAndRefreshTokenCookies() {

            when(properties.isSecure())
                    .thenReturn(true);

            when(properties.getSameSite())
                    .thenReturn("Strict");

            cookieService.clearAuthenticationCookies(response);

            ArgumentCaptor<String> captor =
                    ArgumentCaptor.forClass(String.class);

            verify(response, times(2))
                    .addHeader(
                            eq(HttpHeaders.SET_COOKIE),
                            captor.capture()
                    );

            List<String> cookies =
                    captor.getAllValues();

            assertThat(cookies)
                    .anyMatch(cookie ->
                            cookie.contains("access_token="));

            assertThat(cookies)
                    .anyMatch(cookie ->
                            cookie.contains("refresh_token="));
        }

        @Test
        void shouldSetZeroMaxAgeForBothCookies() {

            when(properties.isSecure())
                    .thenReturn(true);

            when(properties.getSameSite())
                    .thenReturn("Strict");

            cookieService.clearAuthenticationCookies(response);

            ArgumentCaptor<String> captor =
                    ArgumentCaptor.forClass(String.class);

            verify(response, times(2))
                    .addHeader(
                            eq(HttpHeaders.SET_COOKIE),
                            captor.capture()
                    );

            assertThat(captor.getAllValues())
                    .allMatch(cookie ->
                            cookie.contains("Max-Age=0"));
        }

        @Test
        void shouldCreateHttpOnlyCookiesWhenClearing() {

            when(properties.isSecure())
                    .thenReturn(true);

            when(properties.getSameSite())
                    .thenReturn("Lax");

            cookieService.clearAuthenticationCookies(response);

            ArgumentCaptor<String> captor =
                    ArgumentCaptor.forClass(String.class);

            verify(response, times(2))
                    .addHeader(
                            eq(HttpHeaders.SET_COOKIE),
                            captor.capture()
                    );

            assertThat(captor.getAllValues())
                    .allMatch(cookie ->
                            cookie.contains("HttpOnly"));
        }

        @Test
        void shouldPreserveCookieSecurityConfigurationWhenClearing() {

            when(properties.isSecure())
                    .thenReturn(true);

            when(properties.getSameSite())
                    .thenReturn("Strict");

            cookieService.clearAuthenticationCookies(response);

            ArgumentCaptor<String> captor =
                    ArgumentCaptor.forClass(String.class);

            verify(response, times(2))
                    .addHeader(
                            eq(HttpHeaders.SET_COOKIE),
                            captor.capture()
                    );

            assertThat(captor.getAllValues())
                    .allMatch(cookie ->
                            cookie.contains("Secure")
                                    && cookie.contains("SameSite=Strict")
                                    && cookie.contains("Path=/"));
        }

        @Test
        void shouldAddExactlyTwoSetCookieHeaders() {

            when(properties.isSecure())
                    .thenReturn(false);

            when(properties.getSameSite())
                    .thenReturn("Lax");

            cookieService.clearAuthenticationCookies(response);

            verify(response, times(2))
                    .addHeader(
                            eq(HttpHeaders.SET_COOKIE),
                            anyString()
                    );

            verifyNoMoreInteractions(response);
        }
    }
}