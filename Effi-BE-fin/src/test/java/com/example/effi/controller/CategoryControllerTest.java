package com.example.effi.controller;

import com.example.effi.domain.DTO.CategoryRequestDTO;
import com.example.effi.domain.DTO.CategoryResponseDTO;
import com.example.effi.domain.Entity.Category;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @DisplayName("카테고리 추가 - 성공")
    @Test
    public void testCreateCategorySuccess() throws Exception {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO();
        requestDTO.setCategoryId(1L);
        requestDTO.setDeptId(null);
        requestDTO.setGroupId(null);

        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        responseDTO.setCategoryNo(1L);
        responseDTO.setCategoryId(1L);

        when(categoryService.addCategory(any(CategoryRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":1,\"deptId\":null,\"groupId\":null}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.categoryNo").value(1))
                .andExpect(jsonPath("$.categoryId").value(1));
    }

    @DisplayName("카테고리 추가 - 실패")
    @Test
    public void testCreateCategoryFailure() throws Exception {
        when(categoryService.addCategory(any(CategoryRequestDTO.class))).thenThrow(new RuntimeException("Error occurred"));

        mockMvc.perform(post("/api/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":1,\"deptId\":null,\"groupId\":null}"))
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("부서 Id를 입력받아 Category 추가 - 성공")
    @Test
    public void testCreateDeptCategorySuccess() throws Exception {
        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        when(categoryService.addCategoryByDept(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/category/add/dept/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("부서 Id를 입력받아 Category 추가 - 실패")
    @Test
    public void testCreateDeptCategoryFailure() throws Exception {
        when(categoryService.addCategoryByDept(anyLong())).thenThrow(new RuntimeException("Error occurred"));

        mockMvc.perform(post("/api/category/add/dept/1"))
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("그룹 Id를 입력받아 Category 추가 - 성공")
    @Test
    public void testCreateGroupCategorySuccess() throws Exception {
        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        when(categoryService.addCategoryByGroup(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/category/add/group/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("부서 Id를 입력받아 Category 추가 - 실패")
    @Test
    public void testCreateGroupCategoryFailure() throws Exception {
        when(categoryService.addCategoryByGroup(anyLong())).thenThrow(new RuntimeException("Error occurred"));

        mockMvc.perform(post("/api/category/add/group/1"))
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("기존 CategoryID로 category 찾기 (회사, 부서, 그룹, 개인) - 성공")
    @Test
    public void testFindCategorySuccess() throws Exception {
        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        when(categoryService.findByCategoryNo(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/category/find/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("기존 CategoryID로 category 찾기 (회사, 부서, 그룹, 개인) - 실패")
    @Test
    public void testFindCategoryFailure() throws Exception {
        when(categoryService.findByCategoryNo(anyLong())).thenThrow(new RuntimeException("Error occurred"));

        mockMvc.perform(get("/api/category/find/1"))
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("전체 카테고리 찾기 - 성공")
    @Test
    public void testFindAllCategorySuccess() throws Exception {
        Category category = Category.builder()
                .categoryId(4L)
                .dept(null)
                .group(null)
                .build();
        List<Category> categories = Collections.singletonList(category);
        List<CategoryResponseDTO> categoryResponseDTOS = Collections.singletonList(new CategoryResponseDTO(category));
        when(categoryService.findAll()).thenReturn(categoryResponseDTOS);

        mockMvc.perform(get("/api/category/findAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
