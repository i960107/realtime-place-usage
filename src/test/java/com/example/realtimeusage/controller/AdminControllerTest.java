package com.example.realtimeusage.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.realtimeusage.config.SecurityConfig;
import com.example.realtimeusage.constant.AdminOperationStatus;
import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.constant.PlaceType;
import com.example.realtimeusage.dto.EventDto;
import com.example.realtimeusage.dto.EventRequest;
import com.example.realtimeusage.dto.EventResponse;
import com.example.realtimeusage.dto.PlaceDto;
import com.example.realtimeusage.dto.PlaceRequest;
import com.example.realtimeusage.service.EventService;
import com.example.realtimeusage.service.PlaceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@DisplayName("View 컨트롤러 - 어드민")
@WebMvcTest(
        controllers = AdminController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
class AdminControllerTest {
    @MockBean
    private PlaceService placeService;
    @MockBean
    private EventService eventService;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("[view][GET] 어드민 페이지 - 장소 리스트 뷰")
    @Test
    void givenQueryParams_whenRequestingAdminPlacesPage_thenReturnPlacesPage() throws Exception {
        //given
        given(placeService.getPlaces(any())).willReturn(List.of(
                createPlaceDto(1L, "운동장", PlaceType.SPORTS, true),
                createPlaceDto(2L, "파티룸", PlaceType.PARTY, true)
        ));
        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/places")
                        .queryParam("address", "test address"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("/admin/places"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("places", "placeTypeOption"))
                .andExpect(model().attribute("places", hasSize(2)));
        //then
        then(placeService).should().getPlaces(any());
    }

    @DisplayName("[view][GET] 어드민 페이지 - 장소 상세 뷰")
    @Test
    void givenPlaceId_whenRequestingAdminPlacePage_thenReturnPlacePage() throws Exception {
        //given
        Long placeId = 1L;
        given(placeService.getPlace(placeId)).willReturn(
                Optional.of(createPlaceDto(1L, "운동장", PlaceType.SPORTS, true))
        );
        given(eventService.getEvents(any(), any())).willReturn(
                new PageImpl<EventDto>(List.of()) {
                }
        );
        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/places/" + placeId)
                        .queryParam("size", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("/admin/place-detail"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("place", "events", "placeTypeOption", "adminOperationStatus"))
                .andExpect(model().attribute("events", isA(Page.class)));
        //then
        then(placeService).should().getPlace(placeId);
    }

    @DisplayName("[view][GET] 어드민 페이지 - 장소 상세 뷰, 잘못된 placeId")
    @Test
    void givenNonExistingPlaceId_whenRequestingAdminPlacePage_thenReturnPlacePage() throws Exception {
        //given
        Long nonExistingPlaceId = -1L;
        // service 가기전에 Controller validation error발생 ConstraintViolationError-> InternalServerError.
        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/admin/places/" + nonExistingPlaceId))
                .andExpect(status().isOk())
                .andExpect(view().name("/error"))
                .andExpect(model().attribute("errorCode", is(ErrorCode.BAD_REQUEST)))
                .andExpect(model().attribute("statusCode", HttpStatus.BAD_REQUEST.value()))
                .andExpect(model().attribute("message", containsString("placeId")));
        //then
        then(placeService).shouldHaveNoInteractions();
    }

    @DisplayName("[view][GET] 어드민 페이지 - 장소 생성 뷰")
    @Test
    void givenNothing_whenRequestingCreatingPlaceView_thenReturnCreatingPlaceView() throws Exception {
        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/places/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/place-detail"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("adminOperationStatus", AdminOperationStatus.CREATE))
                .andExpect(model().attribute("placeTypeOption", PlaceType.values()));
        //then
        then(placeService).shouldHaveNoInteractions();
    }

    @DisplayName("[view][POST] 어드민 페이지 - 장소 생성")
    @Test
    void givenNewPlace_whenRequestingCreatingPlace_thenSavesPlaceAndReturnsToListPage() throws Exception {
        PlaceRequest newPlace = PlaceRequest.of(
                null,
                "테스트 장소",
                "테스트 주소",
                "02-111-1111",
                PlaceType.PARTY,
                30,
                null);

        given(placeService.upsertPlace(any())).willReturn(true);
        //mock bean안에서 같은 클래스안 함수 호출 없음. mockbean은 내용 없음

        //redirect된 상태 아님.
        mockMvc.perform(
                        post("/admin/places")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(objectToFormData(newPlace)))
                .andExpect(status().isSeeOther())
                .andExpect(view().name("redirect:/admin/confirm"))
                .andExpect(redirectedUrl("/admin/confirm"))
                .andExpect(flash().attribute("redirectUrl", "/admin/places"))
                .andExpect(flash().attribute("adminOperationStatus", AdminOperationStatus.CREATE))
                .andDo(print());

        then(placeService).should().upsertPlace(any());
        then(placeService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("[view][POST] 어드민 페이지 - 장소 수정")
    @Test
    void givenUpdatedPlace_whenRequestingCreatingPlace_thenModifiesPlaceAndReturnsToListPage() throws Exception {
        Long placeId = 1L;
        PlaceRequest updatedPlace = PlaceRequest.of(
                placeId,
                "new 테스트 장소",
                "테스트 주소",
                "02-111-1111",
                PlaceType.PARTY,
                30,
                null);

        given(placeService.upsertPlace(updatedPlace.toDto())).willReturn(true);

        //redirect된 상태 아님.
        mockMvc.perform(
                        post("/admin/places")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(objectToFormData(updatedPlace)))
                .andExpect(status().isSeeOther())
                //controller에서 반환된 뷰 이름을 체크
                .andExpect(view().name("redirect:/admin/confirm"))
                //redirecte된 url을 체크
                .andExpect(redirectedUrl("/admin/confirm"))
                .andExpect(flash().attribute("redirectUrl", "/admin/places"))
                .andExpect(flash().attribute("adminOperationStatus", AdminOperationStatus.MODIFY))
                .andDo(print());

        then(placeService).should().upsertPlace(any());
        then(placeService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("[view][DELETE] 어드민 페이지 - 장소 삭제")
    @Test
    void givenPlaceId_whenRequestingRemovingPlace_thenRemovesPlaceAndReturnsRedirectResponse() throws Exception {
        Long placeId = 1L;
        given(placeService.removePlace(placeId)).willReturn(true);

        //redirect된 상태 아님.
        mockMvc.perform(delete("/admin/places/" + placeId))
                .andExpect(status().isSeeOther())
                .andExpect(view().name("redirect:/admin/confirm"))
                .andExpect(redirectedUrl("/admin/confirm"))
                .andExpect(flash().attribute("adminOperationStatus", AdminOperationStatus.DELETE))
                .andExpect(flash().attribute("redirectUrl", "/admin/places"));

        then(placeService).should().removePlace(any());
    }

    @DisplayName("[view][GET] 어드민 페이지 - 이벤트 리스트 뷰")
    @Test
    void givenNothing_whenRequestingEventsPage_thenReturnEventsPage() throws Exception {
        //given
        given(eventService.getEvents(any()))
                .willReturn(List.of(EventDto.of(
                        1L,
                        createPlaceDto(1L, "핫 파티룸", PlaceType.PARTY, true),
                        "핫 파티룸에서 생일 파티",
                        LocalDateTime.of(2021, 1, 1, 13, 0),
                        LocalDateTime.of(2021, 1, 1, 16, 0),
                        30,
                        0,
                        EventStatus.CLOSED,
                        "",
                        null,
                        null
                )));
        //when
        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/events"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("events", hasSize(1)))
                .andExpect(model().attribute("eventStatusOption", EventStatus.values()));

        //then
        then(eventService).should().getEvents(any());
    }

    @DisplayName("[view][GET] 어드민 페이지 - 이벤트 디테일 및 수정 뷰")
    @Test
    void givenEventId_whenRequestingEventUpdatingPage_thenReturnEventUpdatingPage() throws Exception {
        //given
        Long eventId = 1L;
        given(eventService.getEvent(eventId))
                .willReturn(Optional.of(EventDto.of(
                        eventId,
                        createPlaceDto(1L, "핫 파티룸", PlaceType.PARTY, true),
                        "핫 파티룸에서 생일 파티",
                        LocalDateTime.of(2021, 1, 1, 13, 0),
                        LocalDateTime.of(2021, 1, 1, 16, 0),
                        30,
                        0,
                        EventStatus.CLOSED,
                        "",
                        null,
                        null
                )));
        //when
        mockMvc.perform(get("/admin/events/" + eventId))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/event-detail"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("event", isA(EventResponse.class)))
                .andExpect(model().attribute("adminOperationStatus", AdminOperationStatus.MODIFY))
                .andExpect(model().attribute("eventStatusOption", EventStatus.values()));

        //then
        then(eventService).should().getEvent(eventId);
    }

    @DisplayName("[view][GET] 어드민 페이지 - 이벤트 생성 뷰")
    @Test
    void givenPlaceId_whenRequestingEventCreatingPage_thenReturnEventCreatingPage() throws Exception {
        //given
        Long placeId = 1L;
        given(placeService.getPlace(placeId))
                .willReturn(Optional.of(createPlaceDto(placeId, "파티룸", PlaceType.PARTY, true)));
        //when
        mockMvc.perform(get("/admin/places/" + placeId + "/newEvent"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/event-detail"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("event", isA(EventResponse.class)))
                .andExpect(model().attributeExists("adminOperationStatus", "eventStatusOption"));

        //then
        then(placeService).should().getPlace(placeId);
    }

    @DisplayName("[view][GET] 어드민 페이지 - 이벤트 생성 뷰, 존재하지 않는 장소")
    @Test
    void givenNonExistingPlaceId_whenRequestingEventCreatingPage_thenReturnErrorPage() throws Exception {
        //given
        Long nonExistingPlaceId = 100L;
        //when
        mockMvc.perform(get("/admin/places/" + nonExistingPlaceId + "/newEvent"))
                .andExpect(status().isOk())
                .andExpect(view().name("/error"))
                .andExpect(model().attribute("statusCode", HttpStatus.NOT_FOUND.value()))
                .andExpect(model().attribute("errorCode", ErrorCode.NOT_FOUND))
                .andExpect(model().attribute("message", containsString(ErrorCode.NOT_FOUND.getMessage())));

        //then
        then(placeService).should().getPlace(nonExistingPlaceId);
    }

    @DisplayName("[view][POST] 어드민 페이지 - 이벤트 생성")
    @Test
    void givenNewEvent_whenRequestingCreatingEvent_thenSavesEventAndReturnsToConfirmPage() throws Exception {
        Long placeId = 1L;
        EventRequest newEvent = EventRequest.of(
                null,
                "핫 파티룸에서 생일 파티",
                LocalDateTime.of(2021, 1, 1, 13, 0),
                LocalDateTime.of(2021, 1, 1, 16, 0),
                30,
                0,
                EventStatus.CLOSED,
                ""
        );

        given(placeService.getPlace(placeId)).willReturn(
                Optional.of(createPlaceDto(placeId, "파티룸", PlaceType.PARTY, true)));
//        given(placeService.getPlace(placeId)).willReturn(any());
        given(eventService.upsertEvent(any())).willReturn(true);

        mockMvc.perform(
                        post("/admin/places/" + placeId + "/events")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(objectToFormData(newEvent)))
                .andExpect(status().isSeeOther())
                .andExpect(view().name("redirect:/admin/confirm"))
                .andExpect(redirectedUrl("/admin/confirm"))
                .andExpect(flash().attribute("redirectUrl", "/admin/places/" + placeId))
                .andExpect(flash().attribute("adminOperationStatus", AdminOperationStatus.CREATE))
                .andDo(print());

        then(placeService).should().getPlace(placeId);
        then(eventService).should().upsertEvent(any());
        then(placeService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("[view][POST] 어드민 페이지 - 이벤트 수정")
    @Test
    void givenUpdatedEvent_whenRequestingUpdatingEvent_thenModifiesEventAndReturnConfirmPage() throws Exception {
        Long placeId = 1L;
        Long eventId = 1L;
        EventRequest updatedEvent = EventRequest.of(
                eventId,
                "핫 파티룸에서 생일 파티",
                null,
                null,
                null,
                null,
                null,
                ""
        );
        given(placeService.getPlace(placeId)).willReturn(
                Optional.of(createPlaceDto(placeId, "파티룸", PlaceType.PARTY, true))
        );
        given(eventService.upsertEvent(any())).willReturn(true);

        mockMvc.perform(
                        post("/admin/places/" + placeId + "/events")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(objectToFormData(updatedEvent)))
                .andExpect(status().isSeeOther())
                .andExpect(view().name("redirect:/admin/confirm"))
                .andExpect(redirectedUrl("/admin/confirm"))
                .andExpect(flash().attribute("redirectUrl", "/admin/places/" + placeId))
                .andExpect(flash().attribute("adminOperationStatus", AdminOperationStatus.MODIFY))
                .andDo(print());

        then(placeService).should().getPlace(placeId);
        then(eventService).should().upsertEvent(any());
        then(placeService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("[view][DELETE] 어드민 페이지 - 이벤트 삭제")
    @Test
    void givenEventId_whenRequestDeletingEvent_thenReturnConfirmPage() throws Exception {
        //given
        Long eventId = 1L;
        given(eventService.removeEvent(eventId)).willReturn(true);
        //when
        mockMvc.perform(delete("/admin/events/" + eventId))
                .andExpect(status().isSeeOther())
                .andExpect(view().name("redirect:/admin/confirm"))
                .andExpect(model().hasNoErrors())
                .andExpect(redirectedUrl("/admin/confirm"))
                .andExpect(flash().attribute("redirectUrl", "/admin/events"))
                .andExpect(flash().attribute("adminOperationStatus", AdminOperationStatus.DELETE));

        //then
        then(eventService).should().removeEvent(eventId);
    }


    @DisplayName("[view][GET] 어드민 페이지 - 확인 뷰")
    @Test
    void givenNothing_whenRequestingConfirm_thenReturnConfirmView() throws Exception {
        //when
        mockMvc.perform(get("/admin/confirm")
                        .flashAttr("redirectUrl", "/admin/places")
                        .flashAttr("adminOperationStatus", AdminOperationStatus.CREATE)
                )
                .andExpect(view().name("/admin/confirm"))
                .andExpect(status().isOk());
    }

    private String objectToFormData(Object obj) {
        Map<String, String> map = objectMapper.convertValue(obj, new TypeReference<>() {
        });
        return map.entrySet().stream()
                .map(entry -> entry.getValue() == null
                        ? ""
                        : entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .filter(str -> !str.isBlank())
                .collect(Collectors.joining("&"));
    }

    private PlaceDto createPlaceDto(Long id, String name, PlaceType placeType, boolean enabled) {
        return new PlaceDto(
                id,
                placeType,
                name,
                "test address",
                "010-1111-1111",
                30,
                "",
                enabled,
                null,
                null);
    }

}