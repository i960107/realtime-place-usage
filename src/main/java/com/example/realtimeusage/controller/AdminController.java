package com.example.realtimeusage.controller;

import com.example.realtimeusage.constant.AdminOperationStatus;
import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.constant.PlaceType;
import com.example.realtimeusage.domain.Event;
import com.example.realtimeusage.domain.Place;
import com.example.realtimeusage.dto.EventDto;
import com.example.realtimeusage.dto.EventRequest;
import com.example.realtimeusage.dto.EventResponse;
import com.example.realtimeusage.dto.EventViewResponse;
import com.example.realtimeusage.dto.PlaceDto;
import com.example.realtimeusage.dto.PlaceRequest;
import com.example.realtimeusage.dto.PlaceResponse;
import com.example.realtimeusage.exception.GeneralException;
import com.example.realtimeusage.service.EventService;
import com.example.realtimeusage.service.PlaceService;
import com.querydsl.core.types.Predicate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@Validated
@RequiredArgsConstructor
public class AdminController {
    private final EventService eventService;
    private final PlaceService placeService;
    public static final String PLACE_TYPE_OPTION = "placeTypeOption";
    public static final String EVENT_STATUS_OPTION = "eventStatusOption";
    public static final String REDIRECT_URL = "redirectUrl";
    public static final String ADMIN_OPERATION_STATUS = "adminOperationStatus";

    @GetMapping("/places")
    public String adminPlaces(@QuerydslPredicate(root = Place.class) Predicate predicate, Model model) {
        List<PlaceResponse> places = placeService.getPlaces(predicate)
                .stream()
                .map(PlaceResponse::of)
                .toList();

        model.addAttribute("places", places);
        model.addAttribute(PLACE_TYPE_OPTION, PlaceType.values());
        return "/admin/places";
    }

    @GetMapping("/places/{placeId}")
    public String adminPlaceDetail(
            @Positive @PathVariable Long placeId,
            @PageableDefault Pageable pageable,
            Model model) {
        PlaceResponse place = placeService.getPlace(placeId)
                .map(PlaceResponse::of)
                .orElseThrow(() -> new GeneralException(ErrorCode.NOT_FOUND));

        Page<EventDto> events = eventService.getEvents(placeId, pageable);

        model.addAttribute("place", place);
        model.addAttribute("events", new PageImpl<>(
                events.getContent().stream().map(EventViewResponse::of).toList(),
                events.getPageable(),
                events.getTotalElements()
        ));
        model.addAttribute(PLACE_TYPE_OPTION, PlaceType.values());
        model.addAttribute(ADMIN_OPERATION_STATUS, AdminOperationStatus.MODIFY);
        return "/admin/place-detail";
    }

    @GetMapping("/places/new")
    public String newPlace(Model model) {
        model.addAttribute(ADMIN_OPERATION_STATUS, AdminOperationStatus.CREATE);
        model.addAttribute(PLACE_TYPE_OPTION, PlaceType.values());
        return "/admin/place-detail";
    }


    @ResponseStatus(HttpStatus.SEE_OTHER) // Get /post/places로 redirect.
    @PostMapping("/places")
    public String upsertPlace(
            @Valid @ModelAttribute PlaceRequest placeRequest,
            RedirectAttributes redirectAttributes
    ) {
        AdminOperationStatus status =
                placeRequest.id() != null ? AdminOperationStatus.MODIFY : AdminOperationStatus.CREATE;
        boolean result = placeService.upsertPlace(placeRequest.toDto());
        if (!result) {
            throw new GeneralException(ErrorCode.BAD_REQUEST);
        }

        redirectAttributes.addFlashAttribute(ADMIN_OPERATION_STATUS, status);
        redirectAttributes.addFlashAttribute(REDIRECT_URL, "/admin/places");

        return "redirect:/admin/confirm";
    }

    @ResponseStatus(HttpStatus.SEE_OTHER) // Get /post/places로 redirect.
    @DeleteMapping("/places/{placeId}")
    public String deletePlace(
            @Positive @PathVariable Long placeId,
            RedirectAttributes redirectAttributes
    ) {
        boolean result = placeService.removePlace(placeId);
        if (!result) {
            throw new GeneralException(ErrorCode.BAD_REQUEST);
        }

        redirectAttributes.addFlashAttribute(ADMIN_OPERATION_STATUS, AdminOperationStatus.DELETE);
        redirectAttributes.addFlashAttribute(REDIRECT_URL, "/admin/places");

        return "redirect:/admin/confirm";
    }

