package com.example.realtimeusage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.PlaceType;
import com.example.realtimeusage.domain.Place;
import com.example.realtimeusage.dto.PlaceDto;
import com.example.realtimeusage.exception.GeneralException;
import com.example.realtimeusage.repository.PlaceRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("비지니스 로직 - 장소")
@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {
    @Mock
    private PlaceRepository placeRepository;
    @InjectMocks
    private PlaceService sut;

    @DisplayName("장소를 검색하면, 결과를 출력하여 보여준다.")
    @Test
    void givenNothing_whenSearchingPlaces_thenReturnEntirePlaceList() {
        //given
        given(placeRepository.findAll(any(Predicate.class)))
                .willReturn(List.of(
                        createPlace(1L, PlaceType.COMMON, "레스토랑"),
                        createPlace(2L, PlaceType.SPORTS, "체육관")
                ));
        //when
        List<PlaceDto> places = sut.getPlaces(new BooleanBuilder());

        //then
        assertThat(places).hasSize(2);
        then(placeRepository).should().findAll(any(Predicate.class));
    }

    @DisplayName("장소를 검색하는데 에러가 발생하면, 프로젝트 기본 에러로 전환하여 던진다.")
    @Test
    void givenDataRelatedException_whenSearchingPlaces_thenThrowGeneralException() {
        //given
        given(placeRepository.findAll(any(Predicate.class)))
                .willThrow(new RuntimeException("this is test"));
        //when
        Throwable thrown = catchThrowable(() -> sut.getPlaces(new BooleanBuilder()));

        //then
        assertThat(thrown)
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorCode.DATA_ACCESS_ERROR.getMessage());
        then(placeRepository).should().findAll(any(Predicate.class));
    }

    @DisplayName("장소 ID로 존재하는 장소를 조회하면, 해당 장소 정보를 출력하여 보여준다.")
    @Test
    void givenPlaceId_whenSearchingPlace_thenReturnPlace() {
        //given
        Long placeID = 1L;
        Place place = createPlace(placeID, PlaceType.PARTY, "파티룸");
        given(placeRepository.findById(placeID))
                .willReturn(Optional.of(place));
        //when
        Optional<PlaceDto> result = sut.getPlace(placeID);

        //then
        assertThat(result).isNotNull();
        assertThat(result.hashCode()).isEqualTo(PlaceDto.of(place).hashCode());

        then(placeRepository).should().findById(placeID);
    }

    @DisplayName("존재하지 않는 장소 ID로 조회하면 에러를 발생시킨다.")
    @Test
    void givenNonExistingPlaceId_whenSearchingPlace_thenNull() {
        //given
        Long nonExistingPlaceID = 10L;
        given(placeRepository.findById(nonExistingPlaceID))
                .willReturn(Optional.empty());
        //when
        Optional<PlaceDto> result = sut.getPlace(nonExistingPlaceID);

        //then
        assertThat(result).isEmpty();
        then(placeRepository).should().findById(nonExistingPlaceID);
    }

    @DisplayName("장소 ID로 존재하는 장소를 수정요청하면, 장소 정보를 수정하고 true를 반환한다.")
    @Test
    void givenPlaceIdAndPlaceDto_whenModifyingPlace_thenModifyPlaceAndReturnTrue() {
        //given
        Long placeID = 1L;
        Place originalPlace = createPlace(placeID, PlaceType.PARTY, "파티룸");
        Place updatedPlace = createPlace(placeID, PlaceType.PARTY, "핫한 파티룸");

        given(placeRepository.findById(placeID))
                .willReturn(Optional.of(originalPlace));
        given(placeRepository.save(any()))
                .willReturn(updatedPlace);
        //when
        boolean result = sut.modifyPlace(placeID, PlaceDto.of(updatedPlace));

        //then
        assertThat(result).isTrue();
        then(placeRepository).should().findById(placeID);
        then(placeRepository).should().save(any());
    }

    @DisplayName("존재하지 않는 장소ID로 수정요청하면, 수정을 중지하고 false를 반환한다.")
    @Test
    void givenNonExistingPlaceId_whenModifyingPlace_thenAbortModifyingPlaceAndReturnFalse() {
        //given
        Long nonExistingPlaceId = -100L;
        Place updatedPlace = createPlace(nonExistingPlaceId, PlaceType.PARTY, "핫한 파티룸");
        given(placeRepository.findById(nonExistingPlaceId))
                .willReturn(Optional.empty());
        //when
        boolean result = sut.modifyPlace(nonExistingPlaceId, PlaceDto.of(updatedPlace));

        //then
        assertThat(result).isFalse();
        then(placeRepository).should().findById(nonExistingPlaceId);
        then(placeRepository).shouldHaveNoMoreInteractions();
    }

    @DisplayName("장소ID 없이 수정요청하면, 수정을 중지하고 false를 반환한다.")
    @Test
    void notGivenPlaceId_whenModifyingPlace_thenAbortModifyingPlaceAndReturnFalse() {
        //given
        Place place = createPlace(1L, PlaceType.COMMON, "운동장");
        //when
        boolean result = sut.modifyPlace(null, PlaceDto.of(place));

        //then
        assertThat(result).isFalse();
        then(placeRepository).shouldHaveNoInteractions();
    }

    @DisplayName("장소 정보 없이 수정요청하면, 수정을 중지하고 false를 반환한다.")
    @Test
    void notGivenPlaceDto_whenModifyingPlace_thenAbortModifyingPlaceAndReturnFalse() {
        //when
        boolean result = sut.modifyPlace(1L, null);

        //then
        assertThat(result).isFalse();
        then(placeRepository).shouldHaveNoInteractions();
    }
    @DisplayName("장소 ID로 존재하는 장소를 삭제요청하면, 삭제하고 true를 반환한다.")
    @Test
    void givenPlaceId_whenRemovingPlace_thenRemovePlaceAndReturnTrue() {
        //given
        Long placeID = 1L;
        Place originalPlace = createPlace(placeID, PlaceType.PARTY, "파티룸");

        given(placeRepository.findById(placeID))
                .willReturn(Optional.of(originalPlace));
        //when
        boolean result = sut.removePlace(placeID);

        //then
        assertThat(result).isTrue();
        then(placeRepository).should().findById(placeID);
        then(placeRepository).should().save(any());
    }

    @DisplayName("존재하지 않는 장소ID로 삭제요청하면, 삭제을 중지하고 false를 반환한다.")
    @Test
    void givenNonExistingPlaceId_whenRemovingPlace_thenAbortRemovingAndReturnFalse() {
        //given
        Long nonExistingPlaceId = -100L;
        //when
        boolean result = sut.removePlace(nonExistingPlaceId);

        //then
        assertThat(result).isFalse();
        then(placeRepository).should().findById(nonExistingPlaceId);
        then(placeRepository).shouldHaveNoMoreInteractions();
    }

    @DisplayName("장소ID 없이 삭제요청하면, 삭제을 중지하고 false를 반환한다.")
    @Test
    void notGivenPlaceId_whenRemovingPlace_thenAbortRemovingAndReturnFalse() {
        //when
        boolean result = sut.removePlace(null);

        //then
        assertThat(result).isFalse();
        then(placeRepository).shouldHaveNoInteractions();
    }

    private Place createPlace(Long id, PlaceType type, String name) {
        Place place = Place.builder()
                .name(name)
                .type(type)
                .capacity(30)
                .address("test address")
                .phoneNumber("010-111-1111")
                .enabled(true)
                .build();
        ReflectionTestUtils.setField(place, "id", id);
        return place;
    }

}