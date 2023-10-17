package com.example.realtimeusage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/places")
    public String adminPlaces() {
        return "/admin/places";
    }

    @GetMapping("/places/{placeId}")
    public String adminPlaceDetail(@PathVariable Long placeId) {
        return "/admin/place-detail";
    }

    @GetMapping("/events")
    public String adminEvents() {
        return "/admin/events";
    }

    @GetMapping("/events/{eventId}")
    public String adminEventDetail(@PathVariable Long eventId) {
        return "/admin/event-detail";
    }
}
