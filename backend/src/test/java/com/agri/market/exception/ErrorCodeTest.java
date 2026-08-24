package com.agri.market.exception;

import com.agri.market.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ErrorCode")
class ErrorCodeTest {

    @Test
    void shouldExposeCodeMessageAndStatusForEachRelevantError() {
        assertThat(ErrorCode.INVALID_REFRESH_TOKEN.getCode()).isEqualTo("INVALID_REFRESH_TOKEN");
        assertThat(ErrorCode.INVALID_REFRESH_TOKEN.getDefaultMessage()).contains("refresh token");
        assertThat(ErrorCode.INVALID_REFRESH_TOKEN.getStatus().value()).isEqualTo(401);
    }
}

