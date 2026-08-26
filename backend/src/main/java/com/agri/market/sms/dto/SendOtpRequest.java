package com.agri.market.sms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request used to send an OTP to a mobile number")
public class SendOtpRequest {

    @NotBlank(message = "VALIDATION.SMS.PHONE_NUMBER.NOT_BLANK")
    @Pattern(
            regexp = "^\\+[1-9]\\d{7,14}$",
            message = "VALIDATION.SMS.PHONE_NUMBER.FORMAT"
    )
    @Schema(
            description = "Mobile number in international format",
            example = "+919876543210",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String phoneNumber;
}