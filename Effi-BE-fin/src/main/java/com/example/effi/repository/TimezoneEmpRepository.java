package com.example.effi.repository;

import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.TimezoneEmp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimezoneEmpRepository extends JpaRepository<TimezoneEmp, Long> {
    @Query("SELECT te FROM TimezoneEmp te WHERE te.employee.id = :empId AND te.defaultTimezone = true")
    TimezoneEmp findByEmpIdAndDefaultTimezone(@Param("empId") Long empId);

}
