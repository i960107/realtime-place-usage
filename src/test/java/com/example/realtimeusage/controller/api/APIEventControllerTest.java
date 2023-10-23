package com.example.realtimeusage.controller.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.dto.EventDto;
import com.example.realtimeusage.dto.EventRequest;
import com.example.realtimeusage.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(APIEventController.class)
class APIEventControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    public APIEventControllerTest(@Autowired MockMvc mvc, @Autowired ObjectMapper objectMapper) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @DisplayName("[API] [GET] 이벤트 리스트 조회")
    @Test
    void requestEventWithNoParameterShouldReturnStandardResponseOfEventList() throws Exception {
        //given
        given(eventService.getEvents(null, null, null, null, null))
                .willReturn(List.of(createEventDto()));
        //when & then
        mvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].eventId").value(1L))
                .andExpect(jsonPath("$.data[0].name").value("오후 운동"))
                .andExpect(jsonPath("$.data[0].startDateTime")
                        .value(LocalDateTime.of(2021, 1, 1, 13, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.data[0].endDateTime")
                        .value(LocalDateTime.of(2021, 1, 1, 16, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.data[0].capacity").value(24))
                .andExpect(jsonPath("$.data[0].currentNumberOfPeople").value(0))
                .andExpect(jsonPath("$.data[0].status").value(EventStatus.OPENED.name()))
                .andExpect(jsonPath("$.data[0].memo").value("마스크 꼭 착용하세요"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.OK.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.OK.getMessage()));
        then(eventService).should().getEvents(null, null, null, null, null);
    }

    @DisplayName("[API] [GET] 이벤트 리스트 조회 - 검색 파라미터")
    @Test
    void requestEventWithParametersShouldReturnStandardResponseOfEventList() throws Exception {
        //given
        given(eventService.getEvents(any(), any(), any(), any(), any()))
                .willReturn(List.of(createEventDto()));
        //when & then
        mvc.perform(get("/api/events")
                        .queryParam("placeId", "1")
                        .queryParam("name", "오후") //partial match
                        .queryParam("eventStatus", EventStatus.OPENED.name())
                        .queryParam("startDateTime",
                                LocalDateTime.of(2021, 1, 1, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .queryParam("endDateTime",
                                LocalDateTime.of(2021, 1, 1, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].eventId").value(1L))
                .andExpect(jsonPath("$.data[0].name").value("오후 운동"))
                .andExpect(jsonPath("$.data[0].startDateTime")
                        .value(LocalDateTime.of(2021, 1, 1, 13, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.data[0].endDateTime")
                        .value(LocalDateTime.of(2021, 1, 1, 16, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.data[0].capacity").value(24))
                .andExpect(jsonPath("$.data[0].currentNumberOfPeople").value(0))
                .andExpect(jsonPath("$.data[0].status").value(EventStatus.OPENED.name()))
                .andExpect(jsonPath("$.data[0].memo").value("마스크 꼭 착용하세요"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.OK.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.OK.getMessage()));
        then(eventService).should().getEvents(any(), any(), any(), any(), any());
    }

    @DisplayName("[API] [GET] 이벤트 리스트 조회 - 잘못된 검색 파라미터")
    @Test
    void requestEventWithInvalidParametersShouldReturnStandardErrorResponse() throws Exception {
        //given - validation fail. mocking is not needed.
        //when & then
        mvc.perform(get("/api/events")
                        .queryParam("placeId", "-1")
                        .queryParam("name", "오") //partial match
                        .queryParam("eventStatus", EventStatus.OPENED.name())
                        .queryParam("startDateTime",
                                LocalDateTime.of(2021, 1, 1, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .queryParam("endDateTime",
                                LocalDateTime.of(2021, 1, 1, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(containsString(ErrorCode.VALIDATION_ERROR.getMessage())));

        then(eventService).shouldHaveNoInteractions();
    }

    @DisplayName("[API] [POST] 이벤트 생성")
    @Test
    void requestCreatingEventShouldCreateEventAndReturnStandardSuccessfulResponse() throws Exception {
        //given
        EventRequest requestDto = EventRequest.of(
                "오후 운동",
                LocalDateTime.of(2021, 1, 1, 13, 0, 0),
                LocalDateTime.of(2021, 1, 1, 16, 0, 0),
                24,
                0,
                EventStatus.OPENED,
                "마스크 꼭 착용하세요"
        );
        given(eventService.createEvent(any())).willReturn(true);

        //when & then
        mvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.OK.getCode()))
                .andExpect(jsonPath("$.message").value(containsString(ErrorCode.OK.getMessage())));

        then(eventService).should().createEvent(any());
    }

    @DisplayName("[API] [POST] 이벤트 생성 - 잘못된 데이터 입력")
    @Test
    void requestCreatingEventWithInvalidBodyShouldReturnStandardErrorResponse() throws Exception {
        EventRequest requestDto = EventRequest.of(
                "오",
                null,
                null,
                -1,
                -1,
                null,
                "마스크 꼭 착용하세요"
        );

        //when & then
        mvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.SPRING_BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(containsString(ErrorCode.SPRING_BAD_REQUEST.getMessage())));
        then(eventService).shouldHaveNoInteractions();
    }


    private EventDto createEventDto() {
        return EventDto.of(
                1L,
                null,
                "오후 운동",
                LocalDateTime.of(2021, 1, 1, 13, 0, 0),
                LocalDateTime.of(2021, 1, 1, 16, 0, 0),
                24,
                0,
                EventStatus.OPENED,
                "마스크 꼭 착용하세요",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}