package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.EventStatus;
import java.time.LocalDateTime;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record EventRequest(
        @Positive Long id,
        String name,
        @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime startDateTime,
        @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime endDateTime,
        @Positive Integer capacity,
        @PositiveOrZero Integer currentNumberOfPeople,
        EventStatus status,
        String memo
) {
    public static EventRequest of(
            Long id,
            String name,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Integer capacity,
            Integer currentNumberOfPeople,
            EventStatus status,
            String memo) {
        return new EventRequest(
                id,
                name,
                startDateTime,
                endDateTime,
                capacity,
                currentNumberOfPeople,
                status,
                memo
        );
    }

    public EventDto toDto(PlaceDto placeDto) {
        return EventDto.of(
                id,
                placeDto,
                name,
                startDateTime,
                endDateTime,
                capacity,
                currentNumberOfPeople,
                status,
                memo,
                null,
                null
        );
    }
}
