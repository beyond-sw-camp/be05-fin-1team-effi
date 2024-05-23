package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageUpdateDTO {
    private Long empNo;
    private Long timezoneId;

    @Builder
    public MyPageUpdateDTO( Long empNo, Long timezoneId){
        this.empNo = empNo;
        this.timezoneId = timezoneId;
    }

}
