package com.agri.market.support;

import com.agri.market.user.dto.ProfileUpdateRequestDto;

public final class ProfileUpdateRequestTestFactory {

    private ProfileUpdateRequestTestFactory() {
    }

    public static ProfileUpdateRequestDto validRequest() {
        return ProfileUpdateRequestDto.builder()
                .fullName("Updated Name")
                .profilePictureUrl("https://example.com/profile.jpg")
                .build();
    }
}

