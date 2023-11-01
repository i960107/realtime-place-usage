package com.example.realtimeusage.service;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.domain.Event;
import com.example.realtimeusage.domain.Place;
import com.example.realtimeusage.dto.EventDto;
import com.example.realtimeusage.exception.GeneralException;
import com.example.realtimeusage.repository.EventRepository;
import com.example.realtimeusage.repository.PlaceRepository;
import com.querydsl.core.types.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;

    @Transactional(readOnly = true)
    public List<EventDto> getEvents(Predicate predicate) {
        try {
            return StreamSupport.stream(eventRepository.findAll(predicate).spliterator(), false)
                    .map(EventDto::of)
                    .toList();
        } catch (Exception exception) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, exception);
        }
    }

    //테스트 용 임시 함수
    public List<EventDto> getEvents(
            Long placeId,
            String name,
            EventStatus eventStatus,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        try {
            return null;
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<EventDto> getEvent(long eventId) {
        try {
            return eventRepository.findById(eventId).map(EventDto::of);
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, e);
        }
    }

    public boolean createEvent(EventDto eventDto) {
        try {
            if (eventDto == null) {
                return false;
            }
            // TODO: 2023/11/01
            Place place = placeRepository.findById(eventDto.getPlaceId())
                    .orElseThrow(() -> new GeneralException(ErrorCode.NOT_FOUND));// error대체 필요..
            eventRepository.save(eventDto.toEntity(place));
            return true;
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, e);
        }
    }

    public boolean modifyEvent(Long eventId, EventDto eventDto) {
        // TODO: 2023/11/01 refactoring 할수 없을까?
        try {
            if (eventId == null || eventDto == null) {
                return false;
            }
            if (!eventId.equals(eventDto.id())) {
                return false;
            }

            Optional<Event> optionalEvent = eventRepository.findById(eventId);
            if (optionalEvent.isEmpty()) {
                return false;
            }
            Event event = optionalEvent.get();
            event.update(eventDto);

            if (eventDto.getPlaceId() != null) {
                Optional<Place> place = placeRepository.findById(eventDto.getPlaceId());
                if (place.isEmpty()) {
                    return false;
                }
                event.updatePlace(place.get());
            }
            eventRepository.save(event);
            return true;
        } catch (
                Exception e) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, e);
        }
    }

    public boolean removeEvent(Long eventId) {
        try {
            if (eventId == null) {
                return false;
            }
            Optional<Event> event = eventRepository.findById(eventId);
            if (event.isPresent()) {
                event.get().updateStatus(EventStatus.DELETED);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, e);
        }
    }

}
