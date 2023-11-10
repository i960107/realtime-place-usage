package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.EventStatus;
import java.time.LocalDateTime;

public record EventResponse(
        Long eventId,
        PlaceDto placeDto,
        String eventName,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        Integer capacity,
        Integer currentNumberOfPeople,
        EventStatus status,
        String memo
) {
    public static EventResponse of(
            Long id,
            PlaceDto placeDto,
            String name,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Integer capacity,
            Integer currentNumberOfPeople,
            EventStatus status,
            String memo) {

        return new EventResponse(
                id,
                placeDto,
                name,
                startDateTime,
                endDateTime,
                capacity,
                currentNumberOfPeople,
                status,
                memo);
    }

    public static EventResponse of(EventDto eventDto) {
        if (eventDto == null) {
            return null; //NPE 방지
        }
        return EventResponse.of(
                eventDto.id(),
                eventDto.placeDto(),
                eventDto.name(),
                eventDto.startDateTime(),
                eventDto.endDateTime(),
                eventDto.capacity(),
                eventDto.currentNumberOfPeople(),
                eventDto.status(),
                eventDto.memo()
        );
    }


    public static EventResponse empty(PlaceDto placeDto) {
        return new EventResponse(
                null,
                placeDto,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public String getPlaceName() {
        return placeDto.name();
    }

    public Long getPlaceId() {
        return placeDto.id();
    }
}
