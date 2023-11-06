package com.example.realtimeusage.repository;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.domain.Event;
import com.example.realtimeusage.domain.QEvent;
import com.example.realtimeusage.dto.EventViewResponse;
import com.example.realtimeusage.exception.GeneralException;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

public class EventRepositoryCustomImpl extends QuerydslRepositorySupport implements EventRepositoryCustom {
    public EventRepositoryCustomImpl() {
        super(Event.class);
    }

    @Override
    public Page<EventViewResponse> findEventViewPageBySearchParam(
            String placeName,
            String eventName,
            EventStatus eventStatus,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Pageable pageable
    ) {
        QEvent event = QEvent.event;

        JPQLQuery<EventViewResponse> query = from(event)
                .select(Projections.constructor(
                        EventViewResponse.class,
                        event.id,
                        event.place.name,
                        event.name,
                        event.status,
                        event.startDateTime,
                        event.endDateTime,
                        event.currentNumberOfPeople,
                        event.capacity,
                        event.memo
                ));

        if(StringUtils.hasText(placeName)){
            query.where(event.place.name.containsIgnoreCase(placeName));
        }
        if(StringUtils.hasText(eventName)){
            query.where(event.name.containsIgnoreCase(eventName));
        }
        if(eventStatus!= null){
            query.where(event.status.eq(eventStatus));
        }
        if(startDateTime!= null){
            query.where(event.startDateTime.goe(startDateTime));
        }
        if(endDateTime!= null){
            query.where(event.startDateTime.loe(endDateTime));
        }
        List<EventViewResponse> events = Optional.ofNullable(getQuerydsl())
                .orElseThrow(() -> new GeneralException(ErrorCode.DATA_ACCESS_ERROR, "spring data jpa로부터 query dsl 인스턴스를 가져올 수 없다."))
                .applyPagination(pageable,query).fetch();
        return new PageImpl<>(events,pageable, query.fetchCount());
    }
}
