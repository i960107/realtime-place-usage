package com.example.realtimeusage.service;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.domain.Event;
import com.example.realtimeusage.domain.Place;
import com.example.realtimeusage.dto.EventDto;
import com.example.realtimeusage.dto.EventViewResponse;
import com.example.realtimeusage.exception.GeneralException;
import com.example.realtimeusage.repository.EventRepository;
import com.example.realtimeusage.repository.PlaceRepository;
import com.querydsl.core.types.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
            Place place = placeRepository.getById(eventDto.getPlaceId());
            //placeId가 유효하지 않으면 EntityNotFoundException 발생.
            //cascade = None. 만약, cascde = CREATE면 place 생성됨.. ?
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
            Optional<Event> optionalEvent = eventRepository.findById(eventId);
            if (optionalEvent.isEmpty()) {
                return false;
            }
            Event event = optionalEvent.get();
            event.update(eventDto);

            //place는 수정할 수 없도록 정책 수정
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
            Optional<Event> optionalEvent = eventRepository.findById(eventId);
            if (optionalEvent.isPresent()) {
                Event event = optionalEvent.get();
                event.updateStatus(EventStatus.DELETED);
                eventRepository.save(event);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, e);
        }
    }

    @Transactional(readOnly = true)
    public Page<EventDto> getEvents(Long placeId, Pageable pageable) {
        try {
            Place place = placeRepository.getById(placeId);
            Page<Event> events = eventRepository.findByPlace(place, pageable);
            return new PageImpl<>(
                    events.getContent().stream().map(EventDto::of).toList(),
                    events.getPageable(),
                    events.getTotalElements()
            );
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, e);
        }
    }

    public boolean upsertEvent(EventDto dto) {
        try {
            if (dto == null) {
                return false;
            }
            if (dto.id() == null) {
                return createEvent(dto);
            } else {
                return modifyEvent(dto.id(), dto);
            }
        } catch (Exception exception) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, exception);
        }
    }

    @Transactional(readOnly = true)
    public Page<EventViewResponse> getEventViewResponse(
            String placeName,
            String eventName,
            EventStatus eventStatus,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Pageable pageable
    ) {
        return eventRepository.findEventViewPageBySearchParam(
                placeName,
                eventName,
                eventStatus,
                startDateTime,
                endDateTime,
                pageable
        );
    }
}
