package com.example.realtimeusage.controller.api;

import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.constant.PlaceType;
import com.example.realtimeusage.dto.APIDataResponse;
import com.example.realtimeusage.dto.EventRequest;
import com.example.realtimeusage.dto.EventResponse;
import com.example.realtimeusage.dto.PlaceDto;
import com.example.realtimeusage.service.EventService;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Validated
public class APIEventController {
    private final EventService eventService;

    @GetMapping
    public APIDataResponse<List<EventResponse>> getEvents(
            @Positive Long placeId,
            @Size(min = 2) String name,
            EventStatus eventStatus,
            @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime startDateTime,
            @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime endDateTime
    ) {
        return APIDataResponse.of(List.of(EventResponse.of(
                1L,
                PlaceDto.of(
                        1L,
                        PlaceType.SPORTS,
                        "배드민턴장",
                        "서울시 가나구 다라동",
                        "010-1111-2222",
                        0,
                        null,
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ),
                "오후 운동",
                LocalDateTime.of(2021, 1, 1, 13, 0, 0),
                LocalDateTime.of(2021, 1, 1, 16, 0, 0),
                0,
                24,
                EventStatus.OPENED,
                "마스크 꼭 착용하세요"
        )));
    }

    @GetMapping("/{eventId}")
    public APIDataResponse<EventResponse> getEvent(@Positive @PathVariable Long eventId) {
        return APIDataResponse.of(eventService.getEvent(eventId).orElse(null));
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public APIDataResponse<String> createEvent(
            @Valid @RequestBody EventRequest eventRequest) {
        boolean result = eventService.createEvent(eventRequest.toDto(null));
        return APIDataResponse.of(result);
    }


    @PutMapping("/{eventId}")
    public APIDataResponse<String> modifyEvent(
            @Positive @PathVariable Long eventId,
            @Valid @RequestBody EventRequest eventRequest
    ) {
        boolean result = eventService.modifyEvent(eventId, eventRequest.toDto(null));
        return APIDataResponse.of(Boolean.toString(result));
    }

    @DeleteMapping("/{eventId}")
    public APIDataResponse<String> deleteEvent(@Positive @PathVariable Long eventId) {
        boolean result = eventService.removeEvent(eventId);
        return APIDataResponse.of(Boolean.toString(result));
    }


}
