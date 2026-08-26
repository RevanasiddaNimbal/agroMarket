package com.agri.market.user.service;

import com.agri.market.user.dto.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserProfileResponseDto getCurrentUserProfile(
            String userEmail
    );

    void updateFullName(
            UpdateFullNameRequestDto request,
            String userEmail
    );

    void updateProfilePicture(
            UpdateProfilePictureRequestDto request,
            String userEmail
    );

    void sendPhoneOtp(
            SendPhoneOtpRequestDto request,
            String userEmail
    );

    void verifyPhoneOtp(
            VerifyPhoneOtpRequestDto request,
            String userEmail
    );

    void resendPhoneOtp(
            ResendPhoneOtpRequestDto request,
            String userEmail
    );

    void setPassword(
            SetPasswordRequestDto request,
            String userEmail
    );

    void changePassword(
            ChangePasswordRequestDto request,
            String userEmail
    );


    void deleteAccount(
            String userEmail
    );
}