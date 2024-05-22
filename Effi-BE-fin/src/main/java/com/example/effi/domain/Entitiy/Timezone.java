package com.example.effi.domain.Entitiy;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "timezone")
public class Timezone {
    @Id
    @Column(name = "timezone_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timezoneId;

    @Column(name = "timezone_name", nullable = false)
    private String timezoneName;

    @Column(name = "timezone_num", nullable = false)
    private Integer timezoneNum;

    @Builder
    public Timezone(String timezoneName, Integer timezoneNum) {
        this.timezoneName = timezoneName;
        this.timezoneNum = timezoneNum;
    }
}
