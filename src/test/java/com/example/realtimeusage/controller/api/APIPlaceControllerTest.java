package com.example.realtimeusage.controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.PlaceType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@Deprecated
@Disabled("API 컨트롤러를 REST Repository 컨트롤러로 대체")
@DisplayName("API 컨트롤러 - 장소")
@WebMvcTest(APIPlaceController.class)
class APIPlaceControllerTest {
    private final MockMvc mockMvc;

    public APIPlaceControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @DisplayName("[API] [GET] 장소 리스트 조회")
    @Test
    void requestPlaceListShouldReturnStandardResponseOfPlaceList() throws Exception {
        //when & then
        mockMvc.perform(get("/api/places"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].type").value(PlaceType.COMMON.name()))
                .andExpect(jsonPath("$.data[0].name").value("배드민턴장"))
                .andExpect(jsonPath("$.data[0].address").value("서울시 강남구"))
                .andExpect(jsonPath("$.data[0].phoneNumber").value("010-1234-5678"))
                .andExpect(jsonPath("$.data[0].capacity").value(30))
                .andExpect(jsonPath("$.data[0].memo").value("신장개업"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.OK.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.OK.getMessage()));
    }

    @DisplayName("[API] [GET] 장소 조회")
    @Test
    void requestPlaceShouldReturnPlaceDetail() throws Exception {
        //given
        int placeId = 1;

        //when & then
        mockMvc.perform(get("/api/places/{placeId}", placeId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.type").value(PlaceType.COMMON.name()))
                .andExpect(jsonPath("$.data.name").value("배드민턴장"))
                .andExpect(jsonPath("$.data.address").value("서울시 강남구"))
                .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"))
                .andExpect(jsonPath("$.data.capacity").value(30))
                .andExpect(jsonPath("$.data.memo").value("신장개업"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.OK.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.OK.getMessage()));
    }

    @DisplayName("[API] [GET] 장소 조회 - 장소 없는 경우")
    @Test
    void requestNotExistPlaceShouldReturnError() throws Exception {
        //given
        int placeId = 2;

        //when & then
        mockMvc.perform(get("/api/places/{placeId}", placeId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.OK.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.OK.getMessage()));
    }
}