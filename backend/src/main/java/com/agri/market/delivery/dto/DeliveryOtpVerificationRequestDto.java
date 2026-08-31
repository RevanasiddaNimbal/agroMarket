package com.agri.market.delivery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload used to verify a delivery OTP")
public class DeliveryOtpVerificationRequestDto {

    @NotBlank(
            message = "VALIDATION.DELIVERY.ORDER_ID.NOT_BLANK"
    )
    @JsonProperty("order_id")
    @Schema(
            description = "Order identifier for the delivery",
            example = "38b5459e-f2de-4f86-be60-f0bd926020ec",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String orderId;

    @NotBlank(
            message = "VALIDATION.DELIVERY.OTP.NOT_BLANK"
    )
    @Pattern(
            regexp = "\\d{6}",
            message = "VALIDATION.DELIVERY.OTP.INVALID"
    )
    @JsonProperty("otp")
    @Schema(
            description = "Six digit delivery verification OTP",
            example = "482915",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String otp;
}
