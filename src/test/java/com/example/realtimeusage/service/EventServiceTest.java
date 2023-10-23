package com.example.realtimeusage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.dto.EventDto;
import com.example.realtimeusage.exception.GeneralException;
import com.example.realtimeusage.repository.EventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.hibernate.TransactionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @InjectMocks
    EventService sut; //system under test
    @Mock
    EventRepository eventRepository;


    private EventDto createEventDto(Long id, String name, boolean isMorning) {
        LocalDateTime hourStart = LocalDateTime.parse(String.format("2021-01-01T%s:00:00", isMorning ? "09" : "13"));
        LocalDateTime hourEnd = LocalDateTime.parse(String.format("2021-01-01T%s:00:00", isMorning ? "12" : "16"));
        return EventDto.of(
                id,
                null,
                name,
                hourStart,
                hourEnd,
                30,
                20,
                EventStatus.OPENED,
                "",
                null,
                null
        );

    }

    @DisplayName("검색 조건 없이 이벤트 검색하면 전체 리스트를 반환한다.")
    @Test
    void requestEventsWithNoParameterShouldReturnAllEvents() {
        //given
        given(eventRepository.findBy(null, null, null, null, null))
                .willReturn(List.of(
                        EventDto.of(1L, 1L, "event1", LocalDateTime.now(), LocalDateTime.now(), 30, 0,
                                EventStatus.OPENED, "", null, null),
                        EventDto.of(2L, 1L, "event2", LocalDateTime.now(), LocalDateTime.now(), 30, 0,
                                EventStatus.OPENED, "", null, null)));

        //when
        List<EventDto> list = sut.getEvents(null, null, null, null, null);

        //then
        assertThat(list)
                .hasSize(2);
        then(eventRepository).should().findBy(null, null, null, null, null);
    }


    @DisplayName("이벤트를 검색하는데 에러가 발생한 경우, 프로젝트 일반 에러로 전환하여 예외 던진다.")
    @Test
    void errorWhileGettingEventsShouldBeConvertedToGeneralExceptionAndBeThrown() {
        //given
        String message= "This is test.";
        RuntimeException e = new RuntimeException(message);
        given(eventRepository.findBy(any(), any(), any(), any(), any()))
                .willThrow(e);
        //when
        Throwable throwable = catchThrowable(() ->
                sut.getEvents(null, null, null, null, null) );

        //then
        assertThat(throwable)
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage())
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DATA_ACCESS_ERROR);
        then(eventRepository).should().findBy(any(), any(), any(), any(), any());
    }

    @DisplayName("eventID로 존재하는 이벤트를 조회하면, 해당 이벤트 정보를 보여준다.")
    @Test
    void requestExistingEventShouldReturnEvent() {
        //given
        Long eventId = 1L;
        EventDto eventDto = createEventDto(1L, "오전 운동", true);
        given(eventRepository.findEventById(eventId)).willReturn(Optional.of(eventDto));
        //when
        Optional<EventDto> result = sut.getEvent(eventId);
        //then
        assertThat(result)
                .isNotNull()
                .get()
                .hasFieldOrPropertyWithValue("eventId", eventId);
        then(eventRepository).should().findEventById(eventId);
    }

    @DisplayName("eventID로 존재하지 않는 이벤트를 조회하면, 빈 정보를 출력하여 보여준다.")
    @Test
    void requestNoneExistingEventShouldReturnEmptyOptional() {
        //given
        Long eventId = 2L;
        given(eventRepository.findEventById(eventId)).willReturn(Optional.empty());

        //when
        Optional<EventDto> result = sut.getEvent(eventId);

        //then
        assertThat(result).isEmpty();
        then(eventRepository).should().findEventById(eventId);
    }

    @DisplayName("이벤트 정보를 주면, 이벤트를 생성하고 true를 반환한다.")
    @Test
    void requestCreatingEventShouldCreateEventAndReturnTrue() {
        //given
        EventDto eventDto = createEventDto(null, "오후 운동", false);
        given(eventRepository.create(eventDto)).willReturn(true);

        //when
        boolean result = sut.createEvent(eventDto);

        //then
        assertThat(result).isTrue();
        then(eventRepository).should().create(eventDto);
    }

    @DisplayName("이벤트 ID와 이벤트 정보를 주면, 이벤트 정보를 변경하고 true를 반환한다.")
    @Test
    void requestModifyingEventShouldModifyEventAndReturnTrue() {
        //given
        long eventId = 1L;
        EventDto eventDto = createEventDto(eventId, "오후 운동", false);
        given(eventRepository.update(eventId, eventDto)).willReturn(true);

        //when
        boolean result = sut.modifyEvent(eventId, eventDto);

        //then
        assertThat(result).isTrue();
        then(eventRepository).should().update(eventId, eventDto);
    }

    @DisplayName("존재하지 않는 eventId를 주면, 이벤트 정보를 변경을 중단하고 false를 반환한다.")
    @Test
    void requestModifyingNoneExistingEventShouldAbortModifyingAndReturnFalse() {
        //given
        long noneExistingEventId = 2L;
        EventDto eventDto = createEventDto(1L, "오후 운동", false);
        given(eventRepository.update(noneExistingEventId, eventDto)).willReturn(false);

        //when
        boolean result = sut.modifyEvent(noneExistingEventId, eventDto);

        //then
        assertThat(result).isFalse();
        then(eventRepository).should().update(noneExistingEventId, eventDto);
    }

    @DisplayName("이벤트 ID를 주면, 이벤트를 삭제하고 true를 반환한다.")
    @Test
    void requestDeletingExistingEventShouldDeleteEventAndReturnTrue() {
        //given
        long eventId = 1L;
        given(eventRepository.delete(eventId)).willReturn(true);

        //when
        boolean result = sut.deleteEvent(eventId);

        //then
        assertThat(result).isTrue();
        then(eventRepository).should().delete(eventId);
    }

    @DisplayName("이벤트 ID를 주면, 이벤트를 삭제하고 false를 반환한다.")
    @Test
    void requestDeletingNoneExistingEventShouldAbortDeletingAndReturnFalse() {
        //given
        long noneExistingEventId = 10L;
        given(eventRepository.delete(noneExistingEventId)).willReturn(false);

        //when
        boolean result = sut.deleteEvent(noneExistingEventId);

        //then
        assertThat(result).isFalse();
        then(eventRepository).should().delete(noneExistingEventId);
    }

}