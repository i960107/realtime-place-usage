package com.example.realtimeusage.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.realtimeusage.dto.EventDto;
import com.example.realtimeusage.service.EventService;
import com.querydsl.core.BooleanBuilder;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@Disabled("spring boot test를 사용한 service layer sociable test 연습 위한 테스트로 실제 테스트 아님")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class EventServiceSociableTest {
    @Autowired
    private EventService sut;

    @DisplayName("검색 조건 없이 이벤트 검색하면 전체 리스트를 반환한다.")
    @Test
    void requestEventsWithNoParameterShouldReturnAllEvents() {
        //given
        //when
        List<EventDto> list = sut.getEvents(new BooleanBuilder());

        //then
        assertThat(list).isEmpty();
    }
}
