package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.ErrorCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class APIDataResponse<T> extends APIErrorResponse {
    private final Object data;

    private APIDataResponse(Object data) {
        super(true, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
        this.data = data;
    }

    public static <T> APIDataResponse<T> of(Object data) {
        return new APIDataResponse<>(data);
    }

    public static <T> APIDataResponse<T> empty() {
        return new APIDataResponse<>(null);
    }
}
