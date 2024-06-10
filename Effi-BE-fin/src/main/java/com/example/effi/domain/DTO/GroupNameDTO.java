package com.example.effi.domain.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupNameDTO {
    private Long groupId;
    private String groupName;

    public GroupNameDTO(Long groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }
}
