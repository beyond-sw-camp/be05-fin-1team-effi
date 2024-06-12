package com.example.effi.domain.DTO;

import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Timezone;
import com.example.effi.domain.Entity.TimezoneEmp;

import groovy.transform.builder.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class TimezoneEmpDTO {
    private Long timezoneEmpId;

    private Long timezoneId;
    private Long empId;
    private Boolean defaultTimezone;

    @Builder
    public TimezoneEmpDTO(Long timezoneEmpId, Timezone timezone, Employee emp, Boolean defaultTimezone) {
        this.timezoneEmpId = timezoneEmpId;
        this.timezoneId = timezone.getTimezoneId();
        this.empId = emp.getId();
        this.defaultTimezone = defaultTimezone;
    }
}
