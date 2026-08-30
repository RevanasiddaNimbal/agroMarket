package com.agri.market.payment.mapper;

import com.agri.market.payment.dto.PaymentResponseDto;
import com.agri.market.payment.dto.RefundResponseDto;
import com.agri.market.payment.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponseDto toResponseDto(
            final Payment payment
    ) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .provider(payment.getProvider())
                .providerPaymentId(payment.getProviderPaymentId())
                .paidAt(payment.getPaidAt())
                .refundedAt(payment.getRefundedAt())
                .build();
    }

    public RefundResponseDto toRefundResponseDto(
            final Payment payment
    ) {
        return RefundResponseDto.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .provider(payment.getProvider())
                .refundedAt(payment.getRefundedAt())
                .build();
    }
}