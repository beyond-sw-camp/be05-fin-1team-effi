package com.example.effi.controller;


import com.example.effi.domain.DTO.RoutineRequestDTO;
import com.example.effi.domain.DTO.RoutineResponseDTO;
import com.example.effi.domain.DTO.TagResponseDTO;
import com.example.effi.service.RoutineService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routine")
@AllArgsConstructor
public class RoutineController {
    private final RoutineService routineService;

    // 조회 (routineId)
    @GetMapping("/find/{routineId}")
    public ResponseEntity<RoutineResponseDTO> findByRoutineId(@PathVariable("routineId") Long routineId) {
        return ResponseEntity.ok(routineService.findRoutineById(routineId));
    }

    // 추가
    @PostMapping("/add")
    public ResponseEntity<RoutineResponseDTO> addRoutine(@RequestBody RoutineRequestDTO input) {
        Long routineId = routineService.addRoutine(input);
        return ResponseEntity.ok(routineService.findRoutineById(routineId));
    }

    // 수정
    @PostMapping("/update/{routineId}")
    public ResponseEntity<RoutineResponseDTO> updateRoutine(@PathVariable("routineId") Long routineId, @RequestBody RoutineRequestDTO input) {
        if (routineService.findRoutineById(routineId) == null)
            return ResponseEntity.notFound().build(); // 잘못된 경우
        return ResponseEntity.ok(routineService.updateRoutine(input));
    }

    // 삭제
    @PutMapping("/delete/{routineId}")
    public ResponseEntity<?> deleteRoutine(@PathVariable("routineId") Long routineId) {
        if (routineService.findRoutineById(routineId) == null)
            return ResponseEntity.notFound().build(); // 잘못된 경우
        routineService.deleteRoutine(routineId);
        return ResponseEntity.ok(null);
    }
}
