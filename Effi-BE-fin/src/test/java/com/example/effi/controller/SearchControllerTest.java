package com.example.effi.controller;

import com.example.effi.domain.DTO.SearchResponseDTO;
import com.example.effi.domain.Entity.Dept;
import com.example.effi.repository.DeptRepository;
import com.example.effi.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    @Mock
    private DeptRepository deptRepository;

    @Mock
    private Dept sampleDept;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
        sampleDept = Dept.builder()
                .deptName("Sample Dept")
                .build();
    }

    @DisplayName("이름으로 schedule 찾기 - 성공")
    @Test
    void testSearchSchedulesByTitle() throws Exception {
        SearchResponseDTO searchResponseDTO = SearchResponseDTO.builder()
                .scheduleId(1L).title("Sample Title").context("Sample Context")
                .startTime(new Date()).endTime(new Date()).status(1).notificationYn(true)
                .deleteYn(false).createdAt(new Date()).updatedAt(new Date())
                .categoryId(1L).categoryName("Sample Category").routineId(1L)
                .tagNames(Collections.emptyList()).build();

        when(searchService.searchSchedulesByTitle(anyString())).thenReturn(Collections.singletonList(searchResponseDTO));

        mockMvc.perform(get("/api/search/title")
                        .param("title", "Sample Title")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Title"));
    }

    @DisplayName("이름으로 schedule 찾기 - 실패")
    @Test
    void testSearchSchedulesByTitleFailure() throws Exception {
        when(searchService.searchSchedulesByTitle(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/search/title")
                        .param("title", "Nonexistent Title")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @DisplayName("태그로 schedule 찾기 - 성공")
    @Test
    void testSearchSchedulesByTag() throws Exception {
        SearchResponseDTO searchResponseDTO = SearchResponseDTO.builder()
                .scheduleId(1L).title("Sample Title").context("Sample Context")
                .startTime(new Date()).endTime(new Date()).status(1)
                .notificationYn(true).deleteYn(false).createdAt(new Date())
                .updatedAt(new Date()).categoryId(1L).categoryName("Sample Category")
                .routineId(1L).tagNames(Collections.emptyList()).build();

        when(searchService.searchSchedulesByTag(anyString())).thenReturn(Collections.singletonList(searchResponseDTO));

        mockMvc.perform(get("/api/search/tag")
                        .param("tagName", "Sample Tag")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Title"));
    }

    @DisplayName("태그 schedule 찾기 - 실패")
    @Test
    void testSearchSchedulesByTagFailure() throws Exception {
        when(searchService.searchSchedulesByTag(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/search/tag")
                        .param("tagName", "Nonexistent Tag")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @DisplayName("카테고리로 schedule 찾기 - 성공")
    @Test
    void testSearchSchedulesByCategory() throws Exception {
        SearchResponseDTO searchResponseDTO = SearchResponseDTO.builder()
                .scheduleId(1L).title("Sample Title").context("Sample Context").startTime(new Date())
                .endTime(new Date()).status(1).notificationYn(true).deleteYn(false)
                .createdAt(new Date()).updatedAt(new Date()).categoryId(1L).categoryName("Sample Category")
                .routineId(1L).tagNames(Collections.emptyList()).build();

        when(searchService.searchSchedulesByCategory(anyString())).thenReturn(Collections.singletonList(searchResponseDTO));

        mockMvc.perform(get("/api/search/category")
                        .param("categoryName", "Sample Category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Title"));
    }

    @DisplayName("카테고리로 schedule 찾기 - 실패")
    @Test
    void testSearchSchedulesByCategoryFailure() throws Exception {
        when(searchService.searchSchedulesByCategory(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/search/category")
                        .param("categoryName", "Nonexistent Category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @DisplayName("DeptId로 schedule 찾기 - 성공")
    @Test
    void testSearchSchedulesByDept() throws Exception {
        when(deptRepository.findByDeptId(anyLong())).thenReturn(sampleDept);

        mockMvc.perform(get("/api/search/dept/{deptId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Sample Dept"));
    }

    @DisplayName("DeptId로 schedule 찾기 - 실패")
    @Test
    void testSearchSchedulesByDeptFailure() throws Exception {
        when(deptRepository.findByDeptId(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/search/dept/{deptId}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("전체 Dept schedule 찾기 - 성공")
    @Test
    void testSearchAlldept() throws Exception {
        Dept dept1 = Dept.builder().deptName("Dept 1").build();
        Dept dept2 = Dept.builder().deptName("Dept 2").build();
        when(deptRepository.findAll()).thenReturn(Arrays.asList(dept1, dept2));

        mockMvc.perform(get("/api/search/dept/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deptName").value("Dept 1"))
                .andExpect(jsonPath("$[1].deptName").value("Dept 2"));
    }

    @DisplayName("전체 Dept schedule 찾기 - 실패")
    @Test
    void testSearchAlldeptFailure() throws Exception {
        when(deptRepository.findAll()).thenReturn(Collections.emptyList());
        System.out.println("deptRepository.findAll().size() = " + deptRepository.findAll().size());
        mockMvc.perform(get("/api/search/dept/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
