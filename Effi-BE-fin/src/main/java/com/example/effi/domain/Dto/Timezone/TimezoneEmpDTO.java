package com.example.effi.domain.Dto.Timezone;

import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Timezone;
import com.example.effi.domain.Entity.TimezoneEmp;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class TimezoneEmpDTO {
    private Long timezoneEmpId;

    private Long timezoneId;
    private Long empId;
    private Boolean defaultTimezone;

    public TimezoneEmpDTO(Long timezoneEmpId, Timezone timezone, Employee emp, Boolean defaultTimezone) {
        this.timezoneEmpId = timezoneEmpId;
        this.timezoneId = timezone.getTimezoneId();
        this.empId = emp.getId();
        this.defaultTimezone = defaultTimezone;
    }

    public TimezoneEmp toEntity(Timezone timezone, Employee emp,Boolean defaultTimezone ) {
        return TimezoneEmp.builder()
                .timezone(timezone)
                .employee(emp)
                .defaultTimezone(defaultTimezone)
                .build();
    }
}
