package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.EventStatus;
import java.time.LocalDateTime;

public record EventViewResponse(
        Long id,
        String placeName,
        String eventName,
        EventStatus eventStatus,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        Integer currentNumberOfPeople,
        Integer capacity,
        String memo
) {

    public static EventViewResponse of(
            Long id,
            String placeName,
            String eventName,
            EventStatus eventStatus,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Integer currentNumberOfPeople,
            Integer capacity,
            String memo
    ) {
        return new EventViewResponse(
                id,
                placeName,
                eventName,
                eventStatus,
                startDateTime,
                endDateTime,
                currentNumberOfPeople,
                capacity,
                memo
        );
    }

    public static EventViewResponse of(EventDto eventDto) {
        return new EventViewResponse(
                eventDto.id(),
                eventDto.placeDto().name(),
                eventDto.name(),
                eventDto.status(),
                eventDto.startDateTime(),
                eventDto.endDateTime(),
                eventDto.currentNumberOfPeople(),
                eventDto.capacity(),
                eventDto.memo()
        );
    }
}
