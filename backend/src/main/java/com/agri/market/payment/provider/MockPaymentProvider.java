package com.agri.market.payment.provider;

import com.agri.market.payment.entity.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Slf4j
public class MockPaymentProvider
        implements PaymentProvider {

    private static final String PROVIDER = "MOCK";

    @Override
    public PaymentProviderResponse processPayment(
            final String orderId,
            final BigDecimal amount,
            final PaymentMethod paymentMethod
    ) {

        log.info(
                "Processing mock payment. Order: {}, Amount: {}, Method: {}",
                orderId,
                amount,
                paymentMethod
        );

        final String paymentId =
                "MOCK-PAY-" + UUID.randomUUID();

        final String transactionId =
                "MOCK-TXN-" + UUID.randomUUID();

        return PaymentProviderResponse.builder()
                .successful(true)
                .provider(PROVIDER)
                .providerPaymentId(paymentId)
                .providerTransactionId(transactionId)
                .message("Mock payment successful")
                .build();
    }

    @Override
    public PaymentProviderResponse processRefund(
            final String paymentId,
            final BigDecimal amount
    ) {

        log.info(
                "Processing mock refund. Payment: {}, Amount: {}",
                paymentId,
                amount
        );

        final String transactionId =
                "MOCK-REFUND-" + UUID.randomUUID();

        return PaymentProviderResponse.builder()
                .successful(true)
                .provider(PROVIDER)
                .providerPaymentId(paymentId)
                .providerTransactionId(transactionId)
                .message("Mock refund successful")
                .build();
    }
}