package com.example.realtimeusage.repository;

import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.dto.EventDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// TODO: 2023/10/18 default 함수를 정의해서 bean으로 등록시 익명 클래스 구현 필요 없도록.
public interface EventRepository {
    default List<EventDto> findBy(
            Long placeId,
            String name,
            EventStatus eventStatus,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        return null;
    }


    default Optional<EventDto> findEventById(long eventId) {
        return Optional.empty();
    }

    ;
//    public Optional<EventDto> findEventById(long eventId) {
//        if (eventId != 1L) {
//            return Optional.empty();
//        }
//        return Optional.of(EventDto.of(
//                1L,
//                1L,
//                "오전 운동",
//                LocalDateTime.of(2021, 1, 1, 9, 0),
//                LocalDateTime.of(2021, 1, 1, 12, 0),
//                30,
//                20,
//                EventStatus.OPENED,
//                ""
//        ));
//    }

    default boolean create(EventDto eventDto) {
        return true;
    }

    default boolean update(long eventId, EventDto eventDto) {
        return true;
    }

    default boolean delete(long eventId) {
        return true;
    }
}