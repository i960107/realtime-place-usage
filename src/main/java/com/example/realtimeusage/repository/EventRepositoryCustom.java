package com.example.realtimeusage.repository;

import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.dto.EventViewResponse;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRepositoryCustom {
    Page<EventViewResponse> findEventViewPageBySearchParam(
            String placeName,
            String eventName,
            EventStatus eventStatus,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Pageable pageable
    );
}
