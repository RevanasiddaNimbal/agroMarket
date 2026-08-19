package com.agri.market.security.oauth2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload used to exchange an OAuth login code for authentication tokens")
public class OAuthCodeExchangeRequest {

    @NotBlank(message = "VALIDATION.OAUTH.CODE.NOT_BLANK")
    @Schema(
            description = "One-time OAuth login code received after successful OAuth authentication",
            example = "X7kP2mN9qR4sT8vW",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String code;
}