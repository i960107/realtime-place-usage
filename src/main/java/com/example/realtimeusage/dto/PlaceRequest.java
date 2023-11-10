package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.PlaceType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

// TODO: 2023/11/02 Request객체를 create, update에 모두 사용하면 validation이 어렵지 않나... 
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
