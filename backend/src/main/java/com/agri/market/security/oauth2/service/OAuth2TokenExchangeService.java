package com.agri.market.security.oauth2.service;

import com.agri.market.auth.dto.AuthenticationResponse;
import com.agri.market.auth.dto.ClientInfo;

public interface OAuth2TokenExchangeService {

    AuthenticationResponse exchange(
            String code,
            ClientInfo clientInfo
    );
}