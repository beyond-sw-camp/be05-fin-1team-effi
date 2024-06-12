package com.example.effi.controller;

import com.example.effi.domain.DTO.GlobalResponse;
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
     * 
     * @param timezoneDTO 타임존 DTO
     * @return 추가된 타임존 DTO
     */
    @PostMapping
    public ResponseEntity<GlobalResponse<TimezoneDTO>> addTimezone(@RequestBody TimezoneDTO timezoneDTO) {
        GlobalResponse<TimezoneDTO> response = timezoneService.addTimezone(timezoneDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * 모든 타임존을 조회합니다.
     * 
     * @return 타임존 DTO 목록
     */
    @GetMapping
    public ResponseEntity<GlobalResponse<List<TimezoneDTO>>> getAllTimezones() {
        GlobalResponse<List<TimezoneDTO>> response = timezoneService.getAllTimezones();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * ID로 타임존을 조회합니다.
     * 
     * @param timezoneId 타임존 ID
     * @return 조회된 타임존 DTO
     */
    @GetMapping("/{timezoneId}")
    public ResponseEntity<GlobalResponse<TimezoneDTO>> getTimezoneById(@PathVariable Long timezoneId) {
        GlobalResponse<TimezoneDTO> response = timezoneService.getTimezoneById(timezoneId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * 타임존을 업데이트합니다.
     * 
     * @param timezoneId  타임존 ID
     * @param timezoneDTO 타임존 DTO
     * @return 업데이트된 타임존 DTO
     */
    @PutMapping("/{timezoneId}")
    public ResponseEntity<GlobalResponse<TimezoneDTO>> updateTimezone(@PathVariable("timezoneId") Long timezoneId,
            @RequestBody TimezoneDTO timezoneDTO) {
        GlobalResponse<TimezoneDTO> response = timezoneService.updateTimezone(timezoneId, timezoneDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * 타임존을 삭제합니다.
     * 
     * @param timezoneId 타임존 ID
     */
    @DeleteMapping("/{timezoneId}")
    public ResponseEntity<GlobalResponse<?>> deleteTimezone(@PathVariable("timezoneId") Long timezoneId) {
        GlobalResponse<?> response = timezoneService.deleteTimezone(timezoneId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    

}
