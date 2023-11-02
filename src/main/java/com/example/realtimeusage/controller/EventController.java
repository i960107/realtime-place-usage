package com.example.realtimeusage.controller;

import com.example.realtimeusage.domain.Event;
import com.example.realtimeusage.dto.EventResponse;
import com.example.realtimeusage.service.EventService;
import com.querydsl.core.types.Predicate;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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
                .collect(Collectors.toList())
        );
        return "/event/index";
    }

    @GetMapping("/{eventId}")
    public String eventDetail(
            @Positive @NotNull @PathVariable Long eventId,
            Model model) {
        model.addAttribute("event", eventService.getEvent(eventId).map(EventResponse::of));
        return "/event/detail";
    }
}
