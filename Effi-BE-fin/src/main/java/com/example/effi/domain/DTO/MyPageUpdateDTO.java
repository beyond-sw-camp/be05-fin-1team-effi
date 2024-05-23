package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.TimezoneEmp;
import com.example.effi.domain.Entitiy.Timezone;



@Getter
@NoArgsConstructor
public class MyPageUpdateDTO {

    private Long empId;
    private Long timezoneId;

    @Builder
    public MyPageUpdateDTO(Long empId, Long timezoneId){
        this.empId = empId;
        this.timezoneId = timezoneId;
    }

    public void updateEntity(TimezoneEmp timezoneEmp, Timezone timezone) {
        timezoneEmp.setTimezone(timezone);
    }

}