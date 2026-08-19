package com.agri.market.security.oauth2.service;

import com.agri.market.auth.dto.AuthenticationResponse;
import com.agri.market.auth.dto.ClientInfo;
import com.agri.market.auth.service.AuthenticationTokenService;
import com.agri.market.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2TokenExchangeServiceImpl
        implements OAuth2TokenExchangeService {

    private final OAuthLoginCodeService oauthLoginCodeService;
    private final AuthenticationTokenService authenticationTokenService;

    @Override
    @Transactional
    public AuthenticationResponse exchange(
            final String code,
            final ClientInfo clientInfo
    ) {

        log.debug(
                "OAuth token exchange requested"
        );

        final User user =
                oauthLoginCodeService.exchangeCode(code);

        log.info(
                "OAuth login code validated for user: {}",
                user.getId()
        );

        final AuthenticationResponse response =
                authenticationTokenService
                        .createAuthenticationSession(
                                user,
                                clientInfo
                        );

        log.info(
                "OAuth authentication session created successfully for user: {}",
                user.getId()
        );

        return response;
    }
}