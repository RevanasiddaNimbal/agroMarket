package com.agri.market.security.oauth2.controller;

import com.agri.market.auth.dto.AuthenticationResponse;
import com.agri.market.auth.dto.ClientInfo;
import com.agri.market.security.client.ClientInfoResolver;
import com.agri.market.security.oauth2.dto.OAuthCodeExchangeRequest;
import com.agri.market.security.oauth2.service.OAuth2TokenExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/oauth2")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "OAuth2 Authentication",
        description = "APIs for OAuth2 authentication and token exchange"
)
public class OAuth2TokenExchangeController {

    private final OAuth2TokenExchangeService oauth2TokenExchangeService;
    private final ClientInfoResolver clientInfoResolver;

    @Operation(
            summary = "Exchange OAuth2 login code",
            description = "Exchanges a valid one-time OAuth2 login code for application authentication tokens."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OAuth2 login code exchanged successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid, expired or already used OAuth2 login code"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "OAuth2 authentication failed"
            )
    })
    @PostMapping("/exchange")
    public ResponseEntity<AuthenticationResponse> exchange(
            @Valid @RequestBody final OAuthCodeExchangeRequest request,
            final HttpServletRequest httpRequest
    ) {

        log.debug(
                "OAuth2 token exchange request received"
        );

        final ClientInfo clientInfo =
                clientInfoResolver.resolve(httpRequest);

        final AuthenticationResponse response =
                oauth2TokenExchangeService.exchange(
                        request.getCode(),
                        clientInfo
                );

        log.info(
                "OAuth2 token exchange completed successfully"
        );

        return ResponseEntity.ok(response);
    }
}