package com.example.effi.domain.DTO;

import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Group;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupDTO {
    private Long groupId;
    private String groupName;
    private Boolean deleteYn;

    public GroupDTO(Long groupId, String groupName, Boolean deleteYn) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.deleteYn = deleteYn;
    }

    public GroupDTO(Group grp) {
        this.groupId = grp.getGroupId();
        this.groupName = grp.getGroupName();
        this.deleteYn = grp.getDeleteYn();
    }

    public Group toEntity() {
        return Group.builder()
                .groupName(groupName)
                .deleteYn(deleteYn)
                .build();
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
