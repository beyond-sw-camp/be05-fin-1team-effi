package com.example.effi.service;


import java.util.Optional;

import com.example.effi.domain.DTO.EmployeeDTO;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.effi.config.TokenProvider;
import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.RefreshToken;
import com.example.effi.domain.DTO.SignInRequest;
import com.example.effi.domain.DTO.SignInResponse;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
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
        String accessToken = tokenProvider.createToken(String.format("%s:%s", employee.getEmpNo(), employee.getRank()));
        String refreshToken = tokenProvider.createRefreshToken();
        Optional<RefreshToken> oldRefreshToken = refreshTokenRepository.findById(employee.getId());
        if (oldRefreshToken.isEmpty()) {
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .tokenId(employee.getId())
                    .refreshToken(refreshToken)
                    .employee(employee)
                    .build();
            System.out.println("empty " + newRefreshToken.toString());
            refreshTokenRepository.save(newRefreshToken);
        } else {
            RefreshToken newRefreshToken = oldRefreshToken.get().toBuilder()
                    .refreshToken(refreshToken)
                    .build();
            System.out.println("present " + newRefreshToken.toString());
            refreshTokenRepository.save(newRefreshToken);
        }
        return SignInResponse.builder()
                .empNo(employee.getEmpNo())
                .name(employee.getName())
                .rank(employee.getRank())
                .msg("로그인 성공")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //empno -> empId 찾기
    public Long findEmpIdByEmpNo(Long empNo) {
        return employeeRepository.findByEmpNo(empNo).getId();
    }
}
