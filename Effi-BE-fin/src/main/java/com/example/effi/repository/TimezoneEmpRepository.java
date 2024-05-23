package com.example.effi.repository;

import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.TimezoneEmp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimezoneEmpRepository extends JpaRepository<TimezoneEmp, Long> {
    Optional<TimezoneEmp> findByEmployee(Employee employee);

}
