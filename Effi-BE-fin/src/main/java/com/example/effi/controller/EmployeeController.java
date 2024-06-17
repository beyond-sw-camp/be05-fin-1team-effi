package com.example.effi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.effi.domain.DTO.GlobalResponse;
import com.example.effi.domain.DTO.SignInRequest;
import com.example.effi.domain.DTO.SignInResponse;
import com.example.effi.domain.DTO.SignOutRequest;
import com.example.effi.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping("/signin")
    public ResponseEntity<GlobalResponse> signIn(@RequestBody SignInRequest request) {
        SignInResponse response = employeeService.signIn(request);
        if(response.getMsg().equals("로그인 성공")) {
            return ResponseEntity.ok().body(GlobalResponse.builder()
                                            .message(HttpStatus.OK.getReasonPhrase())
                                            .status(HttpStatus.OK.value())
                                            .data(response)
                                            .build());
        }
        return ResponseEntity.badRequest().body(GlobalResponse.builder()
                                            .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                            .status(HttpStatus.BAD_REQUEST.value())
                                            .data(response)
                                            .build());
    }

    @PostMapping("/signout")// 로그아웃. 단 request 에 empNo 를 직접 담아서 보내는 것이 아닌 토큰을 통해 empNo 를 가져오는 것으로 수정한다.
    public ResponseEntity<GlobalResponse> signOut(@RequestBody Map<String, String> request)  throws JsonProcessingException {
        String token = request.get("token");
        String response = employeeService.signOut(token);
        if(response.equals("로그아웃 실패")) {
            return ResponseEntity.badRequest().body(GlobalResponse.builder()
                                                        .message(response)
                                                        .status(HttpStatus.BAD_REQUEST.value())
                                                        .build());
        }
        return ResponseEntity.ok().body(GlobalResponse.builder()
                                        .message(response)
                                        .status(HttpStatus.OK.value())
                                        .build());
    }

    // hasAnyAuthority('관리자', '사원') : '관리자' 또는 '사원' 권한이 있어야 접근 가능
    @PreAuthorize("hasAuthority('사원')")
    @PostMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test");
    }

}
