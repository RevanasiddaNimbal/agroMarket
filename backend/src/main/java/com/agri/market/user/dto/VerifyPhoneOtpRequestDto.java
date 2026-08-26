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
@Schema(description = "Request for verifying a phone number using OTP")
public class VerifyPhoneOtpRequestDto {

    @NotBlank(
            message = "VALIDATION.PHONE.VERIFY_OTP.PHONE_NUMBER.BLANK"
    )
    @Size(
            min = 10,
            max = 15,
            message = "VALIDATION.PHONE.VERIFY_OTP.PHONE_NUMBER.SIZE"
    )
    @Pattern(
            regexp = "^\\+?[1-9]\\d{9,14}$",
            message = "VALIDATION.PHONE.VERIFY_OTP.PHONE_NUMBER.FORMAT"
    )
    @Schema(
            description = "Phone number for which the OTP was sent",
            example = "+919876543210",
            minLength = 10,
            maxLength = 15
    )
    private String phoneNumber;

    @NotBlank(
            message = "VALIDATION.PHONE.VERIFY_OTP.OTP.BLANK"
    )
    @Size(
            min = 4,
            max = 8,
            message = "VALIDATION.PHONE.VERIFY_OTP.OTP.SIZE"
    )
    @Pattern(
            regexp = "^\\d+$",
            message = "VALIDATION.PHONE.VERIFY_OTP.OTP.FORMAT"
    )
    @Schema(
            description = "OTP received through SMS",
            example = "123456",
            minLength = 4,
            maxLength = 8
    )
    private String otp;
}