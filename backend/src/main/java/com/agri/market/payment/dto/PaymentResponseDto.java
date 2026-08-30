package com.agri.market.payment.dto;

import com.agri.market.payment.entity.PaymentMethod;
import com.agri.market.payment.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment response")
public class PaymentResponseDto {

    @Schema(
            description = "Payment identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0"
    )
    private String id;

    @Schema(
            description = "Order identifier",
            example = "38b5459e-f2de-4f86-be60-f0bd926020ec"
    )
    private String orderId;

    @Schema(
            description = "Payment amount",
            example = "1500.00"
    )
    private BigDecimal amount;

    @Schema(
            description = "Payment method",
            example = "UPI"
    )
    private PaymentMethod paymentMethod;

    @Schema(
            description = "Payment status",
            example = "SUCCESS"
    )
    private PaymentStatus status;

    @Schema(
            description = "Payment provider",
            example = "MOCK"
    )
    private String provider;

    @Schema(
            description = "Provider payment identifier",
            example = "MOCK-PAY-12345"
    )
    private String providerPaymentId;

    @Schema(
            description = "Payment completion time",
            example = "2026-08-30T10:30:00"
    )
    private LocalDateTime paidAt;

    @Schema(
            description = "Refund completion time",
            example = "2026-08-30T11:30:00"
    )
    private LocalDateTime refundedAt;
}