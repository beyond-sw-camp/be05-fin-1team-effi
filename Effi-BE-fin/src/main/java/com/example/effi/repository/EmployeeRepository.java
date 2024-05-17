package com.example.effi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.effi.domain.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
    Employee findByEmpNo(Long empNo);
}
