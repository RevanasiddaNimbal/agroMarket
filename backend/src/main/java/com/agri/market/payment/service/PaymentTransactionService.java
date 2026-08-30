package com.agri.market.payment.service;

import com.agri.market.payment.dto.PaymentTransactionResponseDto;

import java.util.List;

public interface PaymentTransactionService {

    List<PaymentTransactionResponseDto> getMyTransactions(
            String userId
    );
    

    List<PaymentTransactionResponseDto> getPaymentTransactions(
            String paymentId,
            String userId
    );
}