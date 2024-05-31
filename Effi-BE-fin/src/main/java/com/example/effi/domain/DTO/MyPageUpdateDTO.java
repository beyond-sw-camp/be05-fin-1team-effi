package com.example.effi.domain.DTO;

import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Timezone;
import com.example.effi.domain.Entity.TimezoneEmp;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



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
