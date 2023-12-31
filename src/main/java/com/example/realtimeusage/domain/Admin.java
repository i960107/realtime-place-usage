package com.example.realtimeusage.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Admin extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String phoneNumber;
    private String memo;

    @Builder
    public Admin(String email, String password, String nickname, String phoneNumber, String memo) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.memo = memo;
    }

    public UserDetails toUserDetails(){
        return User.builder()
                .username(email)
                .password(password)
                .authorities(List.of())
                .build();
    }

}
