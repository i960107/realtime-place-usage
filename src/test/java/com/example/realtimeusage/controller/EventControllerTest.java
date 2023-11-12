package com.example.realtimeusage.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.realtimeusage.config.SecurityConfig;
import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.dto.EventDto;
import com.example.realtimeusage.service.EventService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("view 컨트롤러 - 이벤트")
@WebMvcTest(
        controllers = EventController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EventService eventService;

    @DisplayName("[VIEW][GET] 이벤트 리스트 페이지 - 조건검색")
    @Test
    void requestEventsPageShouldReturnEventsPage() throws Exception {
        given(eventService.getEvents(any()))
                .willReturn(List.of(
                        createEventDto(1L, "운동회 1", true),
                        createEventDto(2L, "운동회 2", false)
                ));

        //when & then
        mockMvc.perform(get("/events")
                        .queryParam("placeId", "1")
                        .queryParam("name", "운동")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("/event/index"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("events"));
    }

    @DisplayName("[VIEW][GET] 이벤트 상세정보 페이지")
    @Test
    void requestEventPageShouldReturnEventPage() throws Exception {
        //given
        Long eventId = 1L;
        given(eventService.getEvent(eventId))
                .willReturn(Optional.of(createEventDto(1L, "운동회 1", true)));
        //when & then
        mockMvc.perform(get("/events/" + eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("/event/detail"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("event"));
    }

    private EventDto createEventDto(Long id, String name, boolean isMorning) {
        LocalDateTime startDateTime = isMorning ?
                LocalDateTime.of(2021, 1, 1, 10, 0) :
                LocalDateTime.of(2021, 1, 1, 13, 0);
        LocalDateTime endDateTime = isMorning ?
                LocalDateTime.of(2021, 1, 1, 12, 0) :
                LocalDateTime.of(2021, 1, 1, 15, 0);
        return EventDto.of(
                id,
                null,
                name,
                startDateTime,
                endDateTime,
                30,
                0,
                EventStatus.OPENED,
                "",
                null,
                null
        );
    }

    @DisplayName("[View][GET] 이벤트 상세 페이지 - 존재하지 않는 id 검색")
    @Test
    void givenNonExistingEventId_whenSearching_thenReturnsErrorView() throws Exception {
        //given
        Long nonExistEventId = 100L;

        given(eventService.getEvent(nonExistEventId))
                .willReturn(Optional.empty());

        //when
        mockMvc.perform(get("/events/" + nonExistEventId))
                .andExpect(status().isOk())
                .andExpect(view().name("/error"))
                .andExpect(model().attribute("errorCode", ErrorCode.NOT_FOUND))
                .andExpect(model().attribute("statusCode", HttpStatus.NOT_FOUND.value()))
                .andExpect(model().attribute("message", containsString(ErrorCode.NOT_FOUND.getMessage())));

        //then
        then(eventService).should().getEvent(nonExistEventId);
    }

    @DisplayName("[View][GET] 이벤트 상세 페이지 - 양수가 아닌 id 검색")
    @Test
    void givenNegativeEventId_whenSearching_thenReturnsErrorView() throws Exception {
        //given
        Long invalidEventId = -1L;

        //when
        mockMvc.perform(get("/events/" + invalidEventId))
                .andExpect(status().isOk())
                .andExpect(view().name("/error"))
                .andExpect(model().attribute("errorCode", ErrorCode.BAD_REQUEST))
                .andExpect(model().attribute("statusCode", HttpStatus.BAD_REQUEST.value()))
                .andExpect(model().attribute("message", containsString("eventId")));

        //then
        then(eventService).shouldHaveNoInteractions();
    }

}