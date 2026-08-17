package com.agri.market.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {
    // USER
    USER_NOT_FOUND(
            "USER_NOT_FOUND",
            "User not found.",
            NOT_FOUND
    ),

    USER_ALREADY_DEACTIVATED(
            "USER_ALREADY_DEACTIVATED",
            "User account is already deactivated.",
            CONFLICT
    ),

    USER_ALREADY_ACTIVATED(
            "USER_ALREADY_ACTIVATED",
            "User account is already activated.",
            CONFLICT
    ),

    EMAIL_ALREADY_EXISTS(
            "EMAIL_ALREADY_EXISTS",
            "The provided email address cannot be used.",
            CONFLICT
    ),

    PHONE_ALREADY_EXISTS(
            "PHONE_ALREADY_EXISTS",
            "The provided phone number cannot be used.",
            CONFLICT
    ),
    // PASSWORD

    INVALID_CURRENT_PASSWORD(
            "INVALID_CURRENT_PASSWORD",
            "The current password is incorrect.",
            BAD_REQUEST
    ),

    PASSWORD_MISMATCH(
            "ERR_PASSWORD_MISMATCH",
            "The passwords do not match.",
            BAD_REQUEST
    ),

    // PASSWORD RESET

    PASSWORD_RESET_TOKEN_EXPIRED(
            "PASSWORD_RESET_TOKEN_EXPIRED",
            "The password reset request has expired.",
            BAD_REQUEST
    ),

    PASSWORD_RESET_TOKEN_ALREADY_USED(
            "PASSWORD_RESET_TOKEN_ALREADY_USED",
            "The password reset request has already been used.",
            BAD_REQUEST
    ),

    INVALID_PASSWORD_RESET_TOKEN(
            "INVALID_PASSWORD_RESET_TOKEN",
            "The password reset request is invalid.",
            BAD_REQUEST
    ),

    // ROLE

    ROLE_NOT_FOUND(
            "ROLE_NOT_FOUND",
            "The requested role was not found.",
            NOT_FOUND
    ),

    // AUTHENTICATION

    ERR_USER_DISABLED(
            "ERR_USER_DISABLED",
            "The account is not available for authentication.",
            UNAUTHORIZED
    ),

    BAD_CREDENTIALS(
            "BAD_CREDENTIALS",
            "Invalid email or password.",
            UNAUTHORIZED
    ),

    INVALID_REFRESH_TOKEN(
            "INVALID_REFRESH_TOKEN",
            "The refresh token is invalid or has expired.",
            UNAUTHORIZED
    ),

    // EMAIL VERIFICATION

    INVALID_VERIFICATION_TOKEN(
            "INVALID_VERIFICATION_TOKEN",
            "The email verification request is invalid.",
            BAD_REQUEST
    ),

    VERIFICATION_TOKEN_ALREADY_USED(
            "VERIFICATION_TOKEN_ALREADY_USED",
            "The email verification request has already been completed.",
            BAD_REQUEST
    ),

    VERIFICATION_TOKEN_EXPIRED(
            "VERIFICATION_TOKEN_EXPIRED",
            "The email verification request has expired.",
            BAD_REQUEST
    ),

    // AUTHORIZATION

    ACCESS_DENIED(
            "ACCESS_DENIED",
            "You do not have permission to perform this operation.",
            FORBIDDEN
    ),

    // VALIDATION

    VALIDATION_ERROR(
            "VALIDATION_ERROR",
            "The request contains invalid data.",
            BAD_REQUEST
    ),

    MALFORMED_REQUEST(
            "MALFORMED_REQUEST",
            "The request could not be processed.",
            BAD_REQUEST
    ),

    // DATABASE

    DATA_INTEGRITY_VIOLATION(
            "DATA_INTEGRITY_VIOLATION",
            "The requested operation could not be completed.",
            CONFLICT
    ),

    // GENERIC

    ERR_INTERNAL_SERVER_ERROR(
            "ERR_INTERNAL_SERVER_ERROR",
            "An unexpected error occurred.",
            INTERNAL_SERVER_ERROR
    );

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(
            final String code,
            final String defaultMessage,
            final HttpStatus status
    ) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}