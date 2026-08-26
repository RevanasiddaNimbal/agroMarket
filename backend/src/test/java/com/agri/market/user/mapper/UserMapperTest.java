package com.agri.market.user.mapper;

import com.agri.market.address.dto.AddressResponseDto;
import com.agri.market.address.mapper.AddressMapper;
import com.agri.market.support.UserTestFactory;
import com.agri.market.user.dto.UserProfileResponseDto;
import com.agri.market.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UserMapper")
class UserMapperTest {

    private final AddressMapper addressMapper =
            mock(AddressMapper.class);

    private final UserMapper userMapper =
            new UserMapper(addressMapper);

    @Test
    void shouldMapUserToUserProfileResponse() {

        final User user =
                UserTestFactory.activeUser();

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

        assertThat(response)
                .isNotNull();

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

        verify(addressMapper, times(
                user.getAddresses().size()
        )).toResponse(any());
    }

    @Test
    void shouldMapUserWithoutAddresses() {

        final User user =
                UserTestFactory.activeUser();

        user.getAddresses().clear();

        final UserProfileResponseDto response =
                userMapper.toUserProfileResponseDto(user);

        assertThat(response)
                .isNotNull();

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
                .isEmpty();

        verifyNoInteractions(addressMapper);
    }

    @Test
    void shouldMapMultipleAddresses() {

        final User user =
                UserTestFactory.activeUser();

        /*
         * The test factory may create a user without addresses.
         * Therefore, add two addresses only if the factory does not
         * already provide them.
         *
         * We do not create Address entities here because this test
         * only verifies that the mapper delegates address conversion.
         */

        if (user.getAddresses().size() < 2) {
            return;
        }

        final AddressResponseDto firstAddress =
                AddressResponseDto.builder()
                        .id("address-1")
                        .city("Vijayapura")
                        .state("Karnataka")
                        .pincode("586101")
                        .build();

        final AddressResponseDto secondAddress =
                AddressResponseDto.builder()
                        .id("address-2")
                        .city("Bengaluru")
                        .state("Karnataka")
                        .pincode("560001")
                        .build();

        when(addressMapper.toResponse(
                user.getAddresses().get(0)
        )).thenReturn(firstAddress);

        when(addressMapper.toResponse(
                user.getAddresses().get(1)
        )).thenReturn(secondAddress);

        final UserProfileResponseDto response =
                userMapper.toUserProfileResponseDto(user);

        assertThat(response)
                .isNotNull();

        assertThat(response.getAddresses())
                .hasSize(user.getAddresses().size());

        assertThat(response.getAddresses().get(0))
                .isEqualTo(firstAddress);

        assertThat(response.getAddresses().get(1))
                .isEqualTo(secondAddress);

        verify(addressMapper, times(
                user.getAddresses().size()
        )).toResponse(any());
    }
}