package com.agri.market.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    public BusinessException(
            final ErrorCode errorCode,
            final Object... args
    ) {
        super(formatMessage(errorCode, args));
        this.errorCode = errorCode;
        this.args = args;
    }

    private static String formatMessage(
            final ErrorCode errorCode,
            final Object... args
    ) {
        if (args == null || args.length == 0) {
            return errorCode.getDefaultMessage();
        }

        return String.format(
                errorCode.getDefaultMessage(),
                args
        );
    }
}