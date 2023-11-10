package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.PlaceType;

public record PlaceResponse(
        Long id,
        String name,
        PlaceType type,
        String address,
        String phoneNumber,
        int capacity,
        String memo
) {
    public static PlaceResponse of(
            Long id,
            String name,
            PlaceType type,
            String address,
            String phoneNumber,
            int capacity,
            String memo) {
        return new PlaceResponse(id, name, type, address, phoneNumber, capacity, memo);
    }

    public static PlaceResponse of(PlaceDto placeDto) {
        return new PlaceResponse(
                placeDto.id(),
                placeDto.name(),
                placeDto.type(),
                placeDto.address(),
                placeDto.phoneNumber(),
                placeDto.capacity(),
                placeDto.memo()
        );
    }
}
