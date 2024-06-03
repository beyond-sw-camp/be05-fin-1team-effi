package com.example.effi.domain.DTO;


import com.example.effi.domain.Entity.Timezone;

import groovy.transform.builder.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimezoneDTO {
    private Long timezoneId;
    private String timezoneName;
    private String countryCode;
    private String abbreviation;
    private Long timeStart;
    private Integer gmtOffset;
    private String dst;

    @Builder
    public TimezoneDTO(Long timezoneId, String timezoneName, String countryCode, String abbreviation, Long timeStart, Integer gmtOffset, String dst) {
        this.timezoneId = timezoneId;
        this.timezoneName = timezoneName;
        this.countryCode = countryCode;
        this.abbreviation = abbreviation;
        this.timeStart = timeStart;
        this.gmtOffset = gmtOffset;
        this.dst = dst;
    }

    public Timezone toEntity(){
        return Timezone.builder()
                .timezoneName(timezoneName)
                .countryCode(countryCode)
                .abbreviation(abbreviation)
                .timeStart(timeStart)
                .gmtOffset(gmtOffset)
                .dst(dst)
                .build();
    }

    public static TimezoneDTO fromEntity(Timezone timezone) {
        return new TimezoneDTO(
                timezone.getTimezoneId(),
                timezone.getTimezoneName(),
                timezone.getCountryCode(),
                timezone.getAbbreviation(),
                timezone.getTimeStart(),
                timezone.getGmtOffset(),
                timezone.getDst()
        );
    }
}
