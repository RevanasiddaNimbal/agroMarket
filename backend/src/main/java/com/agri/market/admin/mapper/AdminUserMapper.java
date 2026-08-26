package com.agri.market.admin.mapper;

import com.agri.market.admin.dto.AdminUserDetailResponseDto;
import com.agri.market.admin.dto.AdminUserStatusResponseDto;
import com.agri.market.admin.dto.AdminUserSummaryResponseDto;
import com.agri.market.user.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class AdminUserMapper {

    public AdminUserSummaryResponseDto toSummaryResponseDto(
            final User user
    ) {

        return AdminUserSummaryResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .emailVerified(user.isEmailVerified())
                .phoneVerified(user.isPhoneVerified())
                .enabled(user.isEnabled())
                .accountLocked(user.isAccountLocked())
                .roles(
                        CollectionUtils.isEmpty(user.getRoles())
                                ? java.util.List.of()
                                : user.getRoles()
                                .stream()
                                .map(role -> role.getName())
                                .toList()
                )
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public AdminUserDetailResponseDto toDetailResponseDto(
            final User user
    ) {

        return AdminUserDetailResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .emailVerified(user.isEmailVerified())
                .phoneVerified(user.isPhoneVerified())
                .profilePictureUrl(user.getProfilePictureUrl())
                .enabled(user.isEnabled())
                .accountLocked(user.isAccountLocked())
                .roles(
                        CollectionUtils.isEmpty(user.getRoles())
                                ? java.util.List.of()
                                : user.getRoles()
                                .stream()
                                .map(role -> role.getName())
                                .toList()
                )
                .addressCount(
                        user.getAddresses() == null
                                ? 0
                                : user.getAddresses().size()
                )
                .hasPassword(
                        user.getPassword() != null
                                && !user.getPassword().isBlank()
                )
                .credentialsExpired(user.isCredentialsExpired())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .passwordChangedAt(user.getPasswordChangedAt())
                .build();
    }

    public AdminUserStatusResponseDto toStatusResponseDto(
            final User user,
            final String message
    ) {

        return AdminUserStatusResponseDto.builder()
                .userId(user.getId())
                .enabled(user.isEnabled())
                .accountLocked(user.isAccountLocked())
                .message(message)
                .build();
    }
}