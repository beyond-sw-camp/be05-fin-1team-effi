package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.Category;
import com.example.effi.domain.Entitiy.Group;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupDTO {
    private Long groupId;
    private String groupName;
    private Boolean deleteYn;

    private Long categoryId;

    public GroupDTO(Long groupId, String groupName, Boolean deleteYn, Category category) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.deleteYn = deleteYn;
        this.categoryId = category.getCategoryId();
    }

    public GroupDTO(Group grp) {
        this.groupId = grp.getGroupId();
        this.groupName = grp.getGroupName();
        this.deleteYn = grp.getDeleteYn();
        this.categoryId = grp.getCategory().getCategoryId();
    }

    public Group toEntity(Category category) {
        return Group.builder()
                .groupName(groupName)
                .deleteYn(deleteYn)
                .category(category)
                .build();
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
