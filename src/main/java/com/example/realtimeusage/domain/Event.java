package com.example.realtimeusage.domain;

import com.example.realtimeusage.constant.EventStatus;
import com.example.realtimeusage.dto.EventDto;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "placeId")
    private Place place;
    private String name;
    @Column(name = "start_datetime")
    private LocalDateTime startDateTime;
    @Column(name = "end_datetime")
    private LocalDateTime endDateTime;
    private Integer capacity;
    private Integer currentNumberOfPeople;
    @Enumerated(value = EnumType.STRING)
    private EventStatus status;
    private String memo;

    @Builder
    public Event(Long id, Place place, String name, LocalDateTime startDateTime, LocalDateTime endDateTime,
                 int capacity, int currentNumberOfPeople, EventStatus status, String memo) {
        this.id = id;
        this.place = place;
        this.name = name;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.capacity = capacity;
        this.currentNumberOfPeople = currentNumberOfPeople;
        this.status = status;
        this.memo = memo;
    }

    public void update(EventDto eventDto) {
        //place는 service layer에서 따로 update. proxy객체 가지고 와야함.
        if (eventDto.name() != null) {
            this.name = eventDto.name();
        }
        if (eventDto.startDateTime() != null) {
            this.startDateTime = eventDto.startDateTime();
        }
        if (eventDto.endDateTime() != null) {
            this.endDateTime = eventDto.endDateTime();
        }
        if (eventDto.capacity() != null) {
            this.capacity = eventDto.capacity();
        }
        if (eventDto.currentNumberOfPeople() != null) {
            this.currentNumberOfPeople = eventDto.currentNumberOfPeople();
        }
        if (eventDto.status() != null) {
            this.status = eventDto.status();
        }
        if (eventDto.memo() != null) {
            this.memo = eventDto.memo();
        }
    }

    public void updateStatus(EventStatus status) {
        this.status = status;
    }
}
