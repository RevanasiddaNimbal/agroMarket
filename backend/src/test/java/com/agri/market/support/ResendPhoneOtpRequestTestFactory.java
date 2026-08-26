package com.agri.market.support;

import com.agri.market.user.dto.ResendPhoneOtpRequestDto;

public final class ResendPhoneOtpRequestTestFactory {

    private ResendPhoneOtpRequestTestFactory() {
    }

    public static ResendPhoneOtpRequestDto validRequest() {
        return ResendPhoneOtpRequestDto.builder()
                .phoneNumber("9876543210")
                .build();
    }

    public static ResendPhoneOtpRequestDto invalidRequest() {
        return ResendPhoneOtpRequestDto.builder()
                .phoneNumber("")
                .build();
    }
}