package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.PlaceType;
import com.example.realtimeusage.domain.Place;
import java.time.LocalDateTime;

public record PlaceDto(
        Long id,
        PlaceType type,
        String name,
        String address,
        String phoneNumber,
        Integer capacity,
        String memo,
        Boolean enabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // test
    public static PlaceDto of(
            Long id,
            PlaceType type,
            String name,
            String address,
            String phoneNumber,
            Integer capacity,
            String memo,
            Boolean enabled,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new PlaceDto(id, type, name, address, phoneNumber, capacity, memo, enabled, createdAt, updatedAt);
    }

    //place -> PlaceDto -> placeResponse
    public static PlaceDto of(Place place) {
        return new PlaceDto(
                place.getId(),
                place.getType(),
                place.getName(),
                place.getAddress(),
                place.getPhoneNumber(),
                place.getCapacity(),
                place.getMemo(),
                place.isEnabled(),
                place.getCreatedAt(),
                place.getUpdatedAt()
        );
    }

    //save or update
    public Place toEntity() {
        return Place.builder()
                .id(id)
                .type(type)
                .name(name)
                .address(address)
                .phoneNumber(phoneNumber)
                .capacity(capacity)
                .enabled(enabled)
                .memo(memo)
                .build();
    }
}
