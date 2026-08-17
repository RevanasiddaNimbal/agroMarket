package com.agri.market.auth.controller;

import com.agri.market.auth.dto.*;
import com.agri.market.auth.service.AuthenticationService;
import com.agri.market.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Authentication",
        description = "APIs for user authentication and session management"
)
public class AuthenticationController {

    private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String UNKNOWN = "Unknown";

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and authenticates the user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid registration data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email or phone number already exists"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody final RegistrationRequest request,
            final HttpServletRequest httpRequest
    ) {

        log.info("Registration request received");

        final ClientInfo clientInfo =
                buildClientInfo(httpRequest);

        final RegistrationResponse response =
                authenticationService.register(
                        request,
                        clientInfo
                );

        log.info("User registered successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user and creates an authentication session."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid login request"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody final AuthenticationRequest request,
            final HttpServletRequest httpRequest
    ) {

        log.info("Authentication request received");

        final ClientInfo clientInfo =
                buildClientInfo(httpRequest);

        final AuthenticationResponse response =
                authenticationService.login(
                        request,
                        clientInfo
                );

        log.info("User authenticated successfully");

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Refresh authentication tokens",
            description = "Rotates the refresh token and returns a new token pair."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tokens refreshed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid refresh token request"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token"
            )
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @Valid @RequestBody final RefreshTokenRequest request
    ) {

        log.info("Token refresh request received");

        final AuthenticationResponse response =
                authenticationService.refreshToken(request);

        log.info("Authentication tokens refreshed successfully");

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Logout current session",
            description = "Revokes the refresh-token session associated with the supplied refresh token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User logged out successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid refresh token request"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token"
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody final RefreshTokenRequest request
    ) {

        log.info("Logout request received");

        authenticationService.logout(
                request.getRefreshToken()
        );

        log.info("User logged out successfully");

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Logout from all devices",
            description = "Revokes all active authentication sessions belonging to the current user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User logged out from all devices successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(
            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "Logout-all request received for user: {}",
                user.getId()
        );

        authenticationService.logoutAll(
                user.getId()
        );

        log.info(
                "User logged out from all devices successfully: {}",
                user.getId()
        );

        return ResponseEntity.noContent().build();
    }

    private ClientInfo buildClientInfo(
            final HttpServletRequest request
    ) {

        return new ClientInfo(
                resolveDeviceName(request),
                resolveIpAddress(request)
        );
    }

    private String resolveDeviceName(
            final HttpServletRequest request
    ) {

        final String userAgent =
                request.getHeader(USER_AGENT_HEADER);

        if (userAgent == null || userAgent.isBlank()) {
            return UNKNOWN;
        }

        return userAgent;
    }

    private String resolveIpAddress(
            final HttpServletRequest request
    ) {

        final String forwardedFor =
                request.getHeader(FORWARDED_FOR_HEADER);

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        final String remoteAddress =
                request.getRemoteAddr();

        return remoteAddress != null
                ? remoteAddress
                : UNKNOWN;
    }
}