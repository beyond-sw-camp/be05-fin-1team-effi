package com.example.effi.domain.Entity;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timezone_id", nullable = false)
    private Long timezoneId;

    @Column(name = "timezone_name", nullable = false, length = 35)
    private String timezoneName;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "abbreviation", nullable = false, length = 6)
    private String abbreviation;

    @Column(name = "time_start", nullable = false)
    private Long timeStart;

    @Column(name = "gmt_offset", nullable = false)
    private Integer gmtOffset;

    @Column(name = "dst", nullable = false, length = 1)
    private String dst;

    @Builder(toBuilder = true)
    public Timezone(Long timezoneId, String timezoneName, String countryCode, String abbreviation, Long timeStart, Integer gmtOffset, String dst) {
        this.timezoneId = timezoneId;
        this.timezoneName = timezoneName;
        this.countryCode = countryCode;
        this.abbreviation = abbreviation;
        this.timeStart = timeStart;
        this.gmtOffset = gmtOffset;
        this.dst = dst;
    }
}
