package com.agri.market.user.mapper;

import com.agri.market.address.mapper.AddressMapper;
import com.agri.market.user.dto.UserProfileResponseDto;
import com.agri.market.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final AddressMapper addressMapper;

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
                .addresses(
                        user.getAddresses()
                                .stream()
                                .map(addressMapper::toResponse)
                                .toList()
                )
                .build();
    }
}