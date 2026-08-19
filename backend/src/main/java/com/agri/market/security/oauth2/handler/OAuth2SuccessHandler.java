package com.agri.market.security.oauth2.handler;

import com.agri.market.security.oauth2.model.OAuthProviderContext;
import com.agri.market.security.oauth2.service.OAuth2AuthenticationService;
import com.agri.market.security.oauth2.service.OAuthLoginCodeService;
import com.agri.market.user.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2AuthenticationService
            oauth2AuthenticationService;

    private final OAuthLoginCodeService
            oauthLoginCodeService;

    private final OAuth2AuthorizedClientService
            authorizedClientService;

    @Value("${app.frontend.oauth2-success-url}")
    private String oauth2SuccessUrl;

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) throws IOException, ServletException {

        log.info(
                "OAuth2 authentication succeeded"
        );

        if (!(authentication
                instanceof OAuth2AuthenticationToken oauth2Authentication)) {

            log.error(
                    "OAuth2 authentication failed: unexpected authentication type: {}",
                    authentication.getClass().getName()
            );

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid OAuth authentication"
            );

            return;
        }

        final String registrationId =
                oauth2Authentication
                        .getAuthorizedClientRegistrationId();

        final OAuth2User oauth2User =
                oauth2Authentication.getPrincipal();

        log.info(
                "Processing OAuth2 authentication for provider: {}",
                registrationId
        );

        final OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        registrationId,
                        oauth2Authentication.getName()
                );

        if (authorizedClient == null) {

            log.error(
                    "OAuth2 authorized client not found for provider: {}",
                    registrationId
            );

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "OAuth authorization client not found"
            );

            return;
        }

        log.debug(
                "OAuth2 authorized client successfully loaded for provider: {}",
                registrationId
        );

        final OAuthProviderContext context =
                OAuthProviderContext.builder()
                        .oauth2User(oauth2User)
                        .authorizedClient(authorizedClient)
                        .build();

        final User user =
                oauth2AuthenticationService.authenticate(
                        registrationId,
                        context
                );

        log.info(
                "OAuth2 user authentication completed successfully for provider: {}",
                registrationId
        );

        final String code =
                oauthLoginCodeService.createCode(user);

        log.debug(
                "OAuth2 login code generated for user: {}",
                user.getId()
        );

        final String redirectUrl =
                oauth2SuccessUrl
                        + "?code="
                        + code;

        clearAuthenticationAttributes(request);

        log.info(
                "OAuth2 authentication flow completed for provider: {}. " +
                        "Redirecting authenticated user to frontend",
                registrationId
        );

        getRedirectStrategy().sendRedirect(
                request,
                response,
                redirectUrl
        );
    }
}