package com.example.effi.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.effi.domain.DTO.EmployeeDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.effi.config.TokenProvider;
import com.example.effi.domain.DTO.SignInRequest;
import com.example.effi.domain.DTO.SignInResponse;
import com.example.effi.domain.DTO.SignOutRequest;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.RefreshToken;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.RefreshTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;


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

    @Transactional
    public String signOut(String token)  throws JsonProcessingException {
        String subject = tokenProvider.decodeJwtPayloadSubject(token);
        Long empNo = Long.parseLong(subject.split(":")[0]);
        Employee employee = employeeRepository.findByEmpNo(empNo);
        if (refreshTokenRepository.findById(employee.getId()).isEmpty()){
            return "로그아웃 실패";
        }
        try {
            refreshTokenRepository.deleteById(employee.getId());
        } catch (Exception e) {
            return "로그아웃 실패";
        }
        return "로그아웃 성공";
    }

    //empno -> empId 찾기
    public Long findEmpIdByEmpNo(Long empNo) {
        return employeeRepository.findByEmpNo(empNo).getId();
    }

    // dept 사람들 empid만 찾기
    public List<Long> findEmpIdByDept(Long deptId) {
        List<Employee> lst = employeeRepository.findAllByDept_DeptId(deptId);
        List<Long> rtn = new ArrayList<>();
        for (Employee emp : lst) {
            rtn.add(emp.getId());
        }
        return rtn;
    }

    // dept 사람들 emp 찾기
    public List<EmployeeDTO> findAllByDeptId(Long deptId) {
        List<EmployeeDTO> lst = new ArrayList<>();
        employeeRepository.findAllByDept_DeptId(deptId)
                .forEach(employee -> {
                    lst.add(new EmployeeDTO(employee));
                });
        return lst;
    }

    // 전체 찾기
    public List<EmployeeDTO> findAll(){
        List<Employee> lst = employeeRepository.findAll();
        List<EmployeeDTO> rtn = new ArrayList<>();
        for (Employee emp : lst) {
            rtn.add(new EmployeeDTO(emp));
        }
        return rtn;
    }

    // empId로 emp 리턴
    public EmployeeDTO findById(Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            return new EmployeeDTO(employee.get());
        }
        return null;
    }
}
