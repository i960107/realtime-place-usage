package com.example.realtimeusage.controller;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.domain.Event;
import com.example.realtimeusage.dto.EventResponse;
import com.example.realtimeusage.dto.EventViewResponse;
import com.example.realtimeusage.exception.GeneralException;
import com.example.realtimeusage.service.EventService;
import com.querydsl.core.types.Predicate;
import java.time.LocalDateTime;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;

    @GetMapping
    public String events(Model model, @QuerydslPredicate(root = Event.class) Predicate predicate) {
        model.addAttribute("events", eventService.getEvents(predicate)
                .stream().map(EventResponse::of)
                .toList());
        return "/event/index";
    }

    @GetMapping("/{eventId}")
    public String eventDetail(
            @Positive @PathVariable Long eventId,
            Model model) {
        EventResponse event = eventService.getEvent(eventId)
                .map(EventResponse::of)
                .orElseThrow(() -> new GeneralException(ErrorCode.NOT_FOUND));
        model.addAttribute("event", event);
        return "/event/detail";
    }

    // TODO: 2023/11/06 뭐하는 API이지?  -> event검색.. predictae로 검색하는것과
    // custom Interface
    @GetMapping("/custom")
    public String customEvent(
            @Size(min = 2) String placeName,
            @Size(min = 2) String eventName,
            EventStatus eventStatus,
            @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime startDateTime,
            @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime endDateTime,
            Pageable pageable,
            Model model) {
        Page<EventViewResponse> event = eventService.getEventViewResponse(
                placeName,
                eventName,
                eventStatus,
                startDateTime,
                endDateTime,
                pageable
        );
        model.addAttribute("event", event);
        return "/event/index";
    }
}
