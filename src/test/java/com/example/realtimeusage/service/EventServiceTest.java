package com.example.realtimeusage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.constant.PlaceType;
import com.example.realtimeusage.domain.Event;
import com.example.realtimeusage.domain.Place;
import com.example.realtimeusage.dto.EventDto;
import com.example.realtimeusage.dto.EventViewResponse;
import com.example.realtimeusage.exception.GeneralException;
import com.example.realtimeusage.repository.EventRepository;
import com.example.realtimeusage.repository.PlaceRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@DisplayName("비지니스 로직 - 이벤트")
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
                .id(id)
                .name(name)
                .place(createPlace(1L))
                .currentNumberOfPeople(20)
                .currentNumberOfPeople(0)
                .capacity(30)
                .status(EventStatus.OPENED)
                .startDateTime(hourStart)
                .endDateTime(hourEnd)
                .build();
        return event;
    }

    private Place createPlace(Long id) {
        Place place = Place.builder()
                //event service에서는 이미 영속화된 Place만 사용함
                .id(id)
                .type(PlaceType.COMMON)
                .name("test place")
                .address("test address")
                .phoneNumber("010-1234-1234")
                .capacity(30)
                .memo(null)
                .build();
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

    @DisplayName("이벤트 뷰 데이터를 검색하면, 페이징된 결과를 출력하여 보여준다.")
    @Test
    void givenNothing_whenSearchingEventViewResponse_thenReturnsEventViewResponsePage() {
        //given
        given(eventRepository
                .findEventViewPageBySearchParam(null, null, null, null, null, PageRequest.ofSize(10))
        ).willReturn(new PageImpl<>(List.of(
                EventViewResponse.of(EventDto.of(createEvent(1L, "오전 운동", true))),
                EventViewResponse.of(EventDto.of(createEvent(2L, "오후 운동", false)))
        )));
        //when
        Page<EventViewResponse> result = sut
                .getEventViewResponse(null, null, null, null, null, PageRequest.ofSize(10));

        //then
        //page바로 size로 content size 조회 가능
        assertThat(result).hasSize(2);
        then(eventRepository).should()
                .findEventViewPageBySearchParam(null, null, null, null, null, PageRequest.ofSize(10));
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
                .hasValue(EventDto.of(event));
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

    @DisplayName("이벤트ID로 이벤트를 조회하는 중 데이터 관련 에러가 발생하면, 줄서기 프로젝트 기본 예외로 전환하여 예외 던진다.")
    @Test
    void givenDataRelatedException_whenSearchEvent_thenThrowsGeneralException() {
        //given
        Long eventId = 1L;
        RuntimeException e = new RuntimeException("this is test");
        given(eventRepository.findById(eventId)).willThrow(e);
        //when
        Throwable throwable = catchThrowable(() -> sut.getEvent(eventId));
        //then
        assertThat(throwable)
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage(e));
    }

    @DisplayName("이벤트 정보를 주면, 이벤트를 생성하고 true를 반환한다.")
    @Test
    void requestCreatingEventShouldCreateEventAndReturnTrue() {
        //given
        //event id = null, place id != null 인 상태.
        Event event = createEvent(null, "오후 운동", false);
        EventDto eventDto = EventDto.of(event);
        given(placeRepository.getById(eventDto.getPlaceId()))
                .willReturn(eventDto.placeDto().toEntity());
        given(eventRepository.save(event))
                .willReturn(event);

        //when
        boolean result = sut.createEvent(eventDto);

        //then
        assertThat(result).isTrue();
        then(placeRepository).should().getById(eventDto.getPlaceId());
        then(eventRepository).should().save(event);
    }

    @DisplayName("이벤트 정보를 주지 않으면, 이벤트를 생성을 중지하고 false 반환한다.")
    @Test
    void requestCreatingEventWithoutEventDataShouldAbortCreatingEventAndReturnFalse() {
        //when
        boolean result = sut.createEvent(null);

        //then
        assertThat(result).isFalse();
        then(placeRepository).shouldHaveNoInteractions();
        ;
    }

    @DisplayName("이벤트 생성 중 장소 정보가 틀리거나 없으면, 줄서기 프로젝트 기본 에러로 전환하여 예외 던진다.")
    @Test
    void givenWrongPlaceId_whenCreating_thenThrowsGeneralException() {
        //given
        EventDto eventDto = EventDto.of(createEvent(1L, "오전 운동", true));
        given(placeRepository.getById(eventDto.getPlaceId())).willReturn(null);
        given(eventRepository.save(any(Event.class))).willThrow(EntityNotFoundException.class);
        //when
        Throwable throwable = catchThrowable(() -> sut.createEvent(eventDto));
        //then
        assertThat(throwable).isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage());
        then(placeRepository).should().getById(eventDto.getPlaceId());
        then(eventRepository).should().save(any(Event.class));
    }

    @DisplayName("이벤트 생성 중 데이터 예외가 발생하면,  줄서기 프로젝트 기본 에러로 전환하여 예외 던진다.")
    @Test
    void givenDataException_whenCreating_thenThrowsGeneralException() {
        //given
        EventDto eventDto = EventDto.of(createEvent(1L, "오전 운동", true));
        given(placeRepository.getById(eventDto.getPlaceId())).willReturn(null);
        RuntimeException e = new RuntimeException("this is test");
        given(eventRepository.save(any(Event.class))).willThrow(e);
        //when
        Throwable throwable = catchThrowable(() -> sut.createEvent(eventDto));
        //then
        assertThat(throwable).isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage(e));
        then(placeRepository).should().getById(eventDto.getPlaceId());
        then(eventRepository).should().save(any(Event.class));
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

        //when
        boolean result = sut.modifyEvent(eventId, EventDto.of(updatedEvent));

        //then
        assertThat(result).isTrue();
        assertThat(originalEvent.getName()).isEqualTo(updatedEvent.getName());
        assertThat(originalEvent.getStartDateTime()).isEqualTo(updatedEvent.getStartDateTime());
        assertThat(originalEvent.getEndDateTime()).isEqualTo(updatedEvent.getEndDateTime());
        assertThat(originalEvent.getStatus()).isEqualTo(updatedEvent.getStatus());
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

    @DisplayName("이벤트 변경 중 데이터 예외가 발생하면 이벤트 정보 변경을 중단하고 false를 반환한다.")
    @Test
    void whenDataRelatedExceptionOccursWhileModifyingEventShouldThrowGeneralException() {
        //given
        Long eventId = 1L;
        Event event = createEvent(eventId, "오후 운동", false);
        given(eventRepository.findById(eventId)).willReturn(Optional.of(event));
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

    @DisplayName("이벤트 ID를 주지 않으면, 이벤트 삭제를 중지하고 false를 반환한다.")
    @Test
    void givenNoEventId_whenDeleting_thenReturnsFalse() {
        //when
        boolean result = sut.removeEvent(null);

        //then
        assertThat(result).isFalse();
        then(eventRepository).shouldHaveNoMoreInteractions();
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

    @DisplayName("이벤트 삭제 중 데이터 예외가 발생하면 이벤트 정보 삭제를 중단하고 false를 반환한다.")
    @Test
    void whenDataRelatedExceptionOccursWhileDeletingEventShouldThrowGeneralException() {
        //given
        Long eventId = 1L;
        RuntimeException e = new RuntimeException("this is test");
        given(eventRepository.findById(eventId)).willReturn(Optional.of(createEvent(eventId, "삭제될 이벤트", true)));
        given(eventRepository.save(any())).willThrow(e);

        //when
        Throwable thrown = catchThrowable(() -> sut.removeEvent(eventId));

        //then
        assertThat(thrown).isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage());
        then(eventRepository).should().findById(eventId);
        then(eventRepository).should().save(any());
    }

    @DisplayName("ID가 빠진 이벤트 정보를 주면, 이벤트를 생성하고 true를 반환한다.")
    @Test
    void givenEventDtoWithoutEventId_whenUpserting_thenCreatesEventAndReturnsTrue() {
        //given
        Event event = createEvent(null, "저장될 이벤트", true);
        Long placeId = event.getPlace().getId();
        given(placeRepository.getById(placeId)).willReturn(createPlace(placeId));
        given(eventRepository.save(any())).willReturn(any());
        //when
        boolean result = sut.upsertEvent(EventDto.of(event));
        //then
        assertThat(result).isTrue();
        then(placeRepository).should().getById(placeId);
        then(eventRepository).should(never()).findById(any());
        then(eventRepository).should().save(any(Event.class));
    }

    @DisplayName("ID가 포함된 이벤트 정보를 주면, 이벤트를 수정하고 true를 반환한다.")
    @Test
    void givenEventDtoWithEventId_whenUpserting_thenModifiesEventAndReturnsTrue() {
        //given
        Long eventId = 1L;
        Event originalEvent = createEvent(eventId, "수정 전 이벤트", true);
        Event updatedEvent = createEvent(eventId, "수정 후 이벤트", false);
        Long placeId = originalEvent.getPlace().getId();
        given(eventRepository.findById(eventId)).willReturn(Optional.of(originalEvent));
        given(eventRepository.save(any())).willReturn(any());
        //when
        boolean result = sut.upsertEvent(EventDto.of(updatedEvent));
        //then
        assertThat(result).isTrue();
        assertThat(originalEvent.getName()).isEqualTo(updatedEvent.getName());
        assertThat(originalEvent.getStartDateTime()).isEqualTo(updatedEvent.getStartDateTime());
        assertThat(originalEvent.getEndDateTime()).isEqualTo(updatedEvent.getEndDateTime());
        then(placeRepository).shouldHaveNoInteractions();
        then(eventRepository).should().findById(eventId);
        then(eventRepository).should().save(any(Event.class));
    }

}