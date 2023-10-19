package com.example.realtimeusage.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.PlaceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class APIDataResponseTest {
    @DisplayName("데이터가 주어지면 표준 성공 응답을 생성한다.")
    @Test
    void creatingResponseShouldReturnSuccessfulResponse() {
        //given
        PlaceDto data = PlaceDto.of(PlaceType.PARTY,
                "place",
                "place address",
                "0101111111",
                10,
                "");
        //when
        APIDataResponse<String> response = APIDataResponse.of(data);

        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("success", true)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OK.getCode())
                .hasFieldOrPropertyWithValue("message", ErrorCode.OK.getMessage())
                .hasFieldOrPropertyWithValue("data", data);

        assertThat(response.getData())
                .isInstanceOf(PlaceDto.class);
    }

    @DisplayName("데이터가 없으면 비어있는 표준 성공 응답을 생성한다.")
    @Test
    void creatingResponseWithoutDataShouldReturnEmptySuccessfulResponse() {
        //when
        APIDataResponse<String> response = APIDataResponse.empty();

        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("success", true)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OK.getCode())
                .hasFieldOrPropertyWithValue("message", ErrorCode.OK.getMessage())
                .hasFieldOrPropertyWithValue("data", null);
        assertThat(response.getData())
                .isNull();
    }
}