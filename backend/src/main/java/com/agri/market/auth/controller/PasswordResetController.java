package com.agri.market.auth.controller;

import com.agri.market.auth.dto.ForgotPasswordRequest;
import com.agri.market.auth.dto.ResetPasswordRequest;
import com.agri.market.auth.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Password Recovery",
        description = "APIs for password recovery and password reset"
)
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Operation(
            summary = "Request password reset",
            description = "Initiates the password recovery process."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset request processed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            )
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody final ForgotPasswordRequest request
    ) {

        log.info("Password reset request received");

        passwordResetService.forgotPassword(request);

        log.info("Password reset request processed");

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Reset password",
            description = "Resets the user's password using a valid reset token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid password reset request"
            )
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody final ResetPasswordRequest request
    ) {

        log.info("Password reset execution requested");

        passwordResetService.resetPassword(request);

        log.info("Password reset completed successfully");

        return ResponseEntity.ok().build();
    }
}