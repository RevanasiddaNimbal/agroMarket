package com.agri.market.security.oauth2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "GitHub user email information")
public class GitHubEmailResponse {

    @Schema(
            description = "Email address associated with the GitHub account",
            example = "user@example.com"
    )
    private String email;

    @Schema(
            description = "Whether this email is the user's primary GitHub email",
            example = "true"
    )
    private boolean primary;

    @Schema(
            description = "Whether GitHub has verified this email",
            example = "true"
    )
    private boolean verified;

    @Schema(
            description = "GitHub email visibility",
            example = "private"
    )
    private String visibility;
}