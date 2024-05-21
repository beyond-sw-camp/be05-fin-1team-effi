package com.example.effi.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.effi.domain.Entitiy.GroupEmp;

@Repository
public interface GroupEmpRepository extends JpaRepository<GroupEmp, Long> {
}
