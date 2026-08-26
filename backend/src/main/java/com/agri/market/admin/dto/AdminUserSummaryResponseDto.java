package com.agri.market.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Summary information of a user displayed in the admin user list")
public class AdminUserSummaryResponseDto {

    @Schema(
            description = "Unique identifier of the user",
            example = "550e8400-e29b-41d4-a716-446655440000"
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
            example = "+919876543210"
    )
    private String phoneNumber;

    @Schema(
            description = "Whether the user's email address has been verified",
            example = "true"
    )
    private boolean emailVerified;

    @Schema(
            description = "Whether the user's phone number has been verified",
            example = "true"
    )
    private boolean phoneVerified;

    @Schema(
            description = "Whether the user account is enabled",
            example = "true"
    )
    private boolean enabled;

    @Schema(
            description = "Whether the user account is locked",
            example = "false"
    )
    private boolean accountLocked;

    @Schema(
            description = "Roles assigned to the user",
            example = "[\"USER\"]"
    )
    private List<String> roles;

    @Schema(
            description = "Date and time when the user account was created",
            example = "2026-08-26T10:30:00"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Date and time when the user account was last updated",
            example = "2026-08-26T12:45:00"
    )
    private LocalDateTime updatedAt;
}