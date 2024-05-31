package com.example.effi.domain.Dto.Group;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class GroupResponseDTO {

    private String groupName;
    private List<Long> employeeIds;
}
