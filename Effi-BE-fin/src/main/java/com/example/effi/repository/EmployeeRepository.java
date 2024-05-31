package com.example.effi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.effi.domain.Entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
    Employee findByEmpNo(Long empNo);
    
    List<Employee> findByNameContaining(String name);

    List<Employee> findAllByDept_DeptId(Long deptId);
}
