package com.example.effi.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.effi.domain.Entitiy.Category;
import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.Group;
import com.example.effi.domain.Entitiy.GroupEmp;

import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GroupEmpRepository extends JpaRepository<GroupEmp, Long> {

    Optional<Category> findByGroupAndEmployee(Group group, Employee employee);

    @Transactional
    @Modifying
    @Query("UPDATE GroupEmp ge SET ge.deleteYn = true WHERE ge.group.groupId = :groupId AND ge.employee.id = :employeeId")
    void updateDeleteYnByGroupIdAndEmployeeId(Long groupId, Long employeeId);
}
