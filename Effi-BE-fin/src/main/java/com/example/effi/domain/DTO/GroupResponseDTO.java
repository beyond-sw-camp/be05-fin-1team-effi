package com.example.effi.domain.DTO;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class GroupResponseDTO {
    
    private Long groupId;
    private String groupName;
    private List<Long> employeeIds;
}