    @GetMapping("/confirm")
    public String confirm(Model model) {
        if (!model.containsAttribute(REDIRECT_URL)) {
            throw new GeneralException(ErrorCode.BAD_REQUEST);
        }
        return "/admin/confirm";
    }

    @GetMapping("/events")
    public String adminEvents(
            @QuerydslPredicate(root = Event.class) Predicate predicate,
            Model model
    ) {
        // TODO: 2023/11/02 Admin에 해당하는 event list만 반환하도록 수정 필요
        List<EventResponse> events = eventService.getEvents(predicate)
                .stream()
                .map(EventResponse::of)
                .toList();
        model.addAttribute("events", events);
        model.addAttribute(EVENT_STATUS_OPTION, EventStatus.values());

        return "/admin/events";
    }


    @GetMapping("/events/{eventId}")
    public String getEventDetail(
            @Positive @PathVariable Long eventId,
            Model model
    ) {
        EventResponse event = eventService.getEvent(eventId)
                .map(EventResponse::of)
                .orElseThrow(() -> new GeneralException(ErrorCode.NOT_FOUND));
        model.addAttribute("event", event);
        model.addAttribute(ADMIN_OPERATION_STATUS, AdminOperationStatus.MODIFY);
        model.addAttribute(EVENT_STATUS_OPTION, EventStatus.values());

        return "/admin/event-detail";
    }


    @GetMapping("/places/{placeId}/newEvent")
    public String getCreateEventPage(@Positive @PathVariable Long placeId, Model model) {
        EventResponse emptyEvent = placeService.getPlace(placeId)
                .map(EventResponse::empty)
                .orElseThrow(() -> new GeneralException(ErrorCode.NOT_FOUND));
        model.addAttribute("event", emptyEvent);
        model.addAttribute(ADMIN_OPERATION_STATUS, AdminOperationStatus.CREATE);
        model.addAttribute(EVENT_STATUS_OPTION, EventStatus.values());
        return "/admin/event-detail";
    }

    @ResponseStatus(HttpStatus.SEE_OTHER)
    @PostMapping("/places/{placeId}/events")
    public String upsertEvent(
            @Positive @PathVariable Long placeId,
            @Valid EventRequest eventRequest,
            RedirectAttributes redirectAttributes
    ) {
        PlaceDto placeDto = placeService.getPlace(placeId)
                .orElseThrow(() -> new GeneralException(ErrorCode.NOT_FOUND));

        boolean result = eventService.upsertEvent(eventRequest.toDto(placeDto));
        if (!result) {
            throw new GeneralException(ErrorCode.BAD_REQUEST);
        }

        AdminOperationStatus status = eventRequest.id() != null
                ? AdminOperationStatus.MODIFY
                : AdminOperationStatus.CREATE;
        redirectAttributes.addFlashAttribute(ADMIN_OPERATION_STATUS, status);
        redirectAttributes.addFlashAttribute(REDIRECT_URL, "/admin/places/" + placeId);

        // TODO: 2023/11/02 바로 /admin/confirm넘겨주면 안되나? redirect되야하는 페이지는 redirectUrl인데... 
        return "redirect:/admin/confirm";
    }

    @ResponseStatus(HttpStatus.SEE_OTHER)
    @DeleteMapping("/events/{eventId}")
    public String deleteEvent(
            @Positive @PathVariable Long eventId,
            RedirectAttributes redirectAttributes
    ) {
        boolean result = eventService.removeEvent(eventId);
        if (!result) {
            throw new GeneralException(ErrorCode.BAD_REQUEST);
        }

        redirectAttributes.addFlashAttribute(ADMIN_OPERATION_STATUS, AdminOperationStatus.DELETE);
        redirectAttributes.addFlashAttribute(REDIRECT_URL, "/admin/events");
        return "redirect:/admin/confirm";
    }

}
