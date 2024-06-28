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

    //findAll
    public List<CategoryResponseDTO> findAll(){
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty())
            return new ArrayList<>();
        List<CategoryResponseDTO> categoriesDTO = new ArrayList<>();
        for (Category category : categories) {
            CategoryResponseDTO categoryDTO = new CategoryResponseDTO();
        }
        return categoriesDTO;
    }

    //id로 찾기 - 1 2 3 4 기존 거
    public List<CategoryResponseDTO> findCategory(Long categoryId) {
        if (categoryId < 1L || categoryId > 4L)
            throw new IllegalArgumentException("카테고리 ID가 유효하지 않습니다.");
        List<Category> category = Optional.ofNullable(categoryId)
                .map(categoryRepository::findAllByCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리 ID가 유효하지 않습니다."));
        List<CategoryResponseDTO> rtn = new ArrayList<>();
        for (Category c : category) {
            if (c != null)
                rtn.add(new CategoryResponseDTO(c));
        }
        return rtn;
    }

    // add 전체 입력
    public CategoryResponseDTO addCategory(CategoryRequestDTO categoryRequestDTO){
        String categoryName = "";

        if (categoryRequestDTO.getCategoryId() == 1L)
            categoryName = "회사";
        else if (categoryRequestDTO.getCategoryId() == 2L)
            categoryName = "부서";
        else if (categoryRequestDTO.getCategoryId() == 3L)
            categoryName = "그룹";
        else
            categoryName = "개인";


        Dept dept = null;
        Group group = null;

        if (categoryRequestDTO.getCategoryId() == 2L) {
            dept = Optional.ofNullable(categoryRequestDTO.getDeptId())
                    .flatMap(deptRepository::findById)
                    .orElseThrow(() -> new IllegalArgumentException("부서 ID가 유효하지 않습니다."));
        }

        if (categoryRequestDTO.getCategoryId() == 3L) {
            group = Optional.ofNullable(categoryRequestDTO.getGroupId())
                    .flatMap(groupRepository::findById)
                    .orElseThrow(() -> new IllegalArgumentException("그룹 ID가 유효하지 않습니다."));
        }

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
        // Dept dept = Optional.ofNullable(deptId)
        //         .flatMap(deptRepository::findById)
        //         .orElseThrow(() -> new IllegalArgumentException("부서 ID가 유효하지 않습니다."));

        Dept dept = Optional.ofNullable(deptId)
                .map(deptRepository::findByDeptId)
                .orElseThrow(() -> new IllegalArgumentException("부서 ID가 유효하지 않습니다."));
        if (categoryRepository.findByDept_DeptId(deptId) != null)
            return findByDeptId(deptId);

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
        Group group = Optional.ofNullable(groupId)
                .flatMap(groupRepository::findById)
                .orElseThrow(() -> new IllegalArgumentException("그룹 ID가 유효하지 않습니다."));

        if (categoryRepository.findByGroup_GroupId(groupId) != null)
            return findByGroupId(groupId);

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
        if (category == null)
            throw new IllegalArgumentException("카테고리 No가 유효하지 않습니다.");
        return new CategoryResponseDTO(category);
    }

    // deptId 조회 category
    public CategoryResponseDTO findByDeptId(Long deptId){
        Category byDeptDeptId = categoryRepository.findByDept_DeptId(deptId);
        if (byDeptDeptId == null)
            throw new IllegalArgumentException("부서 ID가 유효하지 않습니다.");
        return new CategoryResponseDTO(byDeptDeptId);
    }

    // groupId 조회 category
    public CategoryResponseDTO findByGroupId(Long groupId){
        Category byGroupGroupId = categoryRepository.findByGroup_GroupId(groupId);
        if (byGroupGroupId == null)
            throw new IllegalArgumentException("그룹 ID가 유효하지 않습니다.");
        return new CategoryResponseDTO(byGroupGroupId);
    }
}
