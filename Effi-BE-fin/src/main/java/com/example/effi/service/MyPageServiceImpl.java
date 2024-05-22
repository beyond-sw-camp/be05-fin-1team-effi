package com.example.effi.service;

import com.example.effi.domain.DTO.MyPageResponseDTO;
import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.repository.DeptRepository;
import com.example.effi.repository.MyPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService{

    private final MyPageRepository mypageRepository;

    @Override
    public MyPageResponseDTO getEmployee(Long empId) {
        Optional<Employee> byempId = mypageRepository.findById(empId);
        Employee employee = byempId.get();

        return MyPageResponseDTO.builder()
                .empNo(employee.getEmpNo())
                .name(employee.getName())
                .company(employee.getCompany())
                .email(employee.getEmail())
                .rank(employee.getRank())
                .phoneNum(employee.getPhoneNum())
                .extensionNum(employee.getExtensionNum())
                .deptName(employee.getDept().getDeptName())
                .msg("회원 정보 조회")
                .build();
    }
}
