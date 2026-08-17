package com.agri.market.auth.service;

import com.agri.market.auth.dto.*;

public interface AuthenticationService {

    RegistrationResponse register(
            RegistrationRequest request,
            ClientInfo clientInfo
    );

    AuthenticationResponse login(
            AuthenticationRequest request,
            ClientInfo clientInfo
    );

    AuthenticationResponse refreshToken(
            RefreshTokenRequest request
    );

    void logout(String refreshToken);

    void logoutAll(String userId);
}