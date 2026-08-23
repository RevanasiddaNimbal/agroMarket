package com.agri.market.security.jwt;

import com.agri.market.support.UserTestFactory;
import com.agri.market.user.entity.User;
import com.agri.market.user.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("JwtAuthenticationFilter")
class JwtAuthenticationFilterTest {

    private JwtService jwtService;

    private UserService userService;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {

        jwtService = new JwtService(
                "keys/local-only/private_key.pem",
                "keys/local-only/public_key.pem",
                600000L,
                2000L
        );

        userService = mock(UserService.class);

        jwtAuthenticationFilter =
                new JwtAuthenticationFilter(
                        jwtService,
                        userService
                );

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {

        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Bearer token authentication")
    class BearerTokenTests {

        @Test
        void shouldAuthenticateUserWithValidBearerToken()
                throws Exception {

            final User user =
                    UserTestFactory.activeUser();

            final String token =
                    jwtService.generateAccessToken(
                            user.getUsername()
                    );

            when(userService.loadUserByUsername(
                    user.getUsername()
            )).thenReturn(user);

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.addHeader(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + token
            );

            request.setMethod("GET");
            request.setRequestURI("/api/v1/users/me");

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            ).isNotNull();

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getPrincipal()
            ).isEqualTo(user);

            verify(userService)
                    .loadUserByUsername(
                            user.getUsername()
                    );
        }

        @Test
        void shouldBypassWhenAuthorizationHeaderIsMissing()
                throws Exception {

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.setMethod("GET");
            request.setRequestURI("/api/v1/users/me");

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            ).isNull();

            verifyNoInteractions(userService);
        }

        @Test
        void shouldIgnoreEmptyBearerToken()
                throws Exception {

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.addHeader(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer "
            );

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            ).isNull();

