package com.example.effi.repository;

import com.example.effi.domain.Entitiy.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyPageRepository extends JpaRepository<Employee, Long> {

    List<Employee> findAllByEmpNo(Long empNo);

}
