package com.agri.market.security.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "GitHub user profile response")
public class GitHubUserResponse {

    @Schema(
            description = "Unique GitHub user ID",
            example = "123456789"
    )
    private Long id;

    @Schema(
            description = "GitHub username",
            example = "revanasidda"
    )
    private String login;

    @Schema(
            description = "GitHub user's full name",
            example = "Revanasidda Nimbal"
    )
    private String name;

    @Schema(
            description = "GitHub user's public email address",
            example = "user@example.com"
    )
    private String email;

    @JsonProperty("avatar_url")
    @Schema(
            description = "URL of the user's GitHub profile picture",
            example = "https://avatars.githubusercontent.com/u/123456789"
    )
    private String avatarUrl;
}