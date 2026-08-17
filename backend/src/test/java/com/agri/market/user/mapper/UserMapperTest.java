package com.agri.market.user.mapper;

import com.agri.market.support.ProfileUpdateRequestTestFactory;
import com.agri.market.support.UserTestFactory;
import com.agri.market.user.dto.ProfileUpdateRequestDto;
import com.agri.market.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserMapper")
class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void shouldUpdatePresentProfileFields() {
        User user = UserTestFactory.activeUser();
        ProfileUpdateRequestDto request = ProfileUpdateRequestTestFactory.validRequest();

        userMapper.updateUserFromProfileRequest(request, user);

        assertThat(user.getFullName()).isEqualTo("Updated Name");
        assertThat(user.getProfilePictureUrl()).isEqualTo("https://example.com/profile.jpg");
    }

    @Test
    void shouldLeaveExistingValuesWhenOptionalFieldsAreMissing() {
        User user = UserTestFactory.activeUser();
        String originalName = user.getFullName();
        String originalPictureUrl = user.getProfilePictureUrl();
        ProfileUpdateRequestDto request = ProfileUpdateRequestDto.builder().build();

        userMapper.updateUserFromProfileRequest(request, user);

        assertThat(user.getFullName()).isEqualTo(originalName);
        assertThat(user.getProfilePictureUrl()).isEqualTo(originalPictureUrl);
    }
}

