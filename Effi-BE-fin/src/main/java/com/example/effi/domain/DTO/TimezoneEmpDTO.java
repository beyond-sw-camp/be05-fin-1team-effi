package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.Timezone;
import com.example.effi.domain.Entitiy.TimezoneEmp;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class TimezoneEmpDTO {
    private Long timezoneEmpId;

    private Long timezoneId;
    private Long empId;

    public TimezoneEmpDTO(Long timezoneEmpId, Timezone timezone, Employee emp) {
        this.timezoneEmpId = timezoneEmpId;
        this.timezoneId = timezone.getTimezoneId();
        this.empId = emp.getId();
    }

    public TimezoneEmp toEntity(Timezone timezone, Employee emp) {
        return TimezoneEmp.builder()
                .timezone(timezone)
                .employee(emp)
                .build();
    }
}