package com.agri.market.auth.service;

import com.agri.market.auth.properties.AuthenticationCookieProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthenticationCookieServiceImpl
        implements AuthenticationCookieService {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final AuthenticationCookieProperties properties;

    @Override
    public void addAuthenticationCookies(
            final HttpServletResponse response,
            final String accessToken,
            final String refreshToken
    ) {

        final ResponseCookie accessCookie =
                createCookie(
                        ACCESS_TOKEN_COOKIE,
                        accessToken,
                        properties.getAccessTokenMaxAge()
                );

        final ResponseCookie refreshCookie =
                createCookie(
                        REFRESH_TOKEN_COOKIE,
                        refreshToken,
                        properties.getRefreshTokenMaxAge()
                );

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                accessCookie.toString()
        );

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                refreshCookie.toString()
        );
    }

    @Override
    public String getRefreshToken(
            final HttpServletRequest request
    ) {

        final Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (final Cookie cookie : cookies) {

            if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    @Override
    public void clearAuthenticationCookies(
            final HttpServletResponse response
    ) {

        final ResponseCookie accessCookie =
                createCookie(
                        ACCESS_TOKEN_COOKIE,
                        "",
                        Duration.ZERO
                );

        final ResponseCookie refreshCookie =
                createCookie(
                        REFRESH_TOKEN_COOKIE,
                        "",
                        Duration.ZERO
                );

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                accessCookie.toString()
        );

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                refreshCookie.toString()
        );
    }

    private ResponseCookie createCookie(
            final String name,
            final String value,
            final Duration maxAge
    ) {

        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(properties.isSecure())
                .sameSite(properties.getSameSite())
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}