package com.example.effi.domain.DTO;

import java.util.List;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class GroupResponseDTO {

    private String groupName;
    private List<Long> employeeIds;
}
