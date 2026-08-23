package com.agri.market.auth.service;

import com.agri.market.auth.dto.*;

public interface AuthenticationService {

    RegistrationResponse register(
            RegistrationRequest request,
            ClientInfo clientInfo
    );

    AuthenticationResult login(
            AuthenticationRequest request,
            ClientInfo clientInfo
    );

    AuthenticationResult refreshToken(
            RefreshTokenRequest request
    );

    void logout(String refreshToken);

    void logoutAll(String userId);
}