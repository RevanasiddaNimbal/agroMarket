package com.agri.market.auth.service;

import com.agri.market.auth.dto.AuthenticationResponse;
import com.agri.market.auth.dto.ClientInfo;
import com.agri.market.auth.dto.RefreshTokenRequest;
import com.agri.market.user.entity.User;

public interface AuthenticationTokenService {

    AuthenticationResponse createAuthenticationSession(
            User user,
            ClientInfo clientInfo
    );

    AuthenticationResponse refreshAuthenticationSession(
            RefreshTokenRequest request
    );
}