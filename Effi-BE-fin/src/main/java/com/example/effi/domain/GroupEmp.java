package com.example.effi.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "group_emp")
public class GroupEmp {
    @Id
    @Column(name = "group_emp_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupEmpId;

    // group
    @Column(name = "group_id")
    private Long groupId;

    // emp
    @Column(name = "emp_id")
    private Long empId;

    @Column(name = "group_emp_rank")
    private String groupEmpRank;

    @Column(name = "delete_yn")
    private Boolean deleteYn;

    @Builder
    public GroupEmp(Long groupId, Long empId, String groupEmpRank, Boolean deleteYn) {
        this.groupId = groupId; //
        this.empId = empId; //
        this.groupEmpRank = groupEmpRank;
        this.deleteYn = deleteYn;
    }


}
