package com.example.realtimeusage.domain;

import com.example.realtimeusage.constant.PlaceType;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class Place extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private PlaceType type;
    private String address;
    private String phoneNumber;
    private int capacity;
    private String memo;

    @Builder
    public Place(String name, PlaceType type, String address, String phoneNumber, int capacity, String memo) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.capacity = capacity;
        this.memo = memo;
    }
}
