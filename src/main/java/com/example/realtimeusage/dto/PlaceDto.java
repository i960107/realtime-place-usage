package com.example.realtimeusage.dto;

import com.example.realtimeusage.constant.PlaceType;

public record PlaceDto(
        PlaceType type,
        String name,
        String address,
        String phoneNumber,
        Integer capacity,
        String memo
) {
    public static PlaceDto of(
            PlaceType type,
            String name,
            String address,
            String phoneNumber,
            Integer capacity,
            String memo) {
        return new PlaceDto(type, name, address, phoneNumber, capacity, memo);
    }

}
