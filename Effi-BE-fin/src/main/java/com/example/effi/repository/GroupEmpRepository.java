package com.example.effi.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Group;
import com.example.effi.domain.Entity.GroupEmp;

@Repository
public interface GroupEmpRepository extends JpaRepository<GroupEmp, Long> {

    Optional<Category> findByGroupAndEmployee(Group group, Employee employee);

    @Transactional
    @Modifying
    @Query("UPDATE GroupEmp ge SET ge.deleteYn = true WHERE ge.group.groupId = :groupId AND ge.employee.id = :employeeId")
    void updateDeleteYnByGroupIdAndEmployeeId(Long groupId, Long employeeId);

    @Query("SELECT COUNT(ge) FROM GroupEmp ge WHERE ge.group.groupId = :groupId AND ge.deleteYn = FALSE")
    Long countActiveMembersInGroup(Long groupId);

    @Modifying
    @Transactional
    @Query("DELETE FROM GroupEmp ge WHERE ge.group.groupId = :groupId")
    void deleteAllByGroupId(Long groupId);

    List<GroupEmp> findAllByGroup_GroupId(Long groupId);

    GroupEmp findByGroup_GroupIdAndEmployee_Id(Long groupId, Long employeeId);

    List<GroupEmp> findAllByEmployee_Id(Long employeeId);
}
