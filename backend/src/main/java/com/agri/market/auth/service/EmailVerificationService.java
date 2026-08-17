package com.agri.market.auth.service;

import com.agri.market.auth.dto.EmailVerificationRequest;
import com.agri.market.user.entity.User;

public interface EmailVerificationService {

    void sendVerificationEmail(User user);

    void verifyEmail(String token);

    void resendVerificationEmail(EmailVerificationRequest request);
}