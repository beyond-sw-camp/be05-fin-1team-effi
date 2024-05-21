package com.example.effi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupEmpDTO {
    private Long groupEmpId;
    private String groupEmpRank;
    private Boolean deleteYn;

    private Long groupId;
    private Long empId;

    public GroupEmpDTO(Long groupEmpId, String groupEmpRank, Boolean deleteYn, Group group, Employee emp) {
        this.groupEmpId = groupEmpId;
        this.groupEmpRank = groupEmpRank;
        this.deleteYn = deleteYn;
        this.empId = emp.getId();
        this.groupId = group.getGroupId();
    }

    public GroupEmp toEntity(Group group, Employee emp) {
        return GroupEmp.builder()
                .groupEmpRank(groupEmpRank)
                .deleteYn(deleteYn)
                .group(group)
                .employee(emp)
                .build();
    }
}
