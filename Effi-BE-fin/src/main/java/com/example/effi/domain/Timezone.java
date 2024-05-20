package com.example.effi.domain;

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
    @Column(name = "timezone_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timezoneId;

    @Column(name = "timezone_name")
    private String timezoneName;

    @Column(name = "timezone_num")
    private Integer timezoneNum;

    @Builder
    public Timezone(String timezoneName, Integer timezoneNum) {
        this.timezoneName = timezoneName;
        this.timezoneNum = timezoneNum;
    }
}
