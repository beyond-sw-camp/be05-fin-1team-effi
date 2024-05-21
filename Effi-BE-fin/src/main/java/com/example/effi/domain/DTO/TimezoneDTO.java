package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.Timezone;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimezoneDTO {
    private Long timezoneId;
    private String timezoneName;
    private Integer timezoneNum;

    public TimezoneDTO(Long timezoneId, String timezoneName, Integer timezoneNum) {
        this.timezoneId = timezoneId;
        this.timezoneName = timezoneName;
        this.timezoneNum = timezoneNum;
    }

    public Timezone toEntity(){
        return Timezone.builder()
                .timezoneName(timezoneName)
                .timezoneNum(timezoneNum)
                .build();
    }
}
