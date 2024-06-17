package com.example.effi.service;

import com.example.effi.domain.DTO.CategoryRequestDTO;
import com.example.effi.domain.DTO.CategoryResponseDTO;
import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Dept;
import com.example.effi.domain.Entity.Group;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.DeptRepository;
import com.example.effi.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final DeptRepository deptRepository;
    private final GroupRepository groupRepository;

    //id로 찾기 - 1 2 3 4 기존 거
    public CategoryResponseDTO findCategory(Long categoryId){
        Category category = categoryRepository.findByCategoryId(categoryId);
        return new CategoryResponseDTO(category);
    }

    // add 전체 입력
    public CategoryResponseDTO addCategory(CategoryRequestDTO categoryRequestDTO){
        String categoryName = "";

        if (categoryRequestDTO.getCategoryId() == 1)
            categoryName = "회사";
        else if (categoryRequestDTO.getCategoryId() == 2)
            categoryName = "부서";
        else if (categoryRequestDTO.getCategoryId() == 3)
            categoryName = "그룹";
        else
            categoryName = "개인";

        Dept dept = deptRepository.findById(categoryRequestDTO.getDeptId()).orElse(null);
        Group group = groupRepository.findById(categoryRequestDTO.getGroupId()).orElse(null);

        return new CategoryResponseDTO(categoryRepository.save(
                Category.builder()
                        .categoryId(categoryRequestDTO.getCategoryId())
                        .categoryName(categoryName)
                        .dept(dept)
                        .group(group)
                        .build()
        ));
    }

    // 부서 입력 2
    public CategoryResponseDTO addCategoryByDept(Long deptId){
        Dept dept = deptRepository.findById(deptId).orElse(null);

        return new CategoryResponseDTO(categoryRepository.save(
                Category.builder()
                        .categoryId(2L)
                        .categoryName("부서")
                        .dept(dept)
                        .group(null)
                        .build()
        ));
    }

    // 그룹 입력 3
    public CategoryResponseDTO addCategoryByGroup(Long groupId){
        Group group = groupRepository.findById(groupId).orElse(null);

        return new CategoryResponseDTO(categoryRepository.save(
                Category.builder()
                        .categoryId(3L)
                        .categoryName("그룹")
                        .dept(null)
                        .group(group)
                        .build()
        ));
    }


    // 조회
    public CategoryResponseDTO findByCategoryNo(Long categoryNo){
        Category category = categoryRepository.findById(categoryNo).orElse(null);
        return new CategoryResponseDTO(category);
    }

    // deptId 조회 category
    public CategoryResponseDTO findByDeptId(Long deptId){
        Category byDeptDeptId = categoryRepository.findByDept_DeptId(deptId);
        if (byDeptDeptId == null)
            return null;
        return new CategoryResponseDTO(byDeptDeptId);
    }

    // groupId 조회 category
    public CategoryResponseDTO findByGroupId(Long groupId){
        Category byGroupGroupId = categoryRepository.findByGroup_GroupId(groupId);
        if (byGroupGroupId == null)
            return null;
        return new CategoryResponseDTO(byGroupGroupId);
    }
}
