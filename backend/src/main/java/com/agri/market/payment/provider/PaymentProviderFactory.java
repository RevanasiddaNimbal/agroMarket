package com.agri.market.payment.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProviderFactory {

    private final MockPaymentProvider mockPaymentProvider;

    public PaymentProvider getProvider() {
        return mockPaymentProvider;
    }
}