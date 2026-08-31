package com.agri.market.email.service;


public interface EmailService {

    void sendVerificationEmail(
            String recipient,
            String verificationLink
    );

    void sendPasswordResetEmail(
            String recipient,
            String resetLink
    );

    void sendDeliveryOtpEmail(
            String recipient,
            String otp
    );
}