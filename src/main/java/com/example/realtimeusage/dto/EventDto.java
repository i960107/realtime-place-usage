package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.domain.Event;
import com.example.realtimeusage.domain.Place;
import java.time.LocalDateTime;
import java.util.Optional;

public record EventDto(
        Long id,
        Long placeId,
        String name,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        Integer capacity,
        Integer currentNumberOfPeople,
        EventStatus status,
        String memo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static EventDto of(
            Long id,
            Long placeId,
            String name,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Integer capacity,
            Integer currentNumberOfPeople,
            EventStatus status,
            String memo,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new EventDto(id, placeId, name, startDateTime,
                endDateTime, capacity, currentNumberOfPeople, status, memo, createdAt, updatedAt);
    }

    public static EventDto of(Event event) {
        return new EventDto(
                event.getId(),
                event.getPlace().getId(),
                event.getName(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                event.getCapacity(),
                event.getCurrentNumberOfPeople(),
                event.getStatus(),
                event.getMemo(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }

    public Event toEntity(Place place) {
        return Event.builder()
                .name(name)
                .place(place)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .capacity(capacity)
                .currentNumberOfPeople(currentNumberOfPeople)
                .status(status)
                .memo(memo)
                .build();
    }
}
