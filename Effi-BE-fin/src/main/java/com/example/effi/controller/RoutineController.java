package com.example.effi.controller;


import com.example.effi.domain.DTO.RoutineRequestDTO;
import com.example.effi.domain.DTO.RoutineResponseDTO;
import com.example.effi.domain.DTO.ScheduleResponseDTO;
import com.example.effi.service.RoutineService;
import com.example.effi.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routine")
@AllArgsConstructor
public class RoutineController {
    private final RoutineService routineService;
    private final ScheduleService scheduleService;

    // 조회 (routineId)
    @GetMapping("/find/{routineId}")
    public ResponseEntity<RoutineResponseDTO> findByRoutineId(@PathVariable("routineId") Long routineId) {
        RoutineResponseDTO rtn = routineService.findRoutineById(routineId);
        if (rtn.getDeleteYn() == true)
            return ResponseEntity.ok(null);
        return ResponseEntity.ok(routineService.findRoutineById(routineId));
    }

    // 루틴만 추가
    @PostMapping("/add")
    public ResponseEntity<RoutineResponseDTO> addRoutine(@RequestBody RoutineRequestDTO input) {
        Long routineId = routineService.addRoutine(input);
        return ResponseEntity.ok(routineService.findRoutineById(routineId));
    }

    // 루틴 추가 후 스케줄 업데이트
    @PostMapping("/add/routine/{schdeuleId}")
    public ResponseEntity<RoutineResponseDTO> addRoutine(@PathVariable("schdeuleId") Long schdeuleId, @RequestBody RoutineRequestDTO input) {
        Long routineId = routineService.addRoutine(input);
        scheduleService.updateRoutine(routineId,schdeuleId);
        scheduleService.addRoutineSchedule(scheduleService.getSchedule(schdeuleId));
        return ResponseEntity.ok(routineService.findRoutineById(routineId));
    }

    // 수정
    @PostMapping("/update/{routineId}")
    public ResponseEntity<RoutineResponseDTO> updateRoutine(@PathVariable("routineId") Long routineId, @RequestBody RoutineRequestDTO input) {
        if (routineService.findRoutineById(routineId) == null)
            return ResponseEntity.notFound().build(); // 잘못된 경우
        return ResponseEntity.ok(routineService.updateRoutine(routineId, input));
    }

    // 삭제 -> 스케줄에서 삭제되면 null로 바꿈
    @PutMapping("/delete/{routineId}")
    public ResponseEntity<?> deleteRoutine(@PathVariable("routineId") Long routineId) {
        if (routineService.findRoutineById(routineId) == null)
            return ResponseEntity.notFound().build(); // 잘못된 경우
        routineService.deleteRoutine(routineId);
        List<ScheduleResponseDTO> lst = routineService.findAllByRoutineId(routineId);
        for (ScheduleResponseDTO s : lst) {
            scheduleService.updateRoutine(routineId,s.getScheduleId());
        }
        return ResponseEntity.ok(null);
    }
}
