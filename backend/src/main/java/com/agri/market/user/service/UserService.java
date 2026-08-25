package com.agri.market.user.service;

import com.agri.market.user.dto.ChangePasswordRequestDto;
import com.agri.market.user.dto.ProfileUpdateRequestDto;
import com.agri.market.user.dto.SetPasswordRequestDto;
import com.agri.market.user.dto.UserProfileResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void updateProfileInfo(ProfileUpdateRequestDto request, String userEmail);

    void changePassword(ChangePasswordRequestDto request, String userEmail);

    void deactivateAccount(String userEmail);

    void reactivateAccount(String userEmail);

    void deleteAccount(String userEmail);

    void setPassword(SetPasswordRequestDto request, String userEmail);

    UserProfileResponseDto getCurrentUserProfile(String userEmail);
}