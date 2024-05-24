package com.example.effi.controller;

import com.example.effi.domain.DTO.TimezoneDTO;
import com.example.effi.service.TimezoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TimezoneController는 타임존 관련 요청을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/timezones")
@RequiredArgsConstructor
public class TimezoneController {
    private final TimezoneService timezoneService;

    /**
     * 새로운 타임존을 추가합니다.
     * @param timezoneDTO 타임존 DTO
     * @return 추가된 타임존 DTO
     */
    @PostMapping
    public ResponseEntity<TimezoneDTO> addTimezone(@RequestBody TimezoneDTO timezoneDTO) {
        TimezoneDTO createdTimezone = timezoneService.addTimezone(timezoneDTO);
        return ResponseEntity.ok(createdTimezone);
    }

    /**
     * 모든 타임존을 조회합니다.
     * @return 타임존 DTO 목록
     */
    @GetMapping
    public ResponseEntity<List<TimezoneDTO>> getAllTimezones() {
        List<TimezoneDTO> timezones = timezoneService.getAllTimezones();
        return ResponseEntity.ok(timezones);
    }

    /**
     * ID로 타임존을 조회합니다.
     * @param timezoneId 타임존 ID
     * @return 조회된 타임존 DTO
     */
    @GetMapping("/{timezoneId}")
    public ResponseEntity<TimezoneDTO> getTimezoneById(@PathVariable Long timezoneId) {
        TimezoneDTO timezone = timezoneService.getTimezoneById(timezoneId);
        return ResponseEntity.ok(timezone);
    }

    /**
     * 타임존을 업데이트합니다.
     * @param timezoneId 타임존 ID
     * @param timezoneDTO 타임존 DTO
     * @return 업데이트된 타임존 DTO
     */
    @PutMapping("/{timezoneId}")
    public ResponseEntity<TimezoneDTO> updateTimezone(@PathVariable Long timezoneId, @RequestBody TimezoneDTO timezoneDTO) {
        TimezoneDTO updatedTimezone = timezoneService.updateTimezone(timezoneId, timezoneDTO);
        return ResponseEntity.ok(updatedTimezone);
    }

    /**
     * 타임존을 삭제합니다.
     * @param timezoneId 타임존 ID
     */
    @DeleteMapping("/{timezoneId}")
    public ResponseEntity<Void> deleteTimezone(@PathVariable Long timezoneId) {
        timezoneService.deleteTimezone(timezoneId);
        return ResponseEntity.noContent().build();
    }
}
