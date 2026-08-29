package com.agri.market.common.exception;

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
    USER_NOT_REGISTERED(
            "USER_NOT_REGISTERED",
            "The user is not registered.Please register.",
            NOT_FOUND
    ),

    //ADMIN

    ADMIN_USER_NOT_FOUND(
            "ADMIN_USER_NOT_FOUND",
            "Admin user not found.",
            NOT_FOUND
    ),
    ADMIN_USER_ALREADY_ACTIVE(
            "ADMIN_USER_ALREADY_ACTIVE",
            "Admin user is already active.",
            CONFLICT
    ),
    ADMIN_USER_ALREADY_INACTIVE(
            "ADMIN_USER_ALREADY_INACTIVE",
            "Admin user is already inactive.",
            CONFLICT
    ),

    ADMIN_USER_ALREADY_LOCKED(
            "ADMIN_USER_ALREADY_LOCKED",
            "Admin user is already locked.",
            CONFLICT
    ),

    ADMIN_USER_ALREADY_UNLOCKED(
            "ADMIN_USER_ALREADY_UNLOCKED",
            "Admin user is already unlocked.",
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
    PASSWORD_EXPIRED(
            "PASSWORD_EXPIRED",
            "Your password has expired. Please change your password.",
            UNAUTHORIZED
    ),
    ACCOUNT_LOCKED(
            "ACCOUNT_LOCKED",
            "Your account is temporarily locked due to too many failed login attempts.",
            UNAUTHORIZED
    ),

    TOO_MANY_FAILED_ATTEMPTS(
            "TOO_MANY_FAILED_ATTEMPTS",
            "Too many failed login attempts. Please try again later.",
            UNAUTHORIZED
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
    PASSWORD_LOGIN_NOT_AVAILABLE(
            "PASSWORD_LOGIN_NOT_AVAILABLE",
            "Password login is not available for this account. Use OAuth login or set a password first.",
            UNAUTHORIZED
    ),

    PASSWORD_ALREADY_SET(
            "PASSWORD_ALREADY_SET",
            "Password is already set for this account. Use change password instead.",
            CONFLICT
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
            "Your account is no longer available. Please contact support.",
            UNAUTHORIZED
    ),

    PERMANENT_ACCOUNT_LOCKED(
            "PERMANENT_ACCOUNT_LOCKED",
            "Your account is permanently locked. Please contact support.",
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
    REFRESH_TOKEN_REUSE_DETECTED(
            "REFRESH_TOKEN_REUSE_DETECTED",
            "Refresh token reuse detected",
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
    ),

    // OAuth
    INVALID_OAUTH_LOGIN_CODE("INVALID_OAUTH_LOGIN_CODE",
            "Invalid OAuth login code",
            BAD_REQUEST
    ),

    OAUTH_LOGIN_CODE_ALREADY_USED("OAUTH_LOGIN_CODE_ALREADY_USED",
            "OAuth login code has already been used",
            BAD_REQUEST
    ),
    OAUTH_LOGIN_CODE_EXPIRED("OAUTH_LOGIN_CODE_EXPIRED",
            "OAuth login code has expired",
            BAD_REQUEST
    ),
    OAUTH_EMAIL_NOT_AVAILABLE("OAUTH_EMAIL_NOT_AVAILABLE",
            "OAuth email is not available",
            BAD_REQUEST
    ),

    // ADDRESS
    ADDRESS_NOT_FOUND(
            "ADDRESS_NOT_FOUND",
            "Address not found.",
            NOT_FOUND
    ),

    ADDRESS_COORDINATES_REQUIRED(
            "ADDRESS_COORDINATES_REQUIRED",
            "Latitude and longitude are required for a map-based address.",
            BAD_REQUEST
    ),

    ADDRESS_COORDINATES_NOT_ALLOWED(
            "ADDRESS_COORDINATES_NOT_ALLOWED",
            "Latitude and longitude are not allowed for a manually entered address.",
            BAD_REQUEST
    ),
    // SMS / PHONE VERIFICATION

    PHONE_VERIFICATION_REQUIRED(
            "PHONE_VERIFICATION_REQUIRED",
            "Phone number verification is required to perform this operation.",
            UNAUTHORIZED
    ),

    PHONE_NOT_VERIFIED(
            "PHONE_NOT_VERIFIED",
            "The phone number has not been verified.",
            BAD_REQUEST
    ),

    PHONE_NUMBER_REQUIRED(
            "PHONE_NUMBER_REQUIRED",
            "A phone number is required for this operation.",
            BAD_REQUEST
    ),

    INVALID_PHONE_NUMBER(
            "INVALID_PHONE_NUMBER",
            "The provided phone number is invalid.",
            BAD_REQUEST
    ),

    INVALID_PHONE_OTP(
            "INVALID_PHONE_OTP",
            "The provided OTP is invalid.",
            BAD_REQUEST
    ),

    PHONE_OTP_EXPIRED(
            "PHONE_OTP_EXPIRED",
            "The phone verification OTP has expired. Please request a new OTP.",
            BAD_REQUEST
    ),

    PHONE_OTP_ALREADY_VERIFIED(
            "PHONE_OTP_ALREADY_VERIFIED",
            "The phone number is already verified.",
            CONFLICT
    ),

    PHONE_OTP_NOT_REQUESTED(
            "PHONE_OTP_NOT_REQUESTED",
            "No OTP verification request was found for this phone number.",
            BAD_REQUEST
    ),

    PHONE_OTP_RESEND_NOT_ALLOWED(
            "PHONE_OTP_RESEND_NOT_ALLOWED",
            "A new OTP cannot be requested yet. Please wait before trying again.",
            TOO_MANY_REQUESTS
    ),

    SMS_PROVIDER_ERROR(
            "SMS_PROVIDER_ERROR",
            "The SMS service is temporarily unavailable. Please try again later.",
            SERVICE_UNAVAILABLE
    ),

    // LOCATION

    STATE_NOT_FOUND(
            "STATE_NOT_FOUND",
            "State not found.",
            NOT_FOUND
    ),
    DISTRICT_NOT_FOUND(
            "DISTRICT_NOT_FOUND",
            "District not found.",
            NOT_FOUND
    ),
    TALUK_NOT_FOUND(
            "TALUK_NOT_FOUND",
            "Taluk not found.",
            NOT_FOUND
    ),

    // GEOCODING
    INVALID_COORDINATES(
            "INVALID_COORDINATES",
            "Invalid coordinates provided.",
            BAD_REQUEST
    ),

    GEOCODING_FAILED(
            "GEOCODING_FAILED",
            "Geocoding request failed.",
            BAD_REQUEST
    ),
    LOCATION_RESOLUTION_FAILED(
            "LOCATION_RESOLUTION_FAILED",
            "Failed to resolve location from the provided coordinates.",
            BAD_REQUEST
    ),
    //CATEGORY
    CATEGORY_NOT_FOUND(
            "CATEGORY_NOT_FOUND",
            "Category not found.",
            NOT_FOUND
    ),
    // PRODUCT
    PRODUCT_NOT_FOUND(
            "PRODUCT_NOT_FOUND",
            "Product not found.",
            NOT_FOUND
    ),
    INVALID_PRODUCT_STATUS(
            "INVALID_PRODUCT_STATUS",
            "Invalid product status provided.",
            BAD_REQUEST
    ),
    // PRODUCT  IMAGE
    INVALID_PRODUCT_IMAGE(
            "INVALID_PRODUCT_IMAGE",
            "Invalid product image provided.",
            BAD_REQUEST
    ),
    PRODUCT_IMAGE_UPLOAD_FAILED(
            "PRODUCT_IMAGE_UPLOAD_FAILED",
            "Failed to upload product image.",
            INTERNAL_SERVER_ERROR
    ),
    PRODUCT_IMAGE_DELETE_FAILED(
            "PRODUCT_IMAGE_DELETE_FAILED",
            "Failed to delete product image.",
            INTERNAL_SERVER_ERROR
    ),
    PRODUCT_IMAGE_NOT_FOUND(
            "PRODUCT_IMAGE_NOT_FOUND",
            "Product image not found.",
            NOT_FOUND
    ),
    INVALID_PRODUCT_IMAGE_ORDER(
            "INVALID_PRODUCT_IMAGE_ORDER",
            "Invalid product image order provided.",
            BAD_REQUEST
    ),
    PRODUCT_IMAGE_SIZE_EXCEEDED(
            "PRODUCT_IMAGE_SIZE_EXCEEDED",
            "Product image size exceeds the maximum allowed limit.",
            BAD_REQUEST
    ),
    // INVENTORY
    INVENTORY_NOT_FOUND(
            "INVENTORY_NOT_FOUND",
            "Inventory not found for the specified product.",
            NOT_FOUND
    ),
    INSUFFICIENT_STOCK(
            "INSUFFICIENT_STOCK",
            "Insufficient stock available for the requested operation.",
            BAD_REQUEST
    ),
    INVENTORY_QUANTITY_LESS_THAN_RESERVED(
            "INVENTORY_QUANTITY_LESS_THAN_RESERVED",
            "The new inventory quantity cannot be less than the reserved quantity.",
            BAD_REQUEST
    ),
    INVENTORY_INSUFFICIENT_STOCK(
            "INVENTORY_INSUFFICIENT_STOCK",
            "Insufficient stock available for the requested operation.",
            BAD_REQUEST
    ),
    PRODUCT_ACCESS_DENIED(
            "PRODUCT_ACCESS_DENIED",
            "You do not have permission to access this product.",
            FORBIDDEN
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