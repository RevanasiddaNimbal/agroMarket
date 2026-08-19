package com.agri.market.security.oauth2.strategy;

import com.agri.market.security.oauth2.entity.OAuthProvider;
import com.agri.market.security.oauth2.model.OAuthProviderContext;

public interface OAuthProviderStrategy {

    OAuthProvider getProvider();

    String getProviderUserId(
            OAuthProviderContext context
    );

    String getEmail(
            OAuthProviderContext context
    );

    String getFullName(
            OAuthProviderContext context
    );

    String getProfilePictureUrl(
            OAuthProviderContext context
    );

    boolean isEmailVerified(
            OAuthProviderContext context
    );
}