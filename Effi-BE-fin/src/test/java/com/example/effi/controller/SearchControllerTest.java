package com.example.effi.controller;

import com.example.effi.domain.DTO.SearchResponseDTO;
import com.example.effi.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Date;

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

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @Test
    void testSearchSchedulesByTitle() throws Exception {
        SearchResponseDTO searchResponseDTO = SearchResponseDTO.builder()
                .scheduleId(1L)
                .title("Sample Title")
                .context("Sample Context")
                .startTime(new Date())
                .endTime(new Date())
                .status(1)
                .notificationYn(true)
                .deleteYn(false)
                .createdAt(new Date())
                .updatedAt(new Date())
                .categoryId(1L)
                .categoryName("Sample Category")
                .routineId(1L)
                .tagNames(Collections.emptyList())
                .build();

        when(searchService.searchSchedulesByTitle(anyString())).thenReturn(Collections.singletonList(searchResponseDTO));

        mockMvc.perform(get("/api/search/title")
                        .param("title", "Sample Title")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Title"));
    }

    @Test
    void testSearchSchedulesByTitleFailure() throws Exception {
        when(searchService.searchSchedulesByTitle(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/search/title")
                        .param("title", "Nonexistent Title")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testSearchSchedulesByTag() throws Exception {
        SearchResponseDTO searchResponseDTO = SearchResponseDTO.builder()
                .scheduleId(1L)
                .title("Sample Title")
                .context("Sample Context")
                .startTime(new Date())
                .endTime(new Date())
                .status(1)
                .notificationYn(true)
                .deleteYn(false)
                .createdAt(new Date())
                .updatedAt(new Date())
                .categoryId(1L)
                .categoryName("Sample Category")
                .routineId(1L)
                .tagNames(Collections.emptyList())
                .build();

        when(searchService.searchSchedulesByTag(anyString())).thenReturn(Collections.singletonList(searchResponseDTO));

        mockMvc.perform(get("/api/search/tag")
                        .param("tagName", "Sample Tag")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Title"));
    }

    @Test
    void testSearchSchedulesByTagFailure() throws Exception {
        when(searchService.searchSchedulesByTag(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/search/tag")
                        .param("tagName", "Nonexistent Tag")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testSearchSchedulesByCategory() throws Exception {
        SearchResponseDTO searchResponseDTO = SearchResponseDTO.builder()
                .scheduleId(1L)
                .title("Sample Title")
                .context("Sample Context")
                .startTime(new Date())
                .endTime(new Date())
                .status(1)
                .notificationYn(true)
                .deleteYn(false)
                .createdAt(new Date())
                .updatedAt(new Date())
                .categoryId(1L)
                .categoryName("Sample Category")
                .routineId(1L)
                .tagNames(Collections.emptyList())
                .build();

        when(searchService.searchSchedulesByCategory(anyString())).thenReturn(Collections.singletonList(searchResponseDTO));

        mockMvc.perform(get("/api/search/category")
                        .param("categoryName", "Sample Category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Title"));
    }

    @Test
    void testSearchSchedulesByCategoryFailure() throws Exception {
        when(searchService.searchSchedulesByCategory(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/search/category")
                        .param("categoryName", "Nonexistent Category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
