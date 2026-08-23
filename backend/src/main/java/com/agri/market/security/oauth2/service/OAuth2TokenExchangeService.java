package com.agri.market.security.oauth2.service;

import com.agri.market.auth.dto.AuthenticationResult;
import com.agri.market.auth.dto.ClientInfo;

public interface OAuth2TokenExchangeService {

    AuthenticationResult exchange(
            String code,
            ClientInfo clientInfo
    );
}