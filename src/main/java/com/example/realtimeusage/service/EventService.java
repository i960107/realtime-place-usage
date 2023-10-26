package com.example.realtimeusage.service;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.domain.Place;
import com.example.realtimeusage.dto.EventDto;
import com.example.realtimeusage.exception.GeneralException;
import com.example.realtimeusage.repository.EventRepository;
import com.example.realtimeusage.repository.PlaceRepository;
import com.querydsl.core.types.Predicate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
                    .collect(Collectors.toList());
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
            Place place = placeRepository.findById(eventDto.placeId())
                    .orElseThrow(GeneralException::new);// error대체 필요..
            eventRepository.save(eventDto.toEntity(place));
            return true;
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, e);
        }
    }

    public boolean modifyEvent(Long eventId, EventDto eventDto) {
        try {
            if (eventId == null || eventDto == null) {
                return false;
            }
            if (!eventId.equals(eventDto.id())) {
                return false;
            }

            if (eventDto.placeId() != null && placeRepository.findById(eventDto.placeId()).isEmpty()) {
                return false;
            }
            eventRepository.findById(eventId)
                    .ifPresent(event -> {
                        event.update(eventDto);
                        if (eventDto.placeId() != null) {
                            event.updatePlace(placeRepository.findById(eventDto.placeId()).get());
                        }
                    });
            return true;
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, e);
        }
    }

    public boolean deleteEvent(Long eventId) {
        try {
            if (eventId == null) {
                return false;
            }
            eventRepository.findById(eventId)
                    .ifPresent(event -> event.updateStatus(EventStatus.DELETED));
            return true;
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, e);
        }
    }

}
