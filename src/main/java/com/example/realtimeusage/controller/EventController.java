package com.example.realtimeusage.controller;

import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.dto.EventResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
//    private final EventService eventService;

    @GetMapping
    public String events(Model model) {
        List<EventResponse> events = List.of(
                EventResponse.of(
                        1L,
                        1L,
                        "오후 운동",
                        LocalDateTime.of(2021, 1, 1, 13, 0, 0),
                        LocalDateTime.of(2021, 1, 1, 16, 0, 0),
                        24,
                        0,
                        EventStatus.OPENED,
                        "마스크 착용 필수"
                ),
                EventResponse.of(
                        2L,
                        1L,
                        "오후 운동",
                        LocalDateTime.of(2021, 1, 1, 13, 0, 0),
                        LocalDateTime.of(2021, 1, 1, 16, 0, 0),
                        24,
                        0,
                        EventStatus.OPENED,
                        "마스트 착용 필수"
                )
        );
        model.addAttribute("events", events);
        return "/event/index";
    }

    @GetMapping("/{eventId}")
    public String eventDetail(@PathVariable Long eventId, Model model) {
        EventResponse event = EventResponse.of(
                1L,
                1L,
                "오후 운동",
                LocalDateTime.of(2021, 1, 1, 13, 0, 0),
                LocalDateTime.of(2021, 1, 1, 16, 0, 0),
                0,
                24,
                EventStatus.OPENED,
                "마스트 착용 필수"
        );
        model.addAttribute("event", event);
        return "/event/detail";
    }
}
