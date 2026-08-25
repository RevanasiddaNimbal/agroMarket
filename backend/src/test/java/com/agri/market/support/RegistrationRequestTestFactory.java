package com.agri.market.support;

import com.agri.market.auth.dto.RegistrationRequest;

public final class RegistrationRequestTestFactory {

    private RegistrationRequestTestFactory() {
    }

    public static RegistrationRequest validRequest() {
        return RegistrationRequest.builder()
                .fullName("Revanasidda Nimbal")
                .email("REVANASIDDA@GMAIL.COM")
                .password("P@ssword123")
                .confirmPassword("P@ssword123")
                .build();
    }
}

