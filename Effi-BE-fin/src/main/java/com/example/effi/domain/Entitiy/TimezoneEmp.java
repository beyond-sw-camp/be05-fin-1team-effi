package com.example.effi.domain.Entitiy;

import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.Timezone;
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
    @Column(name = "timezone_emp_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timezoneEmpId;

    //timezone
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timezone_id")
    private Timezone timezone;

    // emp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @Column(name = "default_timezone")
    private Boolean defaultTimezone;


    @Builder
    public TimezoneEmp(Timezone timezone, Employee employee, Boolean defaultTimezone) {
        this.timezone = timezone;
        this.employee = employee;
        this.defaultTimezone = defaultTimezone;
    }

    public void setTimezone(Timezone timezone) {
        this.timezone = timezone;
    }
}
