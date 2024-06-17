package com.example.effi.domain.DTO;

import com.example.effi.domain.Entity.Category;

import com.example.effi.domain.Entity.Dept;
import com.example.effi.domain.Entity.Group;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CategoryDTO {
    private Long categoryNo;
    private Long categoryId;
    private String categoryName;
    private Long deptId;
    private Long groupId;

    public CategoryDTO(Category category) {
        this.categoryNo = category.getCategoryNo();
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();
        if (category.getDept() != null)
            this.deptId = category.getDept().getDeptId();
        else
            this.deptId = null;
        if (category.getGroup() != null)
            this.groupId = category.getGroup().getGroupId();
        else
            this.groupId = null;
    }

    public Category toEntity(Dept dept, Group group) {
        return Category.builder()
                .categoryName(categoryName)
                .categoryId(categoryId)
                .dept(dept)
                .group(group)
                .build();
    }
}
