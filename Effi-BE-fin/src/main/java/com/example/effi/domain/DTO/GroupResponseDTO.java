package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupResponseDTO {
    private Long groupId;
    private String groupName;
    private String msg;

    @Builder
    public GroupResponseDTO(Long groupId, String groupName, String msg) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.msg = msg;
    }
}