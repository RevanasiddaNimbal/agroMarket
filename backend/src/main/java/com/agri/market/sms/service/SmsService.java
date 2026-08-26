package com.agri.market.sms.service;

public interface SmsService {

    void sendOtp(String phoneNumber);

    void verifyOtp(String phoneNumber, String otp);

    void resendOtp(String phoneNumber);
}