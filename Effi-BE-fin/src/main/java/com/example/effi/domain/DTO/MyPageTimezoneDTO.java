package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageTimezoneDTO {
    private Long timezoneId;
    private String timezoneName;

    @Builder
    public MyPageTimezoneDTO(long timezoneId, String timezoneName){
        this.timezoneId = timezoneId;
        this.timezoneName = timezoneName;
    }



}
