package com.example.effi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

    public Group toEntity(Category category) {
        return Group.builder()
                .groupName(groupName)
                .deleteYn(deleteYn)
                .category(category)
                .build();
    }
}
