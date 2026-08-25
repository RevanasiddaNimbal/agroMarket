package com.agri.market.user.dto;

import com.agri.market.address.dto.AddressResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authenticated user's complete profile")
public class UserProfileResponseDto {

    @Schema(
            description = "Unique identifier of the user",
            example = "38b5459e-f2de-4f86-be60-f0bd926020ec"
    )
    private String id;

    @Schema(
            description = "Full name of the user",
            example = "Revanasidda Nimbal"
    )
    private String fullName;

    @Schema(
            description = "Email address of the user",
            example = "revanasidda@example.com"
    )
    private String email;

    @Schema(
            description = "Phone number of the user",
            example = "9876543210"
    )
    private String phoneNumber;

    @Schema(
            description = "Whether the user's email address has been verified",
            example = "true"
    )
    private boolean emailVerified;

    @Schema(
            description = "Whether the user's phone number has been verified",
            example = "false"
    )
    private boolean phoneVerified;

    @Schema(
            description = "Profile picture URL",
            example = "https://example.com/profile.jpg"
    )
    private String profilePictureUrl;

    @Schema(
            description = "Addresses associated with the user"
    )
    private List<AddressResponseDto> addresses;
}