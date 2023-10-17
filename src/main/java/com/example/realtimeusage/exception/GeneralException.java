package com.example.realtimeusage.exception;

import com.example.realtimeusage.constant.ErrorCode;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {
    private final ErrorCode errorCode;

    public GeneralException() {
        super();
        this.errorCode = ErrorCode.INTERVAL_ERROR;
    }

    public GeneralException(String message) {
        super(message);
        this.errorCode = ErrorCode.INTERVAL_ERROR;
    }

    public GeneralException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.INTERVAL_ERROR;
    }

    public GeneralException(Throwable cause) {
        super(cause);
        this.errorCode = ErrorCode.INTERVAL_ERROR;
    }

    public GeneralException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = ErrorCode.INTERVAL_ERROR;
    }

    public GeneralException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public GeneralException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public GeneralException(ErrorCode errorCode, String message, Throwable cause, boolean enableSuppression,
                            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = ErrorCode.INTERVAL_ERROR;
    }

}