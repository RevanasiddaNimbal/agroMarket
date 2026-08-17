package com.agri.market.auth.dto;

import com.agri.market.validation.annotation.NonDisposableEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload used to register a new user")
public class RegistrationRequest {

    @NotBlank(message = "VALIDATION.REGISTRATION.FULL_NAME.BLANK")
    @Size(
            min = 2,
            max = 100,
            message = "VALIDATION.REGISTRATION.FULL_NAME.SIZE"
    )
    @Pattern(
            regexp = "^[\\p{L} '-]+$",
            message = "VALIDATION.REGISTRATION.FULL_NAME.PATTERN"
    )
    @Schema(
            description = "User's full name",
            example = "Revanasidda Nimbal",
            minLength = 2,
            maxLength = 100
    )
    private String fullName;

    @NotBlank(message = "VALIDATION.REGISTRATION.EMAIL.BLANK")
    @Email(message = "VALIDATION.REGISTRATION.EMAIL.FORMAT")
    @Size(
            max = 254,
            message = "VALIDATION.REGISTRATION.EMAIL.SIZE"
    )
    @NonDisposableEmail(
            message = "VALIDATION.REGISTRATION.EMAIL.NON_DISPOSABLE"
    )
    @Schema(
            description = "User's email address",
            example = "revanasidda@gmail.com",
            maxLength = 254
    )
    private String email;

    @NotBlank(message = "VALIDATION.REGISTRATION.PHONE.BLANK")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{9,14}$",
            message = "VALIDATION.REGISTRATION.PHONE.FORMAT"
    )
    @Schema(
            description = "User's phone number in international format",
            example = "+919876543210"
    )
    private String phoneNumber;

    @NotBlank(message = "VALIDATION.REGISTRATION.PASSWORD.BLANK")
    @Size(
            min = 8,
            max = 72,
            message = "VALIDATION.REGISTRATION.PASSWORD.SIZE"
    )
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$",
            message = "VALIDATION.REGISTRATION.PASSWORD.WEAK"
    )
    @Schema(
            description = "User's password",
            example = "P@ssword123",
            minLength = 8,
            maxLength = 72
    )
    private String password;

    @NotBlank(message = "VALIDATION.REGISTRATION.CONFIRM_PASSWORD.BLANK")
    @Size(
            min = 8,
            max = 72,
            message = "VALIDATION.REGISTRATION.CONFIRM_PASSWORD.SIZE"
    )
    @Schema(
            description = "Confirmation of the user's password",
            example = "P@ssword123",
            minLength = 8,
            maxLength = 72
    )
    private String confirmPassword;
}