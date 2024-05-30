package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class GroupRequestDTO {
    private String groupName;
    private List<Long> employeeIds;
    private Long empNo; // 로그인한 사용자의 사번

    @Builder
    public GroupRequestDTO(String groupName, List<Long> employeeIds, Long empNo) {
        this.groupName = groupName;
        this.employeeIds = employeeIds;
        this.empNo = empNo;
    }
}
