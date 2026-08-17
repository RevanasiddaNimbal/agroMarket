package com.agri.market.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload used to refresh an access token")
public class RefreshTokenRequest {

    @NotBlank(message = "VALIDATION.AUTHENTICATION.REFRESH_TOKEN.NOT_BLANK")
    @Schema(
            description = "Valid refresh token issued during authentication",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private String refreshToken;
}