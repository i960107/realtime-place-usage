package com.example.realtimeusage.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Admin {
    private Long id;
    private String email;
    private String password;
    private String nickName;
    private String phoneNumber;
    private String memo;
    private LocalDateTime creatdAt;
    private LocalDateTime updatedAt;
}
