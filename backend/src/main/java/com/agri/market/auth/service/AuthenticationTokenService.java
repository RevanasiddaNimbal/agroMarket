package com.agri.market.auth.service;

import com.agri.market.auth.dto.AuthenticationResult;
import com.agri.market.auth.dto.ClientInfo;
import com.agri.market.auth.dto.RefreshTokenRequest;
import com.agri.market.user.entity.User;

public interface AuthenticationTokenService {

    AuthenticationResult createAuthenticationSession(
            User user,
            ClientInfo clientInfo
    );

    AuthenticationResult refreshAuthenticationSession(
            RefreshTokenRequest request
    );
}