            verifyNoInteractions(userService);
        }

        @Test
        void shouldIgnoreMalformedBearerToken()
                throws Exception {

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.addHeader(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer invalid.jwt.token"
            );

            request.setMethod("GET");
            request.setRequestURI("/api/v1/users/me");

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            ).isNull();

            verifyNoInteractions(userService);
        }

        @Test
        void shouldIgnoreAuthorizationHeaderWithInvalidPrefix()
                throws Exception {

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.addHeader(
                    HttpHeaders.AUTHORIZATION,
                    "Basic invalid-token"
            );

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            ).isNull();

            verifyNoInteractions(userService);
        }
    }

    @Nested
    @DisplayName("Cookie authentication")
    class CookieAuthenticationTests {

        @Test
        void shouldAuthenticateUserWithAccessTokenCookie()
                throws Exception {

            final User user =
                    UserTestFactory.activeUser();

            final String token =
                    jwtService.generateAccessToken(
                            user.getUsername()
                    );

            when(userService.loadUserByUsername(
                    user.getUsername()
            )).thenReturn(user);

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.setCookies(
                    new Cookie(
                            "access_token",
                            token
                    )
            );

            request.setMethod("GET");
            request.setRequestURI("/api/v1/users/me");

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            ).isNotNull();

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getPrincipal()
            ).isEqualTo(user);

            verify(userService)
                    .loadUserByUsername(
                            user.getUsername()
                    );
        }

        @Test
        void shouldUseAccessTokenCookieWhenAuthorizationHeaderIsMissing()
                throws Exception {

            final User user =
                    UserTestFactory.activeUser();

            final String token =
                    jwtService.generateAccessToken(
                            user.getUsername()
                    );

            when(userService.loadUserByUsername(
                    user.getUsername()
            )).thenReturn(user);

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.setCookies(
                    new Cookie(
                            "access_token",
                            token
                    )
            );

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            ).isNotNull();

            verify(userService)
                    .loadUserByUsername(
                            user.getUsername()
                    );
        }

        @Test
        void shouldIgnoreRequestWhenAccessTokenCookieIsMissing()
                throws Exception {

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.setCookies(
                    new Cookie(
                            "refresh_token",
                            "refresh-token"
                    )
            );

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            ).isNull();

            verifyNoInteractions(userService);
        }

        @Test
        void shouldIgnoreRequestWhenCookiesAreMissing()
                throws Exception {

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.setCookies();

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            ).isNull();

            verifyNoInteractions(userService);
        }

        @Test
        void shouldIgnoreInvalidAccessTokenCookie()
                throws Exception {

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.setCookies(
                    new Cookie(
                            "access_token",
                            "invalid.jwt.token"
                    )
            );

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            ).isNull();

            verifyNoInteractions(userService);
        }

        @Test
        void shouldIgnoreBlankAccessTokenCookie()
                throws Exception {

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.setCookies(
                    new Cookie(
                            "access_token",
                            ""
                    )
            );

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            ).isNull();

            verifyNoInteractions(userService);
        }

        @Test
        void shouldFindAccessTokenAmongMultipleCookies()
                throws Exception {

            final User user =
                    UserTestFactory.activeUser();

            final String token =
                    jwtService.generateAccessToken(
                            user.getUsername()
                    );

            when(userService.loadUserByUsername(
                    user.getUsername()
            )).thenReturn(user);

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.setCookies(
                    new Cookie(
                            "session",
                            "session-value"
                    ),
                    new Cookie(
                            "refresh_token",
                            "refresh-token"
                    ),
                    new Cookie(
                            "access_token",
                            token
                    )
            );

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            ).isNotNull();

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getPrincipal()
            ).isEqualTo(user);
        }
    }

    @Nested
    @DisplayName("Token precedence")
    class TokenPrecedenceTests {

        @Test
        void shouldPreferBearerTokenOverAccessTokenCookie()
                throws Exception {

            final User bearerUser =
                    UserTestFactory.activeUser();

            bearerUser.setEmail("bearer@example.com");

            final String bearerToken =
                    jwtService.generateAccessToken(
                            bearerUser.getUsername()
                    );

            final User cookieUser =
                    UserTestFactory.activeUser();

            cookieUser.setEmail("cookie@example.com");

            final String cookieToken =
                    jwtService.generateAccessToken(
                            cookieUser.getUsername()
                    );

            when(userService.loadUserByUsername(
                    bearerUser.getUsername()
            )).thenReturn(bearerUser);

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.addHeader(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + bearerToken
            );

            request.setCookies(
                    new Cookie(
                            "access_token",
                            cookieToken
                    )
            );

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getPrincipal()
            ).isEqualTo(bearerUser);

            verify(userService)
                    .loadUserByUsername(
                            bearerUser.getUsername()
                    );

            verify(userService, never())
                    .loadUserByUsername(
                            cookieUser.getUsername()
                    );
        }

        @Test
        void shouldFallbackToCookieWhenAuthorizationHeaderIsAbsent()
                throws Exception {

            final User user =
                    UserTestFactory.activeUser();

            final String token =
                    jwtService.generateAccessToken(
                            user.getUsername()
                    );

            when(userService.loadUserByUsername(
                    user.getUsername()
            )).thenReturn(user);

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.setCookies(
                    new Cookie(
                            "access_token",
                            token
                    )
            );

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            ).isNotNull();

            verify(userService)
                    .loadUserByUsername(
                            user.getUsername()
                    );
        }
    }

    @Nested
    @DisplayName("Security context")
    class SecurityContextTests {

        @Test
        void shouldNotAuthenticateAgainWhenSecurityContextAlreadyContainsAuthentication()
                throws Exception {

            final User existingUser =
                    UserTestFactory.activeUser();

            final UsernamePasswordAuthenticationToken existingAuthentication =
                    new UsernamePasswordAuthenticationToken(
                            existingUser,
                            null,
                            existingUser.getAuthorities()
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(
                            existingAuthentication
                    );

            final User tokenUser =
                    UserTestFactory.activeUser();

            final String token =
                    jwtService.generateAccessToken(
                            tokenUser.getUsername()
                    );

            final MockHttpServletRequest request =
                    new MockHttpServletRequest();

            request.addHeader(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + token
            );

            jwtAuthenticationFilter.doFilter(
                    request,
                    new MockHttpServletResponse(),
                    new MockFilterChain()
            );

            assertThat(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getPrincipal()
            ).isEqualTo(existingUser);

            verifyNoInteractions(userService);
        }
    }
}