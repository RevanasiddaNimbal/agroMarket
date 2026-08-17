package com.agri.market.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response containing access and refresh tokens")
public class AuthenticationResponse {

    @JsonProperty("access_token")
    @Schema(
            description = "JWT access token used to authenticate API requests",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private String accessToken;

    @JsonProperty("refresh_token")
    @Schema(
            description = "Token used to obtain a new access token",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private String refreshToken;

    @JsonProperty("token_type")
    @Schema(
            description = "Authentication scheme used with the access token",
            example = "Bearer"
    )
    private String tokenType;
}