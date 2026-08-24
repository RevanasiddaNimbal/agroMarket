package com.agri.market.exception;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BusinessException")
class BusinessExceptionTest {

    @Test
    void shouldFormatMessageWithArguments() {
        BusinessException exception = new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
        assertThat(exception.getMessage()).isEqualTo("The provided email address cannot be used.");
        assertThat(exception.getArgs()).isEmpty();
    }

    @Test
    void shouldUseDefaultMessageWhenNoArgumentsAreProvided() {
        BusinessException exception = new BusinessException(ErrorCode.INVALID_CURRENT_PASSWORD);

        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_CURRENT_PASSWORD.getDefaultMessage());
    }
}

