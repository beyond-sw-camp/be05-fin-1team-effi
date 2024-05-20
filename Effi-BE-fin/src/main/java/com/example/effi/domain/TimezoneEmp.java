package com.example.effi.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "timezone_emp")
public class TimezoneEmp {
    @Id
    @Column(name = "timezone_emp_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timezoneEmpId;

    //timezone
    @Column(name = "timezone_id")
    private Long timezoneId;

    //employee
    @Column(name = "emp_id")
    private Long empId;

    @Builder
    public TimezoneEmp(Long timezoneId, Long empId) {
        this.timezoneId = timezoneId;
        this.empId = empId;
    }
}
