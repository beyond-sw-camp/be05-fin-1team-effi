package com.example.effi.repository;

import com.example.effi.domain.Entitiy.Dept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeptRepository extends JpaRepository<Dept, Long> {

}
