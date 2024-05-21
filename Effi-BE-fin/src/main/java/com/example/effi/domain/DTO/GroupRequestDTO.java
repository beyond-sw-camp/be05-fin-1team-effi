package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GroupRequestDTO {
    private String groupName;
    private List<Long> employeeIds;

    @Builder
    public GroupRequestDTO(String groupName, List<Long> employeeIds) {
        this.groupName = groupName;
        this.employeeIds = employeeIds;
    }
}
