package com.example.effi.controller;

import com.example.effi.domain.DTO.DeptDTO;
import com.example.effi.domain.DTO.SearchResponseDTO;
import com.example.effi.domain.Entity.Dept;
import com.example.effi.repository.DeptRepository;
import com.example.effi.service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@AllArgsConstructor
public class SearchController {
    private final SearchService searchService;
    private final DeptRepository deptRepository;

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

    // deptId로 deptName 찾기
    @GetMapping("/dept/{deptId}")
    public ResponseEntity<String> searchSchedulesByDept(@PathVariable Long deptId){
        Dept byDeptId = deptRepository.findByDeptId(deptId);
        return ResponseEntity.ok(byDeptId.getDeptName());
    }

    @GetMapping("/dept")
    public ResponseEntity<List<DeptDTO>> searchAlldept(){
        List<Dept> deptRepositoryAll = deptRepository.findAll();
        List<DeptDTO> deptList = new ArrayList<>();
        for (Dept dept : deptRepositoryAll) {
            deptList.add(new DeptDTO(dept.getDeptId(), dept.getDeptName()));
        }
        return ResponseEntity.ok(deptList);
    }

}

