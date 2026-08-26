package com.agri.market.support;

import com.agri.market.user.dto.UpdateProfilePictureRequestDto;

public final class UpdateProfilePictureRequestTestFactory {

    private UpdateProfilePictureRequestTestFactory() {
    }

    public static UpdateProfilePictureRequestDto validRequest() {

        return UpdateProfilePictureRequestDto.builder()
                .profilePictureUrl(
                        "https://example.com/profile.jpg"
                )
                .build();
    }

    public static UpdateProfilePictureRequestDto invalidRequest() {

        return UpdateProfilePictureRequestDto.builder()
                .profilePictureUrl("")
                .build();
    }
}