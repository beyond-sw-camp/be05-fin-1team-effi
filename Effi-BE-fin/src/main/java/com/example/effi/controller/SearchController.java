package com.example.effi.controller;

import com.example.effi.domain.DTO.SearchResponseDTO;
import com.example.effi.service.EmployeeService;
import com.example.effi.service.ParticipantService;
import com.example.effi.service.ScheduleService;
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
    private final ScheduleService scheduleService;
    private final EmployeeService employeeService;
    private final ParticipantService participantService;
    private final SearchService searchService;

    // ?title=<search_term> -> 제목에 search_term 을 포함하는 모든 스케줄 검색
    @GetMapping("/title")
    public ResponseEntity<List<SearchResponseDTO>> searchSchedulesByTitle(@RequestParam String title) {
        List<SearchResponseDTO> response = searchService.searchSchedulesByTitle(title);
        return ResponseEntity.ok(response);
    }


}
