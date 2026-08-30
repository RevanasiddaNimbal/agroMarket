package com.agri.market.payment.dto;

import com.agri.market.payment.entity.PaymentStatus;
import com.agri.market.payment.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment transaction response")
public class PaymentTransactionResponseDto {

    @Schema(
            description = "Transaction identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0"
    )
    private String id;

    @Schema(
            description = "Payment identifier",
            example = "38b5459e-f2de-4f86-be60-f0bd926020ec"
    )
    private String paymentId;

    @Schema(
            description = "Order identifier",
            example = "38b5459e-f2de-4f86-be60-f0bd926020ec"
    )
    private String orderId;

    @Schema(
            description = "Transaction type",
            example = "PAYMENT"
    )
    private TransactionType transactionType;

    @Schema(
            description = "Transaction amount",
            example = "1500.00"
    )
    private BigDecimal amount;

    @Schema(
            description = "Transaction status",
            example = "SUCCESS"
    )
    private PaymentStatus status;

    @Schema(
            description = "Transaction provider",
            example = "MOCK"
    )
    private String provider;

    @Schema(
            description = "Provider transaction identifier",
            example = "MOCK-TXN-12345"
    )
    private String providerTransactionId;

    @Schema(
            description = "Transaction creation time",
            example = "2026-08-30T10:30:00"
    )
    private LocalDateTime createdDate;
}