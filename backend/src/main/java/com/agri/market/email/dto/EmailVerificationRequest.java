package com.agri.market.email.dto;

import com.agri.market.validation.annotation.NonDisposableEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload used to verify email addresses")
public class EmailVerificationRequest {

    @NotBlank(message = "VALIDATION.EMAIL.VERIFICATION.EMAIL.BLANK")
    @Email(message = "VALIDATION.EMAIL.VERIFICATION.EMAIL.FORMAT")
    @Size(
            max = 254,
            message = "VALIDATION.EMAIL.VERIFICATION.EMAIL.SIZE"
    )
    @NonDisposableEmail(
            message = "VALIDATION.EMAIL.VERIFICATION.EMAIL.NON_DISPOSABLE"
    )
    @Schema(
            description = "User's email address",
            example = "revanasidda@gmail.com",
            maxLength = 254
    )
    private String email;
}
