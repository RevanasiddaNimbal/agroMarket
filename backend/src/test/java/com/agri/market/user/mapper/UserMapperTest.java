package com.agri.market.user.mapper;

import com.agri.market.address.dto.AddressResponseDto;
import com.agri.market.address.mapper.AddressMapper;
import com.agri.market.support.ProfileUpdateRequestTestFactory;
import com.agri.market.support.UserTestFactory;
import com.agri.market.user.dto.ProfileUpdateRequestDto;
import com.agri.market.user.dto.UserProfileResponseDto;
import com.agri.market.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UserMapper")
class UserMapperTest {

    private final AddressMapper addressMapper = mock(AddressMapper.class);

    private final UserMapper userMapper =
            new UserMapper(addressMapper);

    @Test
    void shouldUpdatePresentProfileFields() {

        final User user = UserTestFactory.activeUser();

        final ProfileUpdateRequestDto request =
                ProfileUpdateRequestTestFactory.validRequest();

        userMapper.updateUserFromProfileRequest(
                request,
                user
        );

        assertThat(user.getFullName())
                .isEqualTo("Updated Name");

        assertThat(user.getProfilePictureUrl())
                .isEqualTo("https://example.com/profile.jpg");
    }

    @Test
    void shouldLeaveExistingValuesWhenOptionalFieldsAreMissing() {

        final User user = UserTestFactory.activeUser();

        final String originalName = user.getFullName();
        final String originalPictureUrl =
                user.getProfilePictureUrl();

        final ProfileUpdateRequestDto request =
                ProfileUpdateRequestDto.builder()
                        .build();

        userMapper.updateUserFromProfileRequest(
                request,
                user
        );

        assertThat(user.getFullName())
                .isEqualTo(originalName);

        assertThat(user.getProfilePictureUrl())
                .isEqualTo(originalPictureUrl);

        verifyNoInteractions(addressMapper);
    }

    @Test
    void shouldMapUserToUserProfileResponse() {

        final User user = UserTestFactory.activeUser();

        final AddressResponseDto addressResponse =
                AddressResponseDto.builder()
                        .id("address-id")
                        .city("Vijayapura")
                        .state("Karnataka")
                        .pincode("586101")
                        .build();

        when(addressMapper.toResponse(any()))
                .thenReturn(addressResponse);

        final UserProfileResponseDto response =
                userMapper.toUserProfileResponseDto(user);

        assertThat(response.getId())
                .isEqualTo(user.getId());

        assertThat(response.getFullName())
                .isEqualTo(user.getFullName());

        assertThat(response.getEmail())
                .isEqualTo(user.getEmail());

        assertThat(response.getPhoneNumber())
                .isEqualTo(user.getPhoneNumber());

        assertThat(response.isEmailVerified())
                .isEqualTo(user.isEmailVerified());

        assertThat(response.isPhoneVerified())
                .isEqualTo(user.isPhoneVerified());

        assertThat(response.getProfilePictureUrl())
                .isEqualTo(user.getProfilePictureUrl());

        assertThat(response.getAddresses())
                .hasSize(user.getAddresses().size());

        if (!user.getAddresses().isEmpty()) {
            assertThat(response.getAddresses().get(0))
                    .isEqualTo(addressResponse);
        }

        verify(addressMapper, times(user.getAddresses().size()))
                .toResponse(any());
    }
}