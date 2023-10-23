package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.EventStatus;
import java.time.LocalDateTime;

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
}
