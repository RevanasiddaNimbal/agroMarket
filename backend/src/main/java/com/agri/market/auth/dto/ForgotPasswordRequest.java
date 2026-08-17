package com.agri.market.auth.dto;

import com.agri.market.validation.annotation.NonDisposableEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload used to initiate password recovery")
public class ForgotPasswordRequest {

    @NotBlank(message = "VALIDATION.FORGOT_PASSWORD.EMAIL.BLANK")
    @Email(message = "VALIDATION.FORGOT_PASSWORD.EMAIL.FORMAT")
    @Size(
            max = 254,
            message = "VALIDATION.FORGOT_PASSWORD.EMAIL.SIZE"
    )
    @NonDisposableEmail(
            message = "VALIDATION.FORGOT_PASSWORD.EMAIL.NON_DISPOSABLE"
    )
    @Schema(
            description = "Email address associated with the user account",
            example = "revanasidda@gmail.com",
            maxLength = 254
    )
    private String email;
}