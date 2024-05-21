package com.example.effi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeptDTO {
    private Long deptId;
    private String deptName;

    public DeptDTO(Long deptId, String deptName) {
        this.deptId = deptId;
        this.deptName = deptName;
    }

    public Dept toEntity(){
        return Dept.builder()
                .deptName(deptName)
                .build();
    }
}
