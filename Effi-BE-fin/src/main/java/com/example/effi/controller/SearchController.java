package com.example.effi.controller;

import com.example.effi.domain.Dto.Schedule.SearchResponseDTO;
import com.example.effi.service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@AllArgsConstructor
public class SearchController {
    private final SearchService searchService;

    // api/search/title?title=<title> -> 제목에 title 을 포함하는 모든 스케줄 검색
    @GetMapping("/title")
    public ResponseEntity<List<SearchResponseDTO>> searchSchedulesByTitle(@RequestParam String title) {
        List<SearchResponseDTO> schedules = searchService.searchSchedulesByTitle(title);
        return ResponseEntity.ok(schedules);
    }

    // api/search/tag?tag=<tag_name> -> tagName에 tag_name을 포함하는 모든 스케줄 검색
    @GetMapping("/tag")
    public ResponseEntity<List<SearchResponseDTO>> searchSchedulesByTag(@RequestParam String tagName) {
        List<SearchResponseDTO> schedules = searchService.searchSchedulesByTag(tagName);
        return ResponseEntity.ok(schedules);
    }

    // api/search/category?categoryName=<category_name> -> 정확히 category 이름이 category_name 인 모든 스케줄 검색
    @GetMapping("/category")
    public ResponseEntity<List<SearchResponseDTO>> searchSchedulesByCategory(@RequestParam String categoryName){
        List<SearchResponseDTO> schedules = searchService.searchSchedulesByCategory(categoryName);
        return ResponseEntity.ok(schedules);
    }





}

