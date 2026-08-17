package com.agri.market.support;

import com.agri.market.auth.dto.AuthenticationRequest;

public final class AuthenticationRequestTestFactory {

    private AuthenticationRequestTestFactory() {
    }

    public static AuthenticationRequest validRequest() {
        return AuthenticationRequest.builder()
                .email("REVANASIDDA@GMAIL.COM")
                .password("P@ssword123")
                .build();
    }
}

