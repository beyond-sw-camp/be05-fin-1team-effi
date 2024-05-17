package com.example.effi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.effi.domain.Employee;
import com.example.effi.domain.SignInRequest;
import com.example.effi.domain.SignInResponse;
import com.example.effi.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest request) {
        Employee employee = employeeRepository.findByEmpNo(request.getEmpNo());
        if (employee == null) {
            return SignInResponse.builder()
                    .msg("사원번호가 존재하지 않습니다.")
                    .build();
        }
        if (passwordEncoder.matches(request.getPassword(), employee.getPassword()) == false){
            return SignInResponse.builder()
                    .msg("비밀번호가 일치하지 않습니다.")
                    .build();
        }
        return SignInResponse.builder()
                .empNo(employee.getEmpNo())
                .name(employee.getName())
                .rank(employee.getRank())
                .msg("로그인 성공")
                .build();
    }
}
