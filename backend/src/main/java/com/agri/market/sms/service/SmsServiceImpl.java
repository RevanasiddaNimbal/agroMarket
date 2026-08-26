package com.agri.market.sms.service;

import com.agri.market.sms.client.SmsProviderClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final SmsProviderClient smsProviderClient;

    @Override
    public void sendOtp(final String phoneNumber) {

        log.info("Sending OTP to phone number");

//        smsProviderClient.sendOtp(phoneNumber);

        log.info("OTP sent successfully");
    }

    @Override
    public void verifyOtp(
            final String phoneNumber,
            final String otp
    ) {

        log.info("Verifying OTP for phone number");

//        smsProviderClient.verifyOtp(
//                phoneNumber,
//                otp
//        );

        log.info("OTP verified successfully");
    }

    @Override
    public void resendOtp(final String phoneNumber) {

        log.info("Resending OTP to phone number");

//        smsProviderClient.resendOtp(phoneNumber);

        log.info("OTP resent successfully");
    }
}