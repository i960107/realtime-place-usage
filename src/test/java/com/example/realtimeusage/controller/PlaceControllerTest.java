package com.example.realtimeusage.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.PlaceType;
import com.example.realtimeusage.dto.PlaceDto;
import com.example.realtimeusage.dto.PlaceResponse;
import com.example.realtimeusage.service.PlaceService;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("View 컨트롤러 - 장소")
@WebMvcTest(PlaceController.class)
class PlaceControllerTest {
    @MockBean
    private PlaceService placeService;
    @Autowired
    private MockMvc mockMvc;

    private PlaceDto createPlaceDto(Long id, PlaceType type, String name) {
        return PlaceDto.of(
                id,
                type,
                name,
                "서울특별시 연희동",
                "010-1111-1111",
                30,
                null,
                true,
                null,
                null
        );
    }

    @DisplayName("[View][GET] 장소 리스트 페이지 - 조건 검색")
    @Test
    void givenQueryParams_whenSearching_thenReturnsPlacesListView() throws Exception {
        //given
        String queryName = "운동장";
        String queryAddress = "서울특별시 연희동";

        // TODO: 2023/11/08 query param Predicate에 잘 들어가는지 체크 안해도 되나?
        given(placeService.getPlaces(any(Predicate.class))).willReturn(List.of(
                createPlaceDto(1L, PlaceType.SPORTS, "운동장1"),
                createPlaceDto(2L, PlaceType.SPORTS, "운동장2")
        ));

        //when
        mockMvc.perform(get("/places")
                        .queryParam("name", queryName)
                        .queryParam("address", queryAddress))
                .andExpect(status().isOk())
                .andExpect(view().name("/place/index"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("places", hasSize(2)));

        //then
        then(placeService).should().getPlaces(any(Predicate.class));
    }

    @DisplayName("[View][GET] 장소 상세 페이지")
    @Test
    void givenPlaceId_whenSearching_thenReturnsPlaceView() throws Exception {
        //given
        Long placeId = 1L;

        given(placeService.getPlace(placeId)).willReturn(
                Optional.of(createPlaceDto(1L, PlaceType.SPORTS, "운동장"))
        );

        //when
        mockMvc.perform(get("/places/" + placeId))
                .andExpect(status().isOk())
                .andExpect(view().name("/place/detail"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("place", isA(PlaceResponse.class)));

        //then
        then(placeService).should().getPlace(placeId);
    }

    @DisplayName("[View][GET] 장소 상세 페이지 - 존재하지 않는 id 검색")
    @Test
    void givenNonExistingPlaceId_whenSearching_thenReturnsErrorView() throws Exception {
        //given
        Long nonExistPlaceId = 100L;

        given(placeService.getPlace(nonExistPlaceId))
                .willReturn(Optional.empty());

        //when
        mockMvc.perform(get("/places/" + nonExistPlaceId))
                .andExpect(status().isOk())
                .andExpect(view().name("/error"))
                .andExpect(model().attribute("errorCode", ErrorCode.NOT_FOUND))
                .andExpect(model().attribute("statusCode", HttpStatus.NOT_FOUND.value()))
                .andExpect(model().attribute("message", containsString(ErrorCode.NOT_FOUND.getMessage())));

        //then
        then(placeService).should().getPlace(nonExistPlaceId);
    }

    @DisplayName("[View][GET] 장소 상세 페이지 - 양수가 아닌 id 검색")
    @Test
    void givenNegativePlaceId_whenSearching_thenReturnsErrorView() throws Exception {
        //given
        Long invalidPlaceId = -1L;

        //when
        mockMvc.perform(get("/places/" + invalidPlaceId))
                .andExpect(status().isOk())
                .andExpect(view().name("/error"))
                .andExpect(model().attribute("errorCode", ErrorCode.BAD_REQUEST))
                .andExpect(model().attribute("statusCode", HttpStatus.BAD_REQUEST.value()))
                .andExpect(model().attribute("message", containsString("placeId")));

        //then
        then(placeService).shouldHaveNoInteractions();
    }
}
