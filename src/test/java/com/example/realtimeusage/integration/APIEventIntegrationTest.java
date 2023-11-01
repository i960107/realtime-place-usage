package com.example.realtimeusage.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.EventStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@Disabled("spring rest repository로 대체됨.")
@SpringBootTest
@AutoConfigureMockMvc
class APIEventIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("[API] [GET] 이벤트 리스트 조회 - 검색 파라미터")
    @Test
    void requestEventWithParametersShouldReturnStandardResponseOfEventList() throws Exception {
        mockMvc.perform(get("/api/events")
                        .queryParam("placeId", "1")
                        .queryParam("name", "오후") //partial match
                        .queryParam("eventStatus", EventStatus.OPENED.name())
                        .queryParam("startDateTime",
                                LocalDateTime.of(2021, 1, 1, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .queryParam("endDateTime",
                                LocalDateTime.of(2021, 1, 1, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.OK.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.OK.getMessage()))
                .andDo(print());
    }

}
