package com.agri.market.support;

import com.agri.market.user.dto.ChangePasswordRequestDto;

public final class ChangePasswordRequestTestFactory {

    private ChangePasswordRequestTestFactory() {
    }

    public static ChangePasswordRequestDto validRequest() {
        return ChangePasswordRequestDto.builder()
                .currentPassword("Old@Password123")
                .newPassword("New@Password123")
                .confirmNewPassword("New@Password123")
                .build();
    }
}

