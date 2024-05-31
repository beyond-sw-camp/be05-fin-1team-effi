package com.example.effi.domain.Entity;

import jakarta.persistence.*;
import lombok.*;

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
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timezone_id")
    private Timezone timezone;

    // emp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @Column(name = "default_timezone")
    private Boolean defaultTimezone;


    @Builder(toBuilder = true)
    public TimezoneEmp(Timezone timezone, Employee employee, Boolean defaultTimezone) {
        this.timezone = timezone;
        this.employee = employee;
        this.defaultTimezone = defaultTimezone;
    }
    
    public void setDefaultTimezone(boolean defaultTimezone) {
        this.defaultTimezone = defaultTimezone;
    }

}
