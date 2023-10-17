package com.example.realtimeusage.constant;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    OK(0, ErrorCategory.NORMAL, "OK"), //보통 error코드에 ok상태도 들어감

    BAD_REQUEST(10000, ErrorCategory.CLIENT_SIDE, "bad request"),
    SPRING_BAD_REQUEST(10001, ErrorCategory.CLIENT_SIDE, "Spring-detected bad request"),

    INTERVAL_ERROR(20000, ErrorCategory.SERVER_SIDE, "internal error"),
    SPRING_INTERNAL_ERROR(20001, ErrorCategory.SERVER_SIDE, "Spring-detected internal error");

    private final Integer code;
    private final ErrorCategory errorCategory;
    private final String message;

    public String getMessage(Exception e) {
        return getMessage(e.getMessage());
    }

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(getMessage());
    }

    public boolean isClientSideError() {
//        return this.getErrorCategory() == ErrorCategory.CLIENT_SIDE;
        return this.getErrorCategory().equals(ErrorCategory.CLIENT_SIDE);
    }

    public boolean isServerSideError() {
        return this.getErrorCategory().equals(ErrorCategory.SERVER_SIDE);
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", name(), this.getCode());
    }

    public enum ErrorCategory {
        NORMAL, CLIENT_SIDE, SERVER_SIDE;
    }
}