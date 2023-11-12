package com.example.realtimeusage.controller.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BaseErrorController.class)
class BaseErrorControllerTest {
    private final MockMvc mockMvc;

    public BaseErrorControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @DisplayName("[view] 예외 페이지")
    @Test
    void givenAcceppHeader_whenErrorOccurs_thenReturnsErrorPage() throws Exception {
        //when & then
        // no handler mapping error는 적절하지 않음. ResourceHttpRequestHandler에 의해 404 에러만 남.
//        mockMvc.perform(post("/events/wrong-uri")
        //Method Argument Type Mismatch Exception
        // TODO: 2023/11/09 어떻게 BaseErrorController가 호출되는 상황을 만들지?
        mockMvc.perform(get("/events/wrong-uri")
                        .accept(MediaType.TEXT_HTML_VALUE))
                .andExpect(status().isOk())
                .andExpect(view().name("/error"))
                .andDo(print());
    }

//    @DisplayName("[api] 예외 응답")
    // TODO: 2023/11/12  
//    @Test
//    void whenErrorOccurs_thenReturnsErrorResponse() throws Exception {
//        //when & then
//        mockMvc.perform(get("/events/wrong-uri"))
//                .andExpect(status().isNotFound())
//                .andDo(print());
//    }

}