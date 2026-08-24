package com.agri.market.password.service;

import com.agri.market.password.dto.ForgotPasswordRequest;
import com.agri.market.password.dto.ResetPasswordRequest;


public interface PasswordResetService {

    void forgotPassword(
            ForgotPasswordRequest request
    );

    void resetPassword(
            ResetPasswordRequest request
    );
}