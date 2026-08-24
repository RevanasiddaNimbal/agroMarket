package com.agri.market.email.controller;

import com.agri.market.email.dto.EmailVerificationRequest;
import com.agri.market.email.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Email Verification",
        description = "APIs for email verification and verification email management"
)
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @Operation(
            summary = "Verify email address",
            description = "Verifies the user's email address using the verification token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Email verified successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid, expired or already used verification token"
            )
    })
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(
            @RequestParam final String token
    ) {

        log.info("Email verification request received");

        emailVerificationService.verifyEmail(token);

        log.info("Email verified successfully");

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Resend verification email",
            description = "Sends a new verification email to an unverified user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification email request processed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email address"
            )
    })
    @PostMapping("/resend-verification-email")
    public ResponseEntity<Void> resendVerificationEmail(
            @Valid @RequestBody final EmailVerificationRequest request
    ) {

        log.info("Verification email resend request received");

        emailVerificationService.resendVerificationEmail(
                request
        );

        log.info(
                "Verification email resend request processed"
        );

        return ResponseEntity.ok().build();
    }
}