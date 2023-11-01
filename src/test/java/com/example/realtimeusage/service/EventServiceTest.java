package com.example.realtimeusage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.constant.PlaceType;
import com.example.realtimeusage.domain.Event;
import com.example.realtimeusage.domain.Place;
import com.example.realtimeusage.dto.EventDto;
import com.example.realtimeusage.exception.GeneralException;
import com.example.realtimeusage.repository.EventRepository;
import com.example.realtimeusage.repository.PlaceRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @InjectMocks
    EventService sut; //system under test
    @Mock
    EventRepository eventRepository;
    @Mock
    PlaceRepository placeRepository;


    private Event createEvent(Long id, String name, boolean isMorning) {
        LocalDateTime hourStart = LocalDateTime.parse(String.format("2021-01-01T%s:00:00", isMorning ? "09" : "13"));
        LocalDateTime hourEnd = LocalDateTime.parse(String.format("2021-01-01T%s:00:00", isMorning ? "12" : "16"));
        Event event = Event.builder()
                .name(name)
                .place(createPlace(1L))
                .currentNumberOfPeople(20)
                .currentNumberOfPeople(0)
                .capacity(30)
                .status(EventStatus.OPENED)
                .startDateTime(hourStart)
                .endDateTime(hourEnd)
                .build();
        ReflectionTestUtils.setField(event, "id", id);
        return event;
    }

    private Place createPlace(Long id) {
        Place place = Place.builder()
                .type(PlaceType.COMMON)
                .name("test place")
                .address("test address")
                .phoneNumber("010-1234-1234")
                .capacity(30)
                .memo(null)
                .build();
        ReflectionTestUtils.setField(place, "id", id);
        return place;
    }

    @DisplayName("검색 조건 없이 이벤트 검색하면 전체 리스트를 반환한다.")
    @Test
    void requestEventsWithNoParameterShouldReturnAllEvents() {
        //given
        given(eventRepository.findAll(any(Predicate.class)))
                .willReturn(List.of(
                        createEvent(1L, "오전 운동", true),
                        createEvent(1L, "오전 운동", true)
                ));

        //when
        List<EventDto> list = sut.getEvents(new BooleanBuilder());

        //then
        assertThat(list)
                .hasSize(2);
        then(eventRepository).should().findAll(any(Predicate.class));
    }


    @DisplayName("이벤트를 검색하는데 에러가 발생한 경우, 프로젝트 일반 에러로 전환하여 예외 던진다.")
    @Test
    void errorWhileGettingEventsShouldBeConvertedToGeneralExceptionAndBeThrown() {
        //given
        String message = "This is test.";
        RuntimeException e = new RuntimeException(message);
        given(eventRepository.findAll(any(Predicate.class)))
                .willThrow(e);
        //when
        Throwable throwable = catchThrowable(() ->
                sut.getEvents(new BooleanBuilder()));

        //then
        assertThat(throwable)
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage())
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DATA_ACCESS_ERROR);
        then(eventRepository).should().findAll(any(Predicate.class));
    }

    @DisplayName("eventID로 존재하는 이벤트를 조회하면, 해당 이벤트 정보를 보여준다.")
    @Test
    void requestExistingEventShouldReturnEvent() {
        //given
        Long eventId = 1L;
        Event event = createEvent(1L, "오전 운동", true);
        given(eventRepository.findById(eventId)).willReturn(Optional.of(event));
        //when
        Optional<EventDto> result = sut.getEvent(eventId);
        //then
        assertThat(result)
                .isNotNull()
                .get()
                .hasFieldOrPropertyWithValue("id", eventId);
        then(eventRepository).should().findById(eventId);
    }

    @DisplayName("eventID로 존재하지 않는 이벤트를 조회하면, 빈 정보를 출력하여 보여준다.")
    @Test
    void requestNoneExistingEventShouldReturnEmptyOptional() {
        //given
        Long eventId = 2L;
        given(eventRepository.findById(eventId)).willReturn(Optional.empty());

        //when
        Optional<EventDto> result = sut.getEvent(eventId);

        //then
        assertThat(result).isEmpty();
        then(eventRepository).should().findById(eventId);
    }

    @DisplayName("이벤트 정보를 주면, 이벤트를 생성하고 true를 반환한다.")
    @Test
    void requestCreatingEventShouldCreateEventAndReturnTrue() {
        //given
        Event event = createEvent(null, "오후 운동", false);
        EventDto eventDto = EventDto.of(event);
        given(eventRepository.save(event))
                .willReturn(event);
        given(placeRepository.findById(eventDto.getPlaceId()))
                .willReturn(Optional.of(event.getPlace()));

        //when
        boolean result = sut.createEvent(eventDto);

        //then
        assertThat(result).isTrue();
        then(placeRepository).should().findById(eventDto.getPlaceId());
        then(eventRepository).should().save(event);
    }

    @DisplayName("이벤트 ID와 이벤트 정보를 주면, 이벤트 정보를 변경하고 true를 반환한다.")
    @Test
    void requestModifyingEventShouldModifyEventAndReturnTrue() {
        //given
        long eventId = 1L;
        Event originalEvent = createEvent(eventId, "오후 운동", true);
        Event updatedEvent = createEvent(eventId, "오전 운동", false);
        given(eventRepository.findById(eventId)).willReturn(Optional.of(originalEvent));
        given(eventRepository.save(updatedEvent)).willReturn(updatedEvent);
        given(placeRepository.findById(updatedEvent.getPlace().getId())).willReturn(Optional.of(updatedEvent.getPlace()));

        //when
        boolean result = sut.modifyEvent(eventId, EventDto.of(updatedEvent));

        //then
        assertThat(result).isTrue();
        // TODO: 2023/11/01  orignalEvent 제대로 updated되었는지...
        then(eventRepository).should().findById(eventId);
        then(eventRepository).should().save(updatedEvent);
    }

    @DisplayName("eventId를 주지 않으면, 이벤트 정보를 변경을 중단하고 false를 반환한다.")
    @Test
    void requestModifyingEventWithNullEventIdShouldAbortModifyingAndReturnFalse() {
        //given
        Event event = createEvent(null, "오후 운동", false);

        //when
        boolean result = sut.modifyEvent(null, EventDto.of(event));

        //then
        assertThat(result).isFalse();
        then(eventRepository).shouldHaveNoInteractions();
    }

    @DisplayName("존재하지 않는 eventId를 주면, 이벤트 정보를 변경을 중단하고 false를 반환한다.")
    @Test
    void requestModifyingNoneExistingEventShouldAbortModifyingAndReturnFalse() {
        //given
        long noneExistingEventId = 2L;
        Event event = createEvent(noneExistingEventId, "오후 운동", false);

        //when
        boolean result = sut.modifyEvent(noneExistingEventId, EventDto.of(event));

        //then
        assertThat(result).isFalse();
        then(eventRepository).should().findById(noneExistingEventId);
    }

    @DisplayName("정보 변경 중 예외가 발생하면 이벤트 정보를 변경을 중단하고 false를 반환한다.")
    @Test
    void whenDataRelatedExceptionOccursWhileModifyingEventShouldThrowGeneralException() {
        //given
        Long eventId = 1L;
        Event event = createEvent(eventId, "오후 운동", false);
        given(eventRepository.findById(eventId)).willReturn(Optional.of(event));
        given(placeRepository.findById(any())).willReturn(Optional.of(event.getPlace()));
        given(eventRepository.save(any())).willThrow(new RuntimeException("this is test"));

        //when
        Throwable thrown = catchThrowable(() -> sut.modifyEvent(eventId, EventDto.of(event)));

        //then
        assertThat(thrown).isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage());
        then(eventRepository).should().findById(eventId);
        then(eventRepository).should().save(any());
    }

    @DisplayName("이벤트 ID를 주면, 이벤트를 삭제하고 true를 반환한다.")
    @Test
    void requestDeletingExistingEventShouldDeleteEventAndReturnTrue() {
        //given
        long eventId = 1L;
        Event event = createEvent(eventId, "오전 운동", true);
        given(eventRepository.findById(eventId)).willReturn(Optional.of(event));

        //when
        boolean result = sut.removeEvent(eventId);

        //then
        assertThat(result).isTrue();
        then(eventRepository).should().findById(eventId);
        // TODO: 2023/11/01 제대로 status updated되었는지 체크필요
    }

    @DisplayName("존재하지 않는 이벤트 ID를 주면, 이벤트를 삭제하고 false를 반환한다.")
    @Test
    void requestDeletingNoneExistingEventShouldAbortDeletingAndReturnFalse() {
        //given
        long noneExistingEventId = 10L;
        given(eventRepository.findById(noneExistingEventId)).willReturn(Optional.empty());

        //when
        boolean result = sut.removeEvent(noneExistingEventId);

        //then
        assertThat(result).isFalse();
        then(eventRepository).should().findById(noneExistingEventId);
        then(eventRepository).shouldHaveNoMoreInteractions();
    }

}