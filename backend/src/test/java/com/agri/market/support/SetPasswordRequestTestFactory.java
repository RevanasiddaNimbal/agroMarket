package com.agri.market.support;

import com.agri.market.user.dto.SetPasswordRequestDto;

public final class SetPasswordRequestTestFactory {

    private SetPasswordRequestTestFactory() {
    }

    public static SetPasswordRequestDto validRequest() {
        return SetPasswordRequestDto.builder()
                .newPassword("New@Password123")
                .confirmNewPassword("New@Password123")
                .build();
    }

    public static SetPasswordRequestDto invalidRequest() {
        return SetPasswordRequestDto.builder()
                .newPassword("")
                .confirmNewPassword("")
                .build();
    }
}