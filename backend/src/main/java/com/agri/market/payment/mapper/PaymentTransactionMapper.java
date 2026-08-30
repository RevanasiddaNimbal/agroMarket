package com.agri.market.payment.mapper;

import com.agri.market.payment.dto.PaymentTransactionResponseDto;
import com.agri.market.payment.entity.PaymentTransaction;
import org.springframework.stereotype.Component;

@Component
public class PaymentTransactionMapper {

    public PaymentTransactionResponseDto toResponseDto(
            final PaymentTransaction transaction
    ) {
        return PaymentTransactionResponseDto.builder()
                .id(transaction.getId())
                .paymentId(transaction.getPayment().getId())
                .orderId(transaction.getPayment().getOrder().getId())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .provider(transaction.getProvider())
                .providerTransactionId(
                        transaction.getProviderTransactionId()
                )
                .createdDate(transaction.getCreatedDate())
                .build();
    }
}