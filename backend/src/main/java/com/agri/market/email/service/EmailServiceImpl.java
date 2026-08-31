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

}