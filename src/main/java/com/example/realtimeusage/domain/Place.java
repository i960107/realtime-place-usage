package com.example.realtimeusage.domain;

import com.example.realtimeusage.constant.PlaceType;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Place {
    private Long id;
    private String name;
    private PlaceType type;
    private String address;
    private String phoneNumber;
    private int capacity;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
