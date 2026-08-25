package com.agri.market.user.mapper;

import com.agri.market.address.mapper.AddressMapper;
import com.agri.market.user.dto.ProfileUpdateRequestDto;
import com.agri.market.user.dto.UserProfileResponseDto;
import com.agri.market.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final AddressMapper addressMapper;

    public void updateUserFromProfileRequest(
            final ProfileUpdateRequestDto requestDto,
            final User user
    ) {

        if (StringUtils.hasText(requestDto.getFullName())
                && !user.getFullName().equals(requestDto.getFullName())) {
            user.setFullName(requestDto.getFullName());
        }

        if (StringUtils.hasText(requestDto.getProfilePictureUrl())) {
            user.setProfilePictureUrl(requestDto.getProfilePictureUrl());
        }
    }

    public UserProfileResponseDto toUserProfileResponseDto(
            final User user
    ) {

        return UserProfileResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .emailVerified(user.isEmailVerified())
                .phoneVerified(user.isPhoneVerified())
                .profilePictureUrl(user.getProfilePictureUrl())
                .addresses(user.getAddresses()
                        .stream()
                        .map(addressMapper::toResponse)
                        .toList())
                .build();
    }
}