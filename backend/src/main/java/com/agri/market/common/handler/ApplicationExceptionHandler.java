package com.agri.market.common.handler;

import com.agri.market.common.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.agri.market.common.exception.ErrorCode.*;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            final BusinessException exception
    ) {

        log.warn(
                "Business exception: code={}",
                exception.getErrorCode().getCode()
        );

        final ErrorResponse response = ErrorResponse.builder()
                .code(exception.getErrorCode().getCode())
                .message(exception.getMessage())
                .build();

        return ResponseEntity
                .status(exception.getErrorCode().getStatus())
                .body(response);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledException(
            final DisabledException exception
    ) {

        log.warn("Authentication rejected: user account is disabled");

        final ErrorResponse response = ErrorResponse.builder()
                .code(ERR_USER_DISABLED.getCode())
                .message(ERR_USER_DISABLED.getDefaultMessage())
                .build();

        return ResponseEntity
                .status(ERR_USER_DISABLED.getStatus())
                .body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            final BadCredentialsException exception
    ) {

        log.warn("Authentication failed: invalid credentials");

        final ErrorResponse response = ErrorResponse.builder()
                .code(BAD_CREDENTIALS.getCode())
                .message(BAD_CREDENTIALS.getDefaultMessage())
                .build();

        return ResponseEntity
                .status(BAD_CREDENTIALS.getStatus())
                .body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
            final UsernameNotFoundException exception
    ) {

        log.debug("User lookup failed");

        final ErrorResponse response = ErrorResponse.builder()
                .code(USER_NOT_FOUND.getCode())
                .message(USER_NOT_FOUND.getDefaultMessage())
                .build();

        return ResponseEntity
                .status(USER_NOT_FOUND.getStatus())
                .body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
            final AuthorizationDeniedException exception
    ) {

        log.warn("Authorization denied for authenticated user");

        final ErrorResponse response = ErrorResponse.builder()
                .code("ACCESS_DENIED")
                .message("You do not have permission to access this resource")
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            final DataIntegrityViolationException exception
    ) {

        log.warn(
                "Database constraint violation",
                exception
        );

        final ErrorResponse response = ErrorResponse.builder()
                .code("DATA_INTEGRITY_VIOLATION")
                .message("The requested operation violates a data constraint")
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            final HttpMessageNotReadableException exception
    ) {

        log.debug(
                "Malformed request body: {}",
                exception.getMessage()
        );

        final ErrorResponse response = ErrorResponse.builder()
                .code("MALFORMED_REQUEST")
                .message("Request body is invalid or malformed")
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParameter(
            final MissingServletRequestParameterException exception
    ) {

        log.debug(
                "Missing required request parameter: {}",
                exception.getParameterName()
        );

        final ErrorResponse response = ErrorResponse.builder()
                .code("MISSING_REQUEST_PARAMETER")
                .message("Required request parameter is missing")
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            final MethodArgumentNotValidException exception
    ) {

        final List<ErrorResponse.ValidationError> validationErrors =
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(this::mapValidationError)
                        .toList();

        log.debug(
                "Request validation failed: {} validation error(s)",
                validationErrors.size()
        );

        final ErrorResponse response = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Request validation failed")
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            final EntityNotFoundException exception
    ) {

        log.warn(
                "Entity not found: {}",
                exception.getMessage()
        );

        final ErrorResponse response = ErrorResponse.builder()
                .code("RESOURCE_NOT_FOUND")
                .message(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<ErrorResponse> handleCredentialsExpired(
            final CredentialsExpiredException exception
    ) {
        log.warn("Password expired during authentication");

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        ErrorResponse.builder()
                                .code(PASSWORD_EXPIRED.getCode())
                                .message("Password has expired. Please change your password.")
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            final Exception exception
    ) {

        log.error(
                "Unexpected application exception",
                exception
        );

        final ErrorResponse response = ErrorResponse.builder()
                .code(ERR_INTERNAL_SERVER_ERROR.getCode())
                .message(ERR_INTERNAL_SERVER_ERROR.getDefaultMessage())
                .build();


        return ResponseEntity
                .status(ERR_INTERNAL_SERVER_ERROR.getStatus())
                .body(response);
    }

    private ErrorResponse.ValidationError mapValidationError(
            final FieldError fieldError
    ) {
        final String validationCode = fieldError.getDefaultMessage();

        return ErrorResponse.ValidationError.builder()
                .field(fieldError.getField())
                .code(validationCode)
                .message(validationCode)
                .build();
    }
}