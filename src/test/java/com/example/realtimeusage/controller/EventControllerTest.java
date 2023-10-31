package com.example.realtimeusage.controller;

import com.example.realtimeusage.service.EventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@DisplayName("view 컨트롤러 - 이벤트")
@WebMvcTest(EventController.class)
class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EventService eventService;

    @DisplayName("[VIEW] [GET] 이벤트 리스트 페이지")
    @Test
    void requestEventsPageShouldReturnEventsPage() throws Exception {
        //when & then
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("/event/index"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("events"));
    }

    @DisplayName("[VIEW] [GET] 이벤트 상세정보 페이지")
    @Test
    void requestEventPageShouldReturnEventPage() throws Exception {
        //given
        Long eventId = 1L;
        //when & then
        mockMvc.perform(get("/events/" + eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("/event/detail"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("event"));
    }

}