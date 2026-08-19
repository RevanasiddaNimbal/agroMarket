package com.agri.market.security.oauth2.strategy;

import com.agri.market.security.oauth2.entity.OAuthProvider;
import com.agri.market.security.oauth2.model.OAuthProviderContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GoogleOAuthStrategy implements OAuthProviderStrategy {

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.GOOGLE;
    }

    @Override
    public String getProviderUserId(
            final OAuthProviderContext context
    ) {
        final OAuth2User oauth2User =
                context.getOauth2User();

        return oauth2User.getAttribute("sub");
    }

    @Override
    public String getEmail(
            final OAuthProviderContext context
    ) {
        final OAuth2User oauth2User =
                context.getOauth2User();

        return oauth2User.getAttribute("email");
    }

    @Override
    public String getFullName(
            final OAuthProviderContext context
    ) {
        final OAuth2User oauth2User =
                context.getOauth2User();

        return oauth2User.getAttribute("name");
    }

    @Override
    public String getProfilePictureUrl(
            final OAuthProviderContext context
    ) {
        final OAuth2User oauth2User =
                context.getOauth2User();

        return oauth2User.getAttribute("picture");
    }

    @Override
    public boolean isEmailVerified(
            final OAuthProviderContext context
    ) {
        final OAuth2User oauth2User =
                context.getOauth2User();

        final Boolean verified =
                oauth2User.getAttribute("email_verified");

        return Boolean.TRUE.equals(verified);
    }
}