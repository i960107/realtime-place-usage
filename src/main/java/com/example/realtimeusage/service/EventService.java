package com.example.realtimeusage.service;

import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.dto.EventDto;
import com.example.realtimeusage.repository.EventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public List<EventDto> getEvents(
            Long placeId,
            String name,
            EventStatus eventStatus,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        return eventRepository.findBy(placeId, name, eventStatus, startDateTime, endDateTime);
    }

    public Optional<EventDto> getEvent(long eventId) {
        return eventRepository.findEventById(eventId);
    }

    public boolean createEvent(EventDto eventDto) {
        return eventRepository.create(eventDto);
    }

    public boolean modifyEvent(long eventId, EventDto eventDto) {
        return eventRepository.update(eventId, eventDto);
    }

    public boolean deleteEvent(long eventId) {
        return eventRepository.delete(eventId);
    }
}
