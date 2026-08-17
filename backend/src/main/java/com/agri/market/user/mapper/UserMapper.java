package com.agri.market.user.mapper;


import com.agri.market.user.dto.ProfileUpdateRequestDto;
import com.agri.market.user.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserMapper {

    public void updateUserFromProfileRequest(ProfileUpdateRequestDto requestDto,
                                             User user
    ) {

        if (!StringUtils.isEmpty(requestDto.getFullName()) && !user.getFullName().equals(requestDto.getFullName())) {
            user.setFullName(requestDto.getFullName());
        }
        if (!StringUtils.isEmpty(requestDto.getProfilePictureUrl())) {
            user.setProfilePictureUrl(requestDto.getProfilePictureUrl());
        }
    }
}
