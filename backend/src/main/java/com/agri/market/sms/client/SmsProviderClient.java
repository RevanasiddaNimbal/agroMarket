package com.agri.market.sms.client;

import com.agri.market.sms.dto.SmsProviderResponse;

public interface SmsProviderClient {

    SmsProviderResponse sendOtp(String phoneNumber);

    SmsProviderResponse verifyOtp(String phoneNumber, String otp);

    SmsProviderResponse resendOtp(String phoneNumber);
}