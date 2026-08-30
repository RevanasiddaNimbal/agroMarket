package com.agri.market.admin.service;

import com.agri.market.payment.dto.PaymentResponseDto;
import com.agri.market.payment.entity.PaymentStatus;

import java.util.List;

public interface AdminPaymentService {

    List<PaymentResponseDto> getAllPayments();

    PaymentResponseDto getPayment(
            String paymentId
    );

    List<PaymentResponseDto> getPaymentsByStatus(
            PaymentStatus status
    );
}