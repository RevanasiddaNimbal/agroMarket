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
@Schema(description = "Request for sending an OTP to a phone number")
public class SendPhoneOtpRequestDto {

    @NotBlank(
            message = "VALIDATION.PHONE.SEND_OTP.PHONE_NUMBER.BLANK"
    )
    @Size(
            min = 10,
            max = 15,
            message = "VALIDATION.PHONE.SEND_OTP.PHONE_NUMBER.SIZE"
    )
    @Pattern(
            regexp = "^\\+?[1-9]\\d{9,14}$",
            message = "VALIDATION.PHONE.SEND_OTP.PHONE_NUMBER.FORMAT"
    )
    @Schema(
            description = "Phone number to be verified",
            example = "+919876543210",
            minLength = 10,
            maxLength = 15
    )
    private String phoneNumber;
}