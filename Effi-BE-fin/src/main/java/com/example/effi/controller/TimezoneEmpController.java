package com.example.effi.controller;

import com.example.effi.domain.DTO.*;
import com.example.effi.service.TimezoneEmpService;
import com.example.effi.service.TimezoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/timezone-emp")
@RequiredArgsConstructor
public class TimezoneEmpController {
    private final TimezoneEmpService timezoneEmpService;

    @PostMapping("/{empId}/add")
    public ResponseEntity<GlobalResponse<Map<String, Object>>> addTimezoneForEmployee(
            @PathVariable Long empId,
            @RequestParam Long timezoneId,
            @RequestParam(required = false, defaultValue = "false") boolean isDefault) {
        try {
            timezoneEmpService.addTimezoneForEmployee(empId, timezoneId, isDefault);

            Map<String, Object> data = new HashMap<>();
            data.put("empId", empId);
            data.put("timezoneId", timezoneId);

            return ResponseEntity.ok(GlobalResponse.<Map<String, Object>>builder()
                    .status(200)
                    .message("타임존 추가에 성공했습니다")
                    .data(data)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(GlobalResponse.<Map<String, Object>>builder()
                    .status(400)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/{empId}")
    public ResponseEntity<GlobalResponse<Map<String, Object>>> getTimezonesForEmployee(
            @PathVariable Long empId) {
        List<TimezoneDTO> timezones = timezoneEmpService.getTimezonesForEmployee(empId);

        Map<String, Object> data = new HashMap<>();
        data.put("timezones", timezones);

        return ResponseEntity.ok(GlobalResponse.<Map<String, Object>>builder()
                .status(200)
                .message("타임존 목록 조회에 성공했습니다")
                .data(data)
                .build());
    }

    @PutMapping("/{empId}/update-default")
    public ResponseEntity<GlobalResponse<Map<String, Object>>> updateDefaultTimezoneForEmployee(
            @PathVariable Long empId,
            @RequestParam Long newTimezoneId) {
        try {
            timezoneEmpService.updateDefaultTimezoneForEmployee(empId, newTimezoneId);

            Map<String, Object> data = new HashMap<>();
            data.put("empId", empId);
            data.put("newTimezoneId", newTimezoneId);

            return ResponseEntity.ok(GlobalResponse.<Map<String, Object>>builder()
                    .status(200)
                    .message("기본 타임존 업데이트에 성공했습니다")
                    .data(data)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(GlobalResponse.<Map<String, Object>>builder()
                    .status(400)
                    .message(e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/{empId}/remove")
    public ResponseEntity<GlobalResponse<Map<String, Object>>> removeTimezoneForEmployee(
            @PathVariable Long empId,
            @RequestParam Long timezoneId) {
        try {
            timezoneEmpService.removeTimezoneForEmployee(empId, timezoneId);

            Map<String, Object> data = new HashMap<>();
            data.put("empId", empId);
            data.put("timezoneId", timezoneId);

            return ResponseEntity.ok(GlobalResponse.<Map<String, Object>>builder()
                    .status(200)
                    .message("타임존 삭제에 성공했습니다")
                    .data(data)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(GlobalResponse.<Map<String, Object>>builder()
                    .status(400)
                    .message(e.getMessage())
                    .build());
        }
    }

    // 본인의 기본 타임존 조회
    @GetMapping("/{empId}/default")
    public ResponseEntity<GlobalResponse<DefaultTimezoneDTO>> getDefaultTimezoneForEmployee(
            @PathVariable Long empId) {
        try {
            DefaultTimezoneDTO defaultTimezone = timezoneEmpService.getDefaultTimezoneForEmployee(empId);

            return ResponseEntity.ok(GlobalResponse.<DefaultTimezoneDTO>builder()
                    .status(200)
                    .message("기본 타임존 조회에 성공했습니다")
                    .data(defaultTimezone)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(GlobalResponse.<DefaultTimezoneDTO>builder()
                    .status(400)
                    .message(e.getMessage())
                    .build());
        }
    }
    
}
