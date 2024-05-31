package com.example.effi.domain.Entity;

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
    @Column(name = "group_emp_id", nullable = false)
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

    @Column(name = "group_emp_rank", nullable = false)
    private String groupEmpRank;

    @Column(name = "delete_yn", nullable = false)
    private Boolean deleteYn;

    @Builder(toBuilder = true)
    public GroupEmp(Long groupEmpId, Group group, Employee employee, String groupEmpRank, Boolean deleteYn) {
        this.groupEmpId = groupEmpId;
        this.group = group;
        this.employee = employee;
        this.groupEmpRank = groupEmpRank;
        this.deleteYn = deleteYn;
    }

}
