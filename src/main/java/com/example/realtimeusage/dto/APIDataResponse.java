package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.ErrorCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class APIDataResponse extends APIErrorResponse {
    private final Object data;

    private APIDataResponse(Boolean success, Integer errorCode, String message, Object data) {
        super(success, errorCode, message);
        this.data = data;
    }

    public static APIDataResponse of(Object data) {
        return new APIDataResponse(true, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), data);
    }
}
