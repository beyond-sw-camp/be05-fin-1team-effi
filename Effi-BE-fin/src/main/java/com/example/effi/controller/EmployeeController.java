package com.example.effi.controller;

import com.example.effi.domain.DTO.EmployeeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.effi.domain.DTO.SignInRequest;
import com.example.effi.domain.DTO.SignInResponse;
import com.example.effi.service.EmployeeService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(@RequestBody SignInRequest request) {
        SignInResponse response = employeeService.signIn(request);
        if(response.getMsg().equals("로그인 성공")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // 전체조회
    @GetMapping("/find")
    public ResponseEntity<List<EmployeeDTO>> findAll() {
        List<EmployeeDTO> employees = employeeService.findAll();
        return ResponseEntity.ok(employees);
    }
}
