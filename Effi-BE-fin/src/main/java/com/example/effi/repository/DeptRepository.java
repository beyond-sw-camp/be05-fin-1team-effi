package com.example.effi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.effi.domain.Entity.Dept;

public interface DeptRepository extends JpaRepository<Dept, Long> {

    Dept findByDeptId(Long deptId);
}
