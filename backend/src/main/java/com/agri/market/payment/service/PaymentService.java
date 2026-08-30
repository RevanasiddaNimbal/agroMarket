package com.agri.market.payment.service;

import com.agri.market.payment.dto.PaymentRequestDto;
import com.agri.market.payment.dto.PaymentResponseDto;
import com.agri.market.payment.dto.RefundResponseDto;

public interface PaymentService {

    PaymentResponseDto processPayment(
            String orderId,
            String userId,
            PaymentRequestDto request
    );

    RefundResponseDto refundPayment(
            String orderId,
            String userId
    );

    PaymentResponseDto getPayment(
            String orderId,
            String userId
    );
}