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

    void sendOrderConfirmationEmail(
            String recipient,
            String orderId,
            String totalAmount
    );

    void sendProductBookedEmail(
            String recipient,
            String orderId,
            String productName,
            String quantity
    );

    void sendOrderCancellationEmail(
            String recipient,
            String orderId,
            String totalAmount
    );

    void sendProductOrderCancellationEmail(
            String recipient,
            String orderId,
            String productName,
            String quantity
    );

    void sendOrderShippedEmail(
            String recipient,
            String orderId
    );

    void sendOrderOutForDeliveryEmail(
            String recipient,
            String orderId
    );

    void sendDeliveryCompletedEmail(
            String recipient,
            String orderId
    );

    void sendProductDeliveryCompletedEmail(
            String recipient,
            String orderId,
            String productName,
            String quantity
    );
}