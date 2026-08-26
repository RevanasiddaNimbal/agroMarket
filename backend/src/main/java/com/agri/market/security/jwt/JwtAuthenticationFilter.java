package com.agri.market.security.jwt;

import com.agri.market.user.entity.User;
import com.agri.market.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private static final String ACCESS_TOKEN_COOKIE =
            "access_token";

    private final JwtService jwtService;
    private final UserService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {

        final String jwt =
                resolveToken(request);

        if (jwt == null || jwt.isBlank()) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        try {

            authenticateUser(
                    jwt,
                    request
            );

        } catch (Exception exception) {

            SecurityContextHolder.clearContext();

            log.debug(
                    "JWT authentication failed for {} {}",
                    request.getMethod(),
                    request.getRequestURI()
            );
        }

        filterChain.doFilter(
                request,
                response
        );
    }

    private String resolveToken(
            final HttpServletRequest request
    ) {

        final String authorizationHeader =
                request.getHeader(
                        HttpHeaders.AUTHORIZATION
                );

        if (authorizationHeader != null
                && authorizationHeader.startsWith(
                BEARER_PREFIX
        )) {

            final String jwt =
                    authorizationHeader.substring(
                            BEARER_PREFIX.length()
                    );

            if (!jwt.isBlank()) {
                return jwt;
            }

            log.debug(
                    "Empty Bearer token received"
            );

            return null;
        }

        return extractAccessTokenFromCookie(
                request
        );
    }

    private String extractAccessTokenFromCookie(
            final HttpServletRequest request
    ) {

        final Cookie[] cookies =
                request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (final Cookie cookie : cookies) {

            if (ACCESS_TOKEN_COOKIE.equals(
                    cookie.getName()
            )) {

                return cookie.getValue();
            }
        }

        return null;
    }

    private void authenticateUser(
            final String jwt,
            final HttpServletRequest request
    ) {

        final String username =
                jwtService.extractUsername(jwt);

        if (username == null) {

            log.debug(
                    "JWT did not contain a valid subject"
            );

            return;
        }

        if (SecurityContextHolder
                .getContext()
                .getAuthentication() != null) {

            return;
        }

        final UserDetails userDetails =
                userDetailsService
                        .loadUserByUsername(username);

        if (!(userDetails instanceof User user)) {

            log.debug(
                    "JWT authentication rejected because authenticated principal is not a User"
            );

            return;
        }

        if (!user.isEnabled()) {

            log.warn(
                    "JWT authentication rejected because user is disabled. User: {}",
                    user.getId()
            );

            return;
        }

        if (user.isAccountLocked()) {

            log.warn(
                    "JWT authentication rejected because user is permanently locked. User: {}",
                    user.getId()
            );

            return;
        }

        final LocalDateTime lockedUntil =
                user.getTemporaryLockedUntil();

        if (lockedUntil != null
                && lockedUntil.isAfter(
                LocalDateTime.now()
        )) {

            log.warn(
                    "JWT authentication rejected because temporary account lock is active. User: {}, Locked until: {}",
                    user.getId(),
                    lockedUntil
            );

            return;
        }

        if (!jwtService.isTokenValid(
                jwt,
                userDetails.getUsername()
        )) {

            log.debug(
                    "JWT validation failed for authenticated user"
            );

            return;
        }

        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        log.debug(
                "JWT authentication successful for user: {}",
                username
        );
    }
}