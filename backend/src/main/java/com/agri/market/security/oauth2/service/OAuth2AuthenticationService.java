package com.agri.market.security.oauth2.service;

import com.agri.market.security.oauth2.model.OAuthProviderContext;
import com.agri.market.user.entity.User;

public interface OAuth2AuthenticationService {

    User authenticate(
            String registrationId,
            OAuthProviderContext context
    );
}