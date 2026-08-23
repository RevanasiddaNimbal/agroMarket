package com.agri.market.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationCookieService {

    void addAuthenticationCookies(
            HttpServletResponse response,
            String accessToken,
            String refreshToken
    );

    String getRefreshToken(
            HttpServletRequest request
    );

    void clearAuthenticationCookies(
            HttpServletResponse response
    );
}