package com.example.effi.service;

import com.example.effi.domain.DTO.CategoryRequestDTO;
import com.example.effi.domain.DTO.CategoryResponseDTO;
import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Dept;
import com.example.effi.domain.Entity.Group;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.DeptRepository;
import com.example.effi.repository.GroupRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryServiceTest {
    @Autowired
    CategoryService categoryService;
    @Autowired
    private DeptRepository deptRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private GroupService groupService;

    @BeforeAll
    public void setUp() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("1", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 부서 생성
        deptRepository.save(Dept.builder()
                        .deptName("TestDept").build()
        );
        // 그룹 생성
        groupRepository.save(Group.builder()
                        .groupName("TestGroup")
                        .createdAt(null)
                        .deleteYn(false).build()
        );
        categoryRepository.save(Category.builder().categoryId(1L).categoryName("회사").dept(null).group(null).build());
        categoryRepository.save(Category.builder().categoryId(2L).categoryName("부서").dept(null).group(null).build());
        categoryRepository.save(Category.builder().categoryId(3L).categoryName("그룹").dept(null).group(null).build());
        categoryRepository.save(Category.builder().categoryId(4L).categoryName("개인").dept(null).group(null).build());
    }

    // add 전체 입력
    @DisplayName("카테고리 추가 - 성공")
    @Test
    public void addCategoryTest() {
        CategoryRequestDTO inputDTO = new CategoryRequestDTO();
        inputDTO.setCategoryId(2L);// 부서
        inputDTO.setDeptId(1L);
        inputDTO.setGroupId(null);

        CategoryResponseDTO responseDTO = categoryService.addCategory(inputDTO);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getCategoryId()).isEqualTo(inputDTO.getCategoryId());
        assertThat(responseDTO.getDeptId()).isEqualTo(inputDTO.getDeptId());
        assertThat(responseDTO.getGroupId()).isEqualTo(inputDTO.getGroupId());
    }

    @DisplayName("카테고리 추가 - 실패")
    @Test
    public void addCategoryTestFail() {
        // 부서 카테고리에 부서 번호 X
        CategoryRequestDTO inputDTO = new CategoryRequestDTO();
        inputDTO.setCategoryId(2L);// 부서
        inputDTO.setDeptId(null);
        inputDTO.setGroupId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.addCategory(inputDTO);
        });
        assertEquals("부서 ID가 유효하지 않습니다.", exception.getMessage());

        // 그룹 카테고리에 그룹 번호 X
        inputDTO.setCategoryId(3L);// 부서
        inputDTO.setDeptId(null);
        inputDTO.setGroupId(null);

        exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.addCategory(inputDTO);
        });
        assertEquals("그룹 ID가 유효하지 않습니다.", exception.getMessage());

        // 없는 부서번호 입력
        inputDTO.setCategoryId(2L);// 부서
        inputDTO.setDeptId(999L);
        inputDTO.setGroupId(null);

        exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.addCategory(inputDTO);
        });
        assertEquals("부서 ID가 유효하지 않습니다.", exception.getMessage());
    }

    // 전체 조회
    @DisplayName("전체 조회 - 성공")
    @Test
    public void findAllTest() {
        List<CategoryResponseDTO> all = categoryService.findAll();
        assertThat(all).isNotNull();
    }

    //id로 찾기 - 1 2 3 4 기존 거
    @DisplayName("기존 CategoryID로 category 찾기 (회사, 부서, 그룹, 개인) - 성공")
    @Test
    public void findCategoryTest() {
        Long categoryId = 1L;

        List<CategoryResponseDTO> category = categoryService.findCategory(categoryId);
        assertThat(category).isNotNull();
        for (CategoryResponseDTO categoryDTO : category) {
            assertThat(categoryDTO.getCategoryId()).isEqualTo(categoryId);
        }
    }

    @DisplayName("기존 CategoryID로 category 찾기 (회사, 부서, 그룹, 개인) - 실패")
    @Test
    public void findCategoryTestFail() {
        Long categoryId = 9999L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.findCategory(categoryId);
        });

        assertEquals("카테고리 ID가 유효하지 않습니다.", exception.getMessage());
    }

    // 부서 추가 2
    @DisplayName("부서 Id를 입력받아 Category 추가 - 성공")
    @Test
    public void addCategoryByDeptTest() {
        Long deptId = 1L;

        CategoryResponseDTO responseDTO = categoryService.addCategoryByDept(deptId);
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getCategoryId()).isEqualTo(2L);
        assertThat(responseDTO.getDeptId()).isEqualTo(deptId);
    }

    @DisplayName("부서 Id를 입력받아 Category 추가 - 실패")
    @Test
    public void addCategoryByDeptTestFail() {
        Long deptId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.addCategoryByDept(deptId);
        });

        assertEquals("부서 ID가 유효하지 않습니다.", exception.getMessage());
    }

    // 그룹 입력 3 addCategoryByGroup
    @DisplayName("그룹 Id를 입력받아 Category 추가 - 성공")
    @Test
    public void addCategoryByGroupTest() {
        Long groupId = groupService.findGroupByName("TestGroup").getGroupId();

        System.out.println("groupId = " + groupId);
        CategoryResponseDTO responseDTO = categoryService.addCategoryByGroup(groupId);
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getCategoryId()).isEqualTo(3L);
        assertThat(responseDTO.getGroupId()).isEqualTo(groupId);
    }

    @DisplayName("그룹 Id를 입력받아 Category 추가 - 실패")
    @Test
    public void addCategoryByGroupTestFail() {
        Long groupId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.addCategoryByGroup(groupId);
        });

        assertEquals("그룹 ID가 유효하지 않습니다.", exception.getMessage());
    }

    // 조회
    @DisplayName("카테고리 No를 입력받아 Category 조회 - 성공")
    @Test
    public void findByCategoryNoTest() {
        CategoryRequestDTO inputDTO = new CategoryRequestDTO();
        inputDTO.setCategoryId(4L);
        inputDTO.setDeptId(null);
        inputDTO.setGroupId(null);

        CategoryResponseDTO responseDTO = categoryService.addCategory(inputDTO);

        CategoryResponseDTO byCategoryNo = categoryService.findByCategoryNo(responseDTO.getCategoryNo());
        assertThat(byCategoryNo).isNotNull();
        assertThat(byCategoryNo.getCategoryNo()).isEqualTo(responseDTO.getCategoryNo());
        assertThat(byCategoryNo.getCategoryId()).isEqualTo(4L);
    }

    @DisplayName("카테고리 No를 입력받아 Category 조회 - 실패")
    @Test
    public void findByCategoryNoTestFail() {
        Long categoryNo = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.findByCategoryNo(categoryNo);
        });

        assertEquals("카테고리 No가 유효하지 않습니다.", exception.getMessage());
    }

    // deptId 조회 category findByDeptId
    @DisplayName("부서 Id를 입력받아 Category 조회 - 성공")
    @Test
    public void findByDeptIdTest() {
        Long deptId = 1L;

        CategoryResponseDTO byDeptId = categoryService.findByDeptId(deptId);
        assertThat(byDeptId).isNotNull();
        assertThat(byDeptId.getDeptId()).isEqualTo(deptId);
    }

    @DisplayName("부서 Id를 입력받아 Category 조회 - 실패")
    @Test
    public void findByDeptIdTestFail() {
        Long deptId = 999L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.findByDeptId(deptId);
        });

        assertEquals("부서 ID가 유효하지 않습니다.", exception.getMessage());
    }

    // groupId 조회 category findByGroupId
    @DisplayName("그룹 Id를 입력받아 Category 조회 - 성공")
    @Test
    public void findByGroupIdTest() {
        Long groupId = groupService.findGroupByName("TestGroup").getGroupId();

        CategoryRequestDTO inputDTO = new CategoryRequestDTO();
        inputDTO.setCategoryId(3L);
        inputDTO.setDeptId(null);
        inputDTO.setGroupId(groupId);

        categoryService.addCategory(inputDTO);

        CategoryResponseDTO byGroupId = categoryService.findByGroupId(groupId);
        assertThat(byGroupId).isNotNull();
        assertThat(byGroupId.getGroupId()).isEqualTo(groupId);
    }

    @DisplayName("그룹 Id를 입력받아 Category 조회 - 실패")
    @Test
    public void findByGroupIdTestFail() {
        Long groupId = 999L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.findByGroupId(groupId);
        });

        assertEquals("그룹 ID가 유효하지 않습니다.", exception.getMessage());
    }
}
