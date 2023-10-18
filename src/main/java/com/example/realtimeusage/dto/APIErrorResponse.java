package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED) // 인스턴스 만들 수 있는 곳 -> dto 패키지 내부 혹은 상속받은 곳
public class APIErrorResponse {
    private final Boolean success;
    private final Integer errorCode;
    private final String message;

    public static APIErrorResponse of(Integer errorCode, String message) {
        return new APIErrorResponse(false, errorCode, message);
    }

    public static APIErrorResponse of(ErrorCode errorCode) {
        return new APIErrorResponse(false, errorCode.getCode(), errorCode.getMessage());
    }

    public static APIErrorResponse of(ErrorCode errorCode, Exception e) {
        return new APIErrorResponse(false, errorCode.getCode(), errorCode.getMessage(e));
    }

    public static APIErrorResponse of(ErrorCode errorCode, String message) {
        return new APIErrorResponse(false, errorCode.getCode(), errorCode.getMessage(message));
    }
}
