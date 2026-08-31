package com.agri.market.delivery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Delivery response")
public class DeliveryResponseDto {

    @JsonProperty("id")
    @Schema(
            description = "Delivery identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0"
    )
    private String id;

    @JsonProperty("order_id")
    @Schema(
            description = "Order identifier associated with the delivery",
            example = "38b5459e-f2de-4f86-be60-f0bd926020ec"
    )
    private String orderId;

    @JsonProperty("otp_verified")
    @Schema(
            description = "Indicates whether the delivery OTP has been successfully verified",
            example = "false"
    )
    private boolean otpVerified;

    @JsonProperty("delivered_at")
    @Schema(
            description = "Date and time when the order was successfully delivered",
            example = "2026-08-30T18:30:00"
    )
    private LocalDateTime deliveredAt;

    @JsonProperty("created_at")
    @Schema(
            description = "Date and time when the delivery record was created",
            example = "2026-08-30T10:30:00"
    )
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(
            description = "Date and time when the delivery record was last updated",
            example = "2026-08-30T18:30:00"
    )
    private LocalDateTime updatedAt;
}

