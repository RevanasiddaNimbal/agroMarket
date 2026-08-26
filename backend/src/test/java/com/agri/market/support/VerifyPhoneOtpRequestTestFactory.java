package com.agri.market.support;

import com.agri.market.user.dto.VerifyPhoneOtpRequestDto;

public final class VerifyPhoneOtpRequestTestFactory {

    private VerifyPhoneOtpRequestTestFactory() {
    }

    public static VerifyPhoneOtpRequestDto validRequest() {
        return VerifyPhoneOtpRequestDto.builder()
                .phoneNumber("9876543210")
                .otp("123456")
                .build();
    }

    public static VerifyPhoneOtpRequestDto invalidRequest() {
        return VerifyPhoneOtpRequestDto.builder()
                .phoneNumber("")
                .otp("")
                .build();
    }
}