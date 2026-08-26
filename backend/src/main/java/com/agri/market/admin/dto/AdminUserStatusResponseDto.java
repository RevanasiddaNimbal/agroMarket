package com.agri.market.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Current account status returned after an administrator performs a user status action")
public class AdminUserStatusResponseDto {

    @Schema(
            description = "Unique identifier of the user",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private String userId;

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
            description = "Message describing the result of the administrative action",
            example = "User account activated successfully"
    )
    private String message;
}