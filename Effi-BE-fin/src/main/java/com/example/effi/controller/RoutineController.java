package com.example.effi.controller;


import com.example.effi.domain.DTO.RoutineRequestDTO;
import com.example.effi.domain.DTO.RoutineResponseDTO;
import com.example.effi.domain.DTO.ScheduleResponseDTO;
import com.example.effi.service.RoutineService;
import com.example.effi.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> findByRoutineId(@PathVariable("routineId") Long routineId) {
        try {
            RoutineResponseDTO rtn = routineService.findRoutineById(routineId);
            if (rtn.getDeleteYn() == true)
                return ResponseEntity.ok(null);
            return ResponseEntity.ok(routineService.findRoutineById(routineId));
        }catch (IllegalArgumentException e) {
            // 찾을 수 없는 참가자 ID로 인한 실패
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // 기타 예외로 인한 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to find routine: " + e.getMessage());
        }
    }

    // 루틴만 추가
    @PostMapping("/add")
    public ResponseEntity<?> addRoutine(@RequestBody RoutineRequestDTO input) {
        try {
            Long routineId = routineService.addRoutine(input);
            return ResponseEntity.ok(routineService.findRoutineById(routineId));
        }catch (Exception e) {
            // 기타 예외로 인한 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add routine: " + e.getMessage());
        }
    }

    // 루틴 추가 후 스케줄 업데이트
    @PostMapping("/add/{schdeuleId}")
    public ResponseEntity<?> addRoutine(@PathVariable("schdeuleId") Long schdeuleId, @RequestBody RoutineRequestDTO input) {
        try {
            Long routineId = routineService.addRoutine(input);
            scheduleService.updateRoutine(routineId, schdeuleId);
            scheduleService.addRoutineSchedule(scheduleService.getSchedule(schdeuleId));
            return ResponseEntity.ok(routineService.findRoutineById(routineId));
        }catch (Exception e) {
            // 기타 예외로 인한 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add routine: " + e.getMessage());
        }
    }

    // 수정
    @PostMapping("/update/{routineId}")
    public ResponseEntity<?> updateRoutine(@PathVariable("routineId") Long routineId, @RequestBody RoutineRequestDTO input) {
        try {
            routineService.findRoutineById(routineId);
            return ResponseEntity.ok(routineService.updateRoutine(routineId, input));
        } catch (IllegalArgumentException e) {
            // 찾을 수 없는 참가자 ID로 인한 실패
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update routine: " + e.getMessage());
        }
    }

    // 삭제 -> 스케줄에서 삭제되면 null로 바꿈
    @PutMapping("/delete/{routineId}")
    public ResponseEntity<?> deleteRoutine(@PathVariable("routineId") Long routineId) {
        try {
            routineService.findRoutineById(routineId);
            routineService.deleteRoutine(routineId);
            List<ScheduleResponseDTO> lst = routineService.findAllByRoutineId(routineId);
            for (ScheduleResponseDTO s : lst) {
                scheduleService.updateRoutine(routineId,s.getScheduleId());
            }
            return ResponseEntity.ok(null);
        }catch (IllegalArgumentException e) {
            // 찾을 수 없는 참가자 ID로 인한 실패
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // 기타 예외로 인한 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete routine: " + e.getMessage());
        }

    }
}
