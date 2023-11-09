package com.example.realtimeusage.controller;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.domain.Place;
import com.example.realtimeusage.dto.PlaceResponse;
import com.example.realtimeusage.exception.GeneralException;
import com.example.realtimeusage.service.PlaceService;
import com.querydsl.core.types.Predicate;
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
@RequestMapping("/places")
@RequiredArgsConstructor
@Validated
public class PlaceController {
    private final PlaceService placeService;

    @GetMapping
    public String places(@QuerydslPredicate(root = Place.class) Predicate predicate, Model model) {
        model.addAttribute("places", placeService.getPlaces(predicate)
                .stream().map(PlaceResponse::of)
                .toList());
        return "/place/index";
    }

    @GetMapping("/{placeId}")
    public String placeDetail(@Positive @PathVariable Long placeId, Model model) {
        model.addAttribute("place", placeService.getPlace(placeId)
                .map(PlaceResponse::of)
                .orElseThrow(() -> new GeneralException(ErrorCode.NOT_FOUND)));
        return "/place/detail";
    }
}
