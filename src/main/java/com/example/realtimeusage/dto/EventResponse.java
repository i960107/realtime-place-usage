package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.EventStatus;
import java.time.LocalDateTime;

public record EventResponse(
        Long eventId,
        Long placeId,
        String name,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        Integer capacity,
        Integer currentNumberOfPeople,
        EventStatus status,
        String memo
) {
    public static EventResponse of(
            Long id,
            Long placeId,
            String name,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Integer capacity,
           Integer currentNumberOfPeople,
            EventStatus status,
            String memo) {

        return new EventResponse(id, placeId, name, startDateTime,
                endDateTime, capacity, currentNumberOfPeople, status, memo);
    }

    public static EventResponse from(EventDto eventDto) {
        if (eventDto == null) {
            return null; //NPE 방지
        }
        return EventResponse.of(
                eventDto.id(),
                eventDto.placeId(),
                eventDto.name(),
                eventDto.startDateTime(),
                eventDto.endDateTime(),
                eventDto.capacity(),
                eventDto.currentNumberOfPeople(),
                eventDto.status(),
                eventDto.memo()
        );
    }


}
