package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.EventStatus;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotBlank.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public record EventRequest(
        @NotBlank String name,
        @NotNull  LocalDateTime startDateTime,
        @NotNull  LocalDateTime endDateTime,
        @NotNull @Positive Integer capacity,
        @NotNull @PositiveOrZero Integer currentNumberOfPeople,
        @NotNull EventStatus status,
        String memo
) {
    public static EventRequest of(
            String name,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Integer capacity,
            Integer currentNumberOfPeople,
            EventStatus status,
            String memo) {
        return new EventRequest(
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
                null,
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
