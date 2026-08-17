package com.agri.market.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for updating the authenticated user's profile")
public class ProfileUpdateRequestDto {

    @NotBlank(message = "VALIDATION.PROFILE_UPDATE.FULL_NAME.BLANK")
    @Size(
            min = 2,
            max = 100,
            message = "VALIDATION.PROFILE_UPDATE.FULL_NAME.SIZE"
    )
    @Pattern(
            regexp = "^[\\p{L} '-]+$",
            message = "VALIDATION.PROFILE_UPDATE.FULL_NAME.PATTERN"
    )
    @Schema(
            description = "Full name of the user",
            example = "Revanasidda Nimbal",
            minLength = 2,
            maxLength = 100
    )
    private String fullName;

    @Size(
            max = 500,
            message = "VALIDATION.PROFILE_UPDATE.PROFILE_PICTURE_URL.SIZE"
    )
    @Pattern(
            regexp = "^https?://.+$",
            message = "VALIDATION.PROFILE_UPDATE.PROFILE_PICTURE_URL.FORMAT"
    )
    @Schema(
            description = "URL of the user's profile picture",
            example = "https://example.com/profile.jpg",
            maxLength = 500
    )
    private String profilePictureUrl;
}