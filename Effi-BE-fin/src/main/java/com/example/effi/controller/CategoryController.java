package com.example.effi.controller;

import com.example.effi.domain.DTO.CategoryDTO;
import com.example.effi.domain.DTO.CategoryRequestDTO;
import com.example.effi.domain.DTO.CategoryResponseDTO;
import com.example.effi.domain.DTO.GroupRequestDTO;
import com.example.effi.domain.Entity.Category;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        try {
            CategoryResponseDTO responseDTO = categoryService.addCategory(categoryDTO);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    // 카테고리 추가 부서
    @PostMapping("/add/dept/{deptId}")
    public ResponseEntity<?> createDeptCategory(@PathVariable("deptId") Long deptId) {
        try {
            return ResponseEntity.ok(categoryService.addCategoryByDept(deptId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    // 카테고리 추가 그룹
    @PostMapping("/add/group/{groupId}")
    public ResponseEntity<?> createGroupCategory(@PathVariable("groupId") Long groupId) {
        try {
            return ResponseEntity.ok(categoryService.addCategoryByGroup(groupId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    // 카테고리 조회 (카테고리 No)
    @GetMapping("/find/{categoryNo}")
    public ResponseEntity<?> findCategory(@PathVariable("categoryNo") Long categoryNo) {
        try {
            CategoryResponseDTO category = categoryService.findByCategoryNo(categoryNo);
            return ResponseEntity.ok(category);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAllCategory() {
        try {
            List<CategoryResponseDTO> all = categoryService.findAll();
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(all);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }
}
