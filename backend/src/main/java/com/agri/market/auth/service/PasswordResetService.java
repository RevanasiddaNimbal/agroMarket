package com.agri.market.auth.service;

import com.agri.market.auth.dto.ForgotPasswordRequest;
import com.agri.market.auth.dto.ResetPasswordRequest;


public interface PasswordResetService {

    void forgotPassword(
            ForgotPasswordRequest request
    );
    
    void resetPassword(
            ResetPasswordRequest request
    );
}