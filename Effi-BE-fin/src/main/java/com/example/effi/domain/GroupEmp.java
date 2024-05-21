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

    // group 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    // emp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @Column(name = "group_emp_rank")
    private String groupEmpRank;

    @Column(name = "delete_yn")
    private Boolean deleteYn;

    @Builder
    public GroupEmp(Group group, Employee employee, String groupEmpRank, Boolean deleteYn) {
        this.group = group;
        this.employee = employee;
        this.groupEmpRank = groupEmpRank;
        this.deleteYn = deleteYn;
    }

}
