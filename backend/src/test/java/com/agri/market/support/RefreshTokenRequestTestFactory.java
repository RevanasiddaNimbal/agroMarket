package com.agri.market.support;

import com.agri.market.auth.dto.RefreshTokenRequest;

public final class RefreshTokenRequestTestFactory {

    private RefreshTokenRequestTestFactory() {
    }

    public static RefreshTokenRequest validRequest() {
        return RefreshTokenRequest.builder()
                .refreshToken("refresh-token")
                .build();
    }
}

