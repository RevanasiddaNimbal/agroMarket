package com.agri.market.payment.provider;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentProviderResponse {

    private final boolean successful;

    private final String provider;

    private final String providerPaymentId;

    private final String providerTransactionId;

    private final String message;
}