package com.agri.market.delivery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload used to generate a delivery OTP")
public class DeliveryOtpRequestDto {

    @NotBlank(
            message = "VALIDATION.DELIVERY.ORDER_ID.NOT_BLANK"
    )
    @JsonProperty("order_id")
    @Schema(
            description = "Order identifier for which the delivery OTP is requested",
            example = "38b5459e-f2de-4f86-be60-f0bd926020ec",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String orderId;
}

