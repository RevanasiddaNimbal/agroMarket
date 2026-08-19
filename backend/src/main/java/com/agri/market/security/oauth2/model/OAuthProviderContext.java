package com.agri.market.security.oauth2.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@Builder
public class OAuthProviderContext {

    private final OAuth2User oauth2User;

    private final OAuth2AuthorizedClient authorizedClient;
}