package com.agri.market.admin.service;

import com.agri.market.payment.dto.PaymentTransactionResponseDto;
import com.agri.market.payment.entity.PaymentStatus;
import com.agri.market.payment.entity.TransactionType;

import java.util.List;

public interface AdminPaymentTransactionService {

    List<PaymentTransactionResponseDto> getAllTransactions();

    List<PaymentTransactionResponseDto> getTransactionsByType(
            TransactionType transactionType
    );

    List<PaymentTransactionResponseDto> getTransactionsByStatus(
            PaymentStatus status
    );

    List<PaymentTransactionResponseDto> getPaymentTransactions(
            String paymentId
    );
}