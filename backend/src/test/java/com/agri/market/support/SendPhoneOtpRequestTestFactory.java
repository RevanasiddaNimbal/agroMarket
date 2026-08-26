package com.agri.market.support;

import com.agri.market.user.dto.SendPhoneOtpRequestDto;

public final class SendPhoneOtpRequestTestFactory {

    private SendPhoneOtpRequestTestFactory() {
    }

    public static SendPhoneOtpRequestDto validRequest() {
        return SendPhoneOtpRequestDto.builder()
                .phoneNumber("9876543210")
                .build();
    }

    public static SendPhoneOtpRequestDto invalidRequest() {
        return SendPhoneOtpRequestDto.builder()
                .phoneNumber("")
                .build();
    }
}