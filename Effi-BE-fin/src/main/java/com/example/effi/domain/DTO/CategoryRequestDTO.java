package com.example.effi.domain.DTO;

import com.example.effi.domain.Entity.Category;

import com.example.effi.domain.Entity.Dept;
import com.example.effi.domain.Entity.Group;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@NoArgsConstructor
public class CategoryRequestDTO {
    private Long categoryId;
    private Long deptId;
    private Long groupId;
}
