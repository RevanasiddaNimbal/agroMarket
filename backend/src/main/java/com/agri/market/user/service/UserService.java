package com.agri.market.user.service;

import com.agri.market.user.dto.ChangePasswordRequestDto;
import com.agri.market.user.dto.ProfileUpdateRequestDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void updateProfileInfo(ProfileUpdateRequestDto request, String userId);

    void changePassword(ChangePasswordRequestDto request, String userId);

    void deactivateAccount(String userId);

    void reactivateAccount(String userId);

    void deleteAccount(String userId);
}
