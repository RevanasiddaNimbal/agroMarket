package com.agri.market.password.dto;

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
@Schema(description = "Request payload used to reset a user's password")
public class ResetPasswordRequest {

    @NotBlank(message = "VALIDATION.RESET_PASSWORD.TOKEN.BLANK")
    @Schema(
            description = "Password reset token received through the user's email",
            example = "dGhpcy1pcy1hLXNlY3VyZS10b2tlbg"
    )
    private String token;

    @NotBlank(message = "VALIDATION.RESET_PASSWORD.PASSWORD.BLANK")
    @Size(
            min = 8,
            max = 72,
            message = "VALIDATION.RESET_PASSWORD.PASSWORD.SIZE"
    )
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$",
            message = "VALIDATION.RESET_PASSWORD.PASSWORD.WEAK"
    )
    @Schema(
            description = "New password for the user account",
            example = "N@wPassword123",
            minLength = 8,
            maxLength = 72
    )
    private String newPassword;

    @NotBlank(
            message = "VALIDATION.RESET_PASSWORD.CONFIRM_PASSWORD.BLANK"
    )
    @Size(
            min = 8,
            max = 72,
            message = "VALIDATION.RESET_PASSWORD.CONFIRM_PASSWORD.SIZE"
    )
    @Schema(
            description = "Confirmation of the new password",
            example = "N@wPassword123",
            minLength = 8,
            maxLength = 72
    )
    private String confirmPassword;
}