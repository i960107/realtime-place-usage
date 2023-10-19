package com.example.realtimeusage.domain;

import com.example.realtimeusage.constant.EventStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Event {
    private Long id;
    private Long placeId;
    private String name;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int capacity;
    private int currentNumberOfPeople;
    private EventStatus status;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
