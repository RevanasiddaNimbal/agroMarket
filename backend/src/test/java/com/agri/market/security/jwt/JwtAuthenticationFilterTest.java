package com.agri.market.security.jwt;

import com.agri.market.support.UserTestFactory;
import com.agri.market.user.entity.User;
import com.agri.market.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
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
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldPopulateSecurityContextForValidBearerToken() throws Exception {
        User user = UserTestFactory.activeUser();
        String token = jwtService.generateAccessToken(user.getUsername());
        when(userService.loadUserByUsername(user.getUsername())).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        request.setMethod("GET");
        request.setRequestURI("/api/v1/users/me");

        jwtAuthenticationFilter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(user);
        verify(userService).loadUserByUsername(user.getUsername());
    }

    @Test
    void shouldBypassWhenAuthorizationHeaderIsMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/v1/users/me");

        jwtAuthenticationFilter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(userService);
    }

    @Test
    void shouldIgnoreMalformedBearerToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid.jwt.token");
        request.setMethod("GET");
        request.setRequestURI("/api/v1/users/me");

        jwtAuthenticationFilter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(userService);
    }
}
