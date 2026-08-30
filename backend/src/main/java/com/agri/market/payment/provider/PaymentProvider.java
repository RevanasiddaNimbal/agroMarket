package com.agri.market.payment.provider;

import com.agri.market.payment.entity.PaymentMethod;

import java.math.BigDecimal;

public interface PaymentProvider {

    PaymentProviderResponse processPayment(
            String orderId,
            BigDecimal amount,
            PaymentMethod paymentMethod
    );

    PaymentProviderResponse processRefund(
            String paymentId,
            BigDecimal amount
    );
}