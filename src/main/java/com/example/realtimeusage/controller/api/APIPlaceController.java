package com.example.realtimeusage.controller.api;

import com.example.realtimeusage.constant.PlaceType;
import com.example.realtimeusage.dto.APIDataResponse;
import com.example.realtimeusage.dto.PlaceDto;
import com.example.realtimeusage.dto.PlaceResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Deprecated
//@RestController
//@RequestMapping("/api/places")
//@RequiredArgsConstructor
public class APIPlaceController {
    @GetMapping
    public APIDataResponse<List<PlaceResponse>> getPlaces() {
        return APIDataResponse.of(List.of(
                PlaceResponse.of(
                        1L,
                        "배드민턴장",
                        PlaceType.COMMON,
                        "서울시 강남구",
                        "010-1234-5678",
                        30,
                        "신장개업")
        ));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Boolean createPlace() {
        return true;
    }

    @GetMapping("/{placeId}")
    public APIDataResponse<PlaceDto> getPlace(@PathVariable Long placeId) {
        if (placeId != 1) {
            return APIDataResponse.empty();
        }
        return APIDataResponse.of(PlaceDto.of(
                1L,
                PlaceType.COMMON,
                "배드민턴장",
                "서울시 강남구",
                "010-1234-5678",
                30,
                "신장개업",
                true,
                null,
                null)
        );
    }

    @PutMapping("/{placeId}")
    public Boolean modifyPlace(@PathVariable Long placeId) {
        return true;
    }

    @DeleteMapping("/{placeId}")
    public Boolean deletePlace(@PathVariable Long placeId) {
        return true;
    }
}
