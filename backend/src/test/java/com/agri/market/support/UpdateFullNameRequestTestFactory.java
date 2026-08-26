package com.agri.market.support;

import com.agri.market.user.dto.UpdateFullNameRequestDto;

public final class UpdateFullNameRequestTestFactory {

    private UpdateFullNameRequestTestFactory() {
    }

    public static UpdateFullNameRequestDto validRequest() {

        return UpdateFullNameRequestDto.builder()
                .fullName("Updated Name")
                .build();
    }

    public static UpdateFullNameRequestDto invalidRequest() {

        return UpdateFullNameRequestDto.builder()
                .fullName("")
                .build();
    }
}