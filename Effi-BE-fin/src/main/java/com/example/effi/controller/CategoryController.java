package com.example.effi.controller;

import com.example.effi.domain.DTO.CategoryDTO;
import com.example.effi.domain.DTO.CategoryRequestDTO;
import com.example.effi.domain.DTO.CategoryResponseDTO;
import com.example.effi.domain.DTO.GroupRequestDTO;
import com.example.effi.domain.Entity.Category;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/category") // 필요한가
@AllArgsConstructor
public class CategoryController {

    // 회사는 무조건 CategoryNo 1
    // 개인은 무조건 CategoryNo 4 라고 가정함 -> 추가 함수 없음

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;


    // 카테고리 추가 (전체 다 작성 가정)
    @PostMapping("/add")
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequestDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.addCategory(categoryDTO));
    }

    // 카테고리 추가 부서
    @PostMapping("/add/dept/{deptId}")
    public ResponseEntity<?> createDeptCategory(@PathVariable("deptId") Long deptId) {
        return ResponseEntity.ok(categoryService.addCategoryByDept(deptId));
    }

    // 카테고리 추가 그룹
    @PostMapping("/add/group/{groupId}")
    public ResponseEntity<?> createGroupCategory(@PathVariable("groupId") Long groupId) {
        return ResponseEntity.ok(categoryService.addCategoryByGroup(groupId));
    }

    // 카테고리 조회 (카테고리 No)
    @GetMapping("/find/{categoryNo}")
    public ResponseEntity<?> findCategory(@PathVariable("categoryNo") Long categoryNo) {
        CategoryResponseDTO category = categoryService.findCategory(categoryNo);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponseDTO> categoryResponseDTOS = new ArrayList<>();
        for (Category category : categories) {
            categoryResponseDTOS.add(new CategoryResponseDTO(category));
        }
        return ResponseEntity.ok(categoryResponseDTOS);
    }
}
