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
@Schema(description = "Request for updating the authenticated user's profile picture")
public class UpdateProfilePictureRequestDto {

    @NotBlank(
            message = "VALIDATION.UPDATE_PROFILE_PICTURE.PROFILE_PICTURE_URL.BLANK"
    )
    @Size(
            max = 500,
            message = "VALIDATION.UPDATE_PROFILE_PICTURE.PROFILE_PICTURE_URL.SIZE"
    )
    @Pattern(
            regexp = "^https?://.+$",
            message = "VALIDATION.UPDATE_PROFILE_PICTURE.PROFILE_PICTURE_URL.FORMAT"
    )
    @Schema(
            description = "New profile picture URL",
            example = "https://example.com/profile.jpg",
            maxLength = 500
    )
    private String profilePictureUrl;
}