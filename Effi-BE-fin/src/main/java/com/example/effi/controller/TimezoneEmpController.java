package com.example.effi.controller;

import com.example.effi.domain.DTO.TimezoneDTO;
import com.example.effi.service.TimezoneEmpService;
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
    public ResponseEntity<Map<String, Object>> addTimezoneForEmployee(
            @PathVariable Long empId,
            @RequestParam Long timezoneId,
            @RequestParam(required = false, defaultValue = "false") boolean isDefault) {
        timezoneEmpService.addTimezoneForEmployee(empId, timezoneId, isDefault);

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "타임존 추가에 성공했습니다");
        Map<String, Object> data = new HashMap<>();
        data.put("empId", empId);
        data.put("timezoneId", timezoneId);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{empId}")
    public ResponseEntity<Map<String, Object>> getTimezonesForEmployee(
            @PathVariable Long empId) {
        List<TimezoneDTO> timezones = timezoneEmpService.getTimezonesForEmployee(empId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "타임존 목록 조회에 성공했습니다");
        Map<String, Object> data = new HashMap<>();
        data.put("timezones", timezones);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{empId}/update-default")
    public ResponseEntity<Map<String, Object>> updateDefaultTimezoneForEmployee(
            @PathVariable Long empId,
            @RequestParam Long newTimezoneId) {
        timezoneEmpService.updateDefaultTimezoneForEmployee(empId, newTimezoneId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "기본 타임존 업데이트에 성공했습니다");
        Map<String, Object> data = new HashMap<>();
        data.put("empId", empId);
        data.put("newTimezoneId", newTimezoneId);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{empId}/remove")
    public ResponseEntity<Map<String, Object>> removeTimezoneForEmployee(
            @PathVariable Long empId,
            @RequestParam Long timezoneId) {
        timezoneEmpService.removeTimezoneForEmployee(empId, timezoneId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "타임존 삭제에 성공했습니다");
        Map<String, Object> data = new HashMap<>();
        data.put("empId", empId);
        data.put("timezoneId", timezoneId);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}
