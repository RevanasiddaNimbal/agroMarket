package com.agri.market.email.service;

import com.agri.market.email.config.BrevoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final BrevoProperties brevoProperties;
    private final RestClient restClient;

    @Override
    public void sendVerificationEmail(
            final String recipient,
            final String verificationLink
    ) {

        final String htmlContent =
                buildVerificationEmail(verificationLink);

        sendEmail(
                recipient,
                "Verify your AgriMarket account",
                htmlContent
        );

        log.info(
                "Verification email sent successfully to recipient"
        );
    }

    @Override
    public void sendPasswordResetEmail(
            final String recipient,
            final String resetLink
    ) {

        final String htmlContent =
                buildPasswordResetEmail(resetLink);

        sendEmail(
                recipient,
                "Reset your AgriMarket password",
                htmlContent
        );

        log.info(
                "Password reset email sent successfully to recipient"
        );
    }

    private void sendEmail(
            final String recipient,
            final String subject,
            final String htmlContent
    ) {

        final Map<String, Object> requestBody =
                Map.of(
                        "sender",
                        Map.of(
                                "name",
                                brevoProperties.getSenderName(),
                                "email",
                                brevoProperties.getSenderEmail()
                        ),
                        "to",
                        new Object[]{
                                Map.of(
                                        "email",
                                        recipient
                                )
                        },
                        "subject",
                        subject,
                        "htmlContent",
                        htmlContent
                );

        try {

            restClient
                    .post()
                    .uri(brevoProperties.getUrl())
                    .header(
                            "api-key",
                            brevoProperties.getApiKey()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception exception) {

            log.error(
                    "Failed to send email to recipient",
                    exception
            );

            throw new IllegalStateException(
                    "Unable to send email",
                    exception
            );
        }
    }

    private String buildVerificationEmail(
            final String verificationLink
    ) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Verify your AgriMarket account</title>
                </head>
                <body>
                    <h2>Verify your AgriMarket account</h2>
                
                    <p>Hello,</p>
                
                    <p>
                        Thank you for creating an AgriMarket account.
                    </p>
                
                    <p>
                        Click the button below to verify your email address.
                    </p>
                
                    <p>
                        <a href="%s">
                            Verify Email
                        </a>
                    </p>
                
                    <p>
                        This link will expire within 15 minutes.
                    </p>
                
                    <p>
                        If you did not create an AgriMarket account,
                        you can safely ignore this email.
                    </p>
                
                    <p>
                        Regards,<br>
                        AgriMarket Team
                    </p>
                </body>
                </html>
                """.formatted(verificationLink);
    }

    private String buildPasswordResetEmail(
            final String resetLink
    ) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Reset your AgriMarket password</title>
                </head>
                <body>
                    <h2>Reset your AgriMarket password</h2>
                
                    <p>Hello,</p>
                
                    <p>
                        We received a request to reset your AgriMarket password.
                    </p>
                
                    <p>
                        Click the button below to reset your password.
                    </p>
                
                    <p>
                        <a href="%s">
                            Reset Password
                        </a>
                    </p>
                
                    <p>
                        This link will expire within 5 minutes.
                    </p>
                
                    <p>
                        If you did not request a password reset,
                        you can safely ignore this email.
                    </p>
                
                    <p>
                        Regards,<br>
                        AgriMarket Team
                    </p>
                </body>
                </html>
                """.formatted(resetLink);
    }

    @Override
    public void sendDeliveryOtpEmail(
            final String recipient,
            final String otp
    ) {

        final String htmlContent =
                buildDeliveryOtpEmail(otp);

        sendEmail(
                recipient,
                "AgriMarket delivery verification OTP",
                htmlContent
        );

        log.info(
                "Delivery OTP email sent successfully to recipient"
        );
    }

    private String buildDeliveryOtpEmail(
            final String otp
    ) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>AgriMarket Delivery Verification</title>
                </head>
                <body>
                    <h2>AgriMarket Delivery Verification</h2>
                
                    <p>Hello,</p>
                
                    <p>
                        Your delivery verification OTP is:
                    </p>
                
                    <h1>%s</h1>
                
                    <p>
                        This OTP will expire shortly.
                    </p>
                
                    <p>
                        Do not share this OTP with anyone except the person
                        completing your delivery.
                    </p>
                
                    <p>
                        Regards,<br>
                        AgriMarket Team
                    </p>
                </body>
                </html>
                """.formatted(otp);
    }

    @Override
    public void sendOrderConfirmationEmail(
            final String recipient,
            final String orderId,
            final String totalAmount
    ) {

        final String htmlContent =
                buildOrderConfirmationEmail(
                        orderId,
                        totalAmount
                );

        sendEmail(
                recipient,
                "AgriMarket order confirmed",
                htmlContent
        );

        log.info(
                "Order confirmation email sent successfully to recipient"
        );
    }

    private String buildOrderConfirmationEmail(
            final String orderId,
            final String totalAmount
    ) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>AgriMarket Order Confirmed</title>
                </head>
                <body>
                    <h2>AgriMarket Order Confirmed</h2>
                
                    <p>Hello,</p>
                
                    <p>
                        Your AgriMarket order has been successfully confirmed.
                    </p>
                
                    <p>
                        <strong>Order ID:</strong> %s
                    </p>
                
                    <p>
                        <strong>Total Amount:</strong> %s
                    </p>
                
                    <p>
                        Thank you for shopping with AgriMarket.
                    </p>
                
                    <p>
                        Regards,<br>
                        AgriMarket Team
                    </p>
                </body>
                </html>
                """.formatted(
                orderId,
                totalAmount
        );
    }

    @Override
    public void sendProductBookedEmail(
            final String recipient,
            final String orderId,
            final String productName,
            final String quantity
    ) {

        final String htmlContent =
                buildProductBookedEmail(
                        orderId,
                        productName,
                        quantity
                );

        sendEmail(
                recipient,
                "Your AgriMarket product has been booked",
                htmlContent
        );

        log.info(
                "Product booked email sent successfully to product owner"
        );
    }

    private String buildProductBookedEmail(
            final String orderId,
            final String productName,
            final String quantity
    ) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>AgriMarket Product Booked</title>
                </head>
                <body>
                    <h2>Your AgriMarket Product Has Been Booked</h2>
                
                    <p>Hello,</p>
                
                    <p>
                        Your product has been successfully booked by a customer
                        on AgriMarket.
                    </p>
                
                    <p>
                        <strong>Order ID:</strong> %s
                    </p>
                
                    <p>
                        <strong>Product:</strong> %s
                    </p>
                
                    <p>
                        <strong>Quantity:</strong> %s
                    </p>
                
                    <p>
                        Please check your AgriMarket account for the order details.
                    </p>
                
                    <p>
                        Regards,<br>
                        AgriMarket Team
                    </p>
                </body>
                </html>
                """.formatted(
                orderId,
                productName,
                quantity
        );
    }

    @Override
    public void sendOrderCancellationEmail(
            final String recipient,
            final String orderId,
            final String totalAmount
    ) {

        final String htmlContent =
                buildOrderCancellationEmail(
                        orderId,
                        totalAmount
                );

        sendEmail(
                recipient,
                "AgriMarket order cancelled and refunded",
                htmlContent
        );

        log.info(
                "Order cancellation email sent successfully to recipient"
        );
    }

    private String buildOrderCancellationEmail(
            final String orderId,
            final String totalAmount
    ) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>AgriMarket Order Cancelled</title>
                </head>
                <body>
                    <h2>AgriMarket Order Cancelled</h2>
                
                    <p>Hello,</p>
                
                    <p>
                        Your AgriMarket order has been cancelled successfully.
                    </p>
                
                    <p>
                        <strong>Order ID:</strong> %s
                    </p>
                
                    <p>
                        <strong>Refund Amount:</strong> %s
                    </p>
                
                    <p>
                        Your payment refund has been processed successfully.
                    </p>
                
                    <p>
                        Regards,<br>
                        AgriMarket Team
                    </p>
                </body>
                </html>
                """.formatted(
                orderId,
                totalAmount
        );
    }

    @Override
    public void sendProductOrderCancellationEmail(
            final String recipient,
            final String orderId,
            final String productName,
            final String quantity
    ) {

        final String htmlContent =
                buildProductOrderCancellationEmail(
                        orderId,
                        productName,
                        quantity
                );

        sendEmail(
                recipient,
                "AgriMarket product order cancelled",
                htmlContent
        );

        log.info(
                "Product order cancellation email sent successfully to product owner"
        );
    }

    private String buildProductOrderCancellationEmail(
            final String orderId,
            final String productName,
            final String quantity
    ) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>AgriMarket Product Order Cancelled</title>
                </head>
                <body>
                    <h2>AgriMarket Product Order Cancelled</h2>
                
                    <p>Hello,</p>
                
                    <p>
                        An order containing your product has been cancelled.
                    </p>
                
                    <p>
                        <strong>Order ID:</strong> %s
                    </p>
                
                    <p>
                        <strong>Product:</strong> %s
                    </p>
                
                    <p>
                        <strong>Quantity:</strong> %s
                    </p>
                
                    <p>
                        The product order is no longer active.
                    </p>
                
                    <p>
                        Regards,<br>
                        AgriMarket Team
                    </p>
                </body>
                </html>
                """.formatted(
                orderId,
                productName,
                quantity
        );
    }

    @Override
    public void sendOrderShippedEmail(
            final String recipient,
            final String orderId
    ) {

        final String htmlContent =
                buildOrderShippedEmail(orderId);

        sendEmail(
                recipient,
                "Your AgriMarket order has been shipped",
                htmlContent
        );

        log.info(
                "Order shipped email sent successfully to recipient"
        );
    }

    private String buildOrderShippedEmail(
            final String orderId
    ) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>AgriMarket Order Shipped</title>
                </head>
                <body>
                    <h2>Your AgriMarket Order Has Been Shipped</h2>
                
                    <p>Hello,</p>
                
                    <p>
                        Your AgriMarket order has been shipped.
                    </p>
                
                    <p>
                        <strong>Order ID:</strong> %s
                    </p>
                
                    <p>
                        You will receive another notification when your order
                        is out for delivery.
                    </p>
                
                    <p>
                        Regards,<br>
                        AgriMarket Team
                    </p>
                </body>
                </html>
                """.formatted(orderId);
    }

    @Override
    public void sendOrderOutForDeliveryEmail(
            final String recipient,
            final String orderId
    ) {

        final String htmlContent =
                buildOrderOutForDeliveryEmail(orderId);

        sendEmail(
                recipient,
                "Your AgriMarket order is out for delivery",
                htmlContent
        );

        log.info(
                "Order out-for-delivery email sent successfully to recipient"
        );
    }

    private String buildOrderOutForDeliveryEmail(
            final String orderId
    ) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>AgriMarket Order Out For Delivery</title>
                </head>
                <body>
                    <h2>Your AgriMarket Order Is Out For Delivery</h2>
                
                    <p>Hello,</p>
                
                    <p>
                        Your AgriMarket order is now out for delivery.
                    </p>
                
                    <p>
                        <strong>Order ID:</strong> %s
                    </p>
                
                    <p>
                        Please keep your delivery verification OTP ready.
                    </p>
                
                    <p>
                        Regards,<br>
                        AgriMarket Team
                    </p>
                </body>
                </html>
                """.formatted(orderId);
    }

    @Override
    public void sendDeliveryCompletedEmail(
            final String recipient,
            final String orderId
    ) {

        final String htmlContent =
                buildDeliveryCompletedEmail(orderId);

        sendEmail(
                recipient,
                "Your AgriMarket order has been delivered",
                htmlContent
        );

        log.info(
                "Delivery completed email sent successfully to recipient"
        );
    }

    private String buildDeliveryCompletedEmail(
            final String orderId
    ) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>AgriMarket Order Delivered</title>
                </head>
                <body>
                    <h2>Your AgriMarket Order Has Been Delivered</h2>
                
                    <p>Hello,</p>
                
                    <p>
                        Your AgriMarket order has been successfully delivered.
                    </p>
                
                    <p>
                        <strong>Order ID:</strong> %s
                    </p>
                
                    <p>
                        Thank you for using AgriMarket.
                    </p>
                
                    <p>
                        Regards,<br>
                        AgriMarket Team
                    </p>
                </body>
                </html>
                """.formatted(orderId);
    }

    @Override
    public void sendProductDeliveryCompletedEmail(
            final String recipient,
            final String orderId,
            final String productName,
            final String quantity
    ) {

        final String htmlContent =
                buildProductDeliveryCompletedEmail(
                        orderId,
                        productName,
                        quantity
                );

        sendEmail(
                recipient,
                "Your AgriMarket product order has been delivered",
                htmlContent
        );

        log.info(
                "Product delivery completed email sent successfully to product owner"
        );
    }

    private String buildProductDeliveryCompletedEmail(
            final String orderId,
            final String productName,
            final String quantity
    ) {

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>AgriMarket Product Delivered</title>
                </head>
                <body>
                    <h2>Your AgriMarket Product Order Has Been Delivered</h2>
                
                    <p>Hello,</p>
                
                    <p>
                        Your product has been successfully delivered to the customer.
                    </p>
                
                    <p>
                        <strong>Order ID:</strong> %s
                    </p>
                
                    <p>
                        <strong>Product:</strong> %s
                    </p>
                
                    <p>
                        <strong>Quantity:</strong> %s
                    </p>
                
                    <p>
                        Thank you for selling through AgriMarket.
                    </p>
                
                    <p>
                        Regards,<br>
                        AgriMarket Team
                    </p>
                </body>
                </html>
                """.formatted(
                orderId,
                productName,
                quantity
        );
    }

}