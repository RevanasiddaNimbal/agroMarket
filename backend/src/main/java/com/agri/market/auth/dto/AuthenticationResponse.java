package com.agri.market.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response")
public class AuthenticationResponse {
    @JsonProperty("message")
    @Schema(
            description = "Authentication operation result message",
            example = "Authentication successful"
    )
    private String message;

    @JsonProperty("has_password")
    @Schema(
            description = "Indicates whether the user has a password configured",
            example = "true"
    )
    private boolean hasPassword;
    
}