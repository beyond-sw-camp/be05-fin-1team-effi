package com.example.effi.controller;

import com.example.effi.domain.DTO.MyPageResponseDTO;
import com.example.effi.domain.DTO.MyPageTimezoneDTO;
import com.example.effi.domain.DTO.MyPageUpdateDTO;
import com.example.effi.domain.Entity.Timezone;
import com.example.effi.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/me")
    public ResponseEntity<MyPageResponseDTO> mypageView() {
        try {
            MyPageResponseDTO responseDTO = myPageService.getEmployee();
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateTimezone(@RequestBody MyPageUpdateDTO myPageUpdateDTO) {
        try {
            myPageService.updateEmployeeTimezone(myPageUpdateDTO);
            return ResponseEntity.ok("Timezone updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/timezones")
    public ResponseEntity<List<MyPageTimezoneDTO>> getTimezones() {
        List<MyPageTimezoneDTO> timezones = myPageService.getTimezones();
        return ResponseEntity.ok(timezones);
    }

    @GetMapping("/timezone/{empNo}")
    public ResponseEntity<String> getEmployeeTimezone(@PathVariable Long empNo) {
        try {
            String timezoneName = myPageService.getEmployeeTimezoneName(empNo);
            return ResponseEntity.ok(timezoneName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
