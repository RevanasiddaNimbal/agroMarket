package com.agri.market.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for uploading the authenticated user's profile picture")
public class UpdateProfilePictureRequestDto {

    @NotNull(
            message = "VALIDATION.UPDATE_PROFILE_PICTURE.PROFILE_PICTURE.BLANK"
    )
    @Schema(
            description = "Profile picture image file",
            type = "string",
            format = "binary"
    )
    private MultipartFile profilePicture;
}