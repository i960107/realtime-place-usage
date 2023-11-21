package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.PlaceType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

// create, update 요청 모두에 사용됨.
// update(HTTP PUT)시에도 모든 항목 값 채워져서 들어옴
// -> validation 같은 로직 사용해도 괜찮음.
public record PlaceRequest(
        @Positive Long id,
        @NotBlank String name,
        @NotBlank String address,
        @NotBlank String phoneNumber,
        @NotNull PlaceType type,
        @NotNull @PositiveOrZero Integer capacity,
        String memo
) {
    public static PlaceRequest of(
            Long id,
            String name,
            String address,
            String phoneNumber,
            PlaceType type,
            Integer capacity,
            String memo) {
        return new PlaceRequest(id, name, address, phoneNumber, type, capacity, memo);
    }

    public PlaceDto toDto() {
        return PlaceDto.of(id, type, name, address, phoneNumber, capacity, memo, true, null, null);
    }
}
