package com.example.effi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.effi.domain.Entity.TimezoneEmp;

import java.util.List;
import java.util.Optional;

public interface TimezoneEmpRepository extends JpaRepository<TimezoneEmp, Long> {
    @Query("SELECT te FROM TimezoneEmp te WHERE te.employee.id = :empId AND te.defaultTimezone = true")
    Optional<TimezoneEmp> findByEmpIdAndDefaultTimezone(@Param("empId") Long empId);

    Optional<TimezoneEmp> findByEmployeeIdAndTimezone_TimezoneId(Long empId, Long timezoneId);
    Optional<TimezoneEmp> findByEmployeeIdAndDefaultTimezone(Long empId, boolean isDefault);
    List<TimezoneEmp> findByEmployeeId(Long empId);

    @Query("SELECT COUNT(te) FROM TimezoneEmp te WHERE te.employee.id = :empId")
    long countByEmployeeId(@Param("empId") Long empId);

    @Query("SELECT COUNT(te) FROM TimezoneEmp te WHERE te.defaultTimezone = true")
    long countByDefaultTimezone(boolean defaultTimezone);
}
