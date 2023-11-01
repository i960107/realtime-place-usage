package com.example.realtimeusage.constant;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    OK(0, ErrorCategory.NORMAL, "OK"), //보통 error코드에 ok상태도 들어감

    BAD_REQUEST(10000, ErrorCategory.CLIENT_SIDE, "bad request"),
    SPRING_BAD_REQUEST(10001, ErrorCategory.CLIENT_SIDE, "Spring-detected bad request"),
    VALIDATION_ERROR(10002, ErrorCategory.CLIENT_SIDE, "validation failed"),
    NOT_FOUND(10003, ErrorCategory.CLIENT_SIDE, "validation failed"),

    INTERNAL_ERROR(20000, ErrorCategory.SERVER_SIDE, "internal error"),
    SPRING_INTERNAL_ERROR(20001, ErrorCategory.SERVER_SIDE, "Spring-detected internal error"),
    DATA_ACCESS_ERROR(20002, ErrorCategory.SERVER_SIDE, "requested resource is not found");

    private final Integer code;
    private final ErrorCategory errorCategory;
    private final String message;

    public String getMessage(Throwable e) {
        return getMessage(this.getMessage() + " - " + e.getMessage());
    }

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(getMessage());
    }

    public String getMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining());
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
        return String.format("%s (%d) : %s", name(), this.getCode(), this.getMessage());
    }

    public enum ErrorCategory {
        NORMAL, CLIENT_SIDE, SERVER_SIDE;
    }
}
