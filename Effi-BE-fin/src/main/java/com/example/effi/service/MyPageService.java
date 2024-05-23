package com.example.effi.service;

import com.example.effi.domain.DTO.MyPageResponseDTO;
import com.example.effi.domain.DTO.MyPageUpdateDTO;
import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.TimezoneEmp;
import com.example.effi.domain.Entitiy.Timezone;
import com.example.effi.repository.MyPageRepository;
import com.example.effi.repository.TimezoneEmpRepository;
import com.example.effi.repository.TimezoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageRepository mypageRepository;
    private final TimezoneEmpRepository timezoneEmpRepository;
    private final TimezoneRepository timezoneRepository;

    @Transactional
    public MyPageResponseDTO getEmployee(Long empId) {
        String timezoneName = mypageRepository.findDefaultTimezoneName(empId);

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
                .timezoneName(timezoneName)
                .msg("회원 정보 조회")
                .build();
    }

    public void updateEmployeeTimezone(MyPageUpdateDTO myPageUpdateDTO){
        Optional<Employee> optionalEmployee = mypageRepository.findById(myPageUpdateDTO.getEmpNo());
        if (optionalEmployee.isEmpty()) {
            throw new IllegalArgumentException("Employee not found with empNo: " + myPageUpdateDTO.getEmpNo());
        }

        Optional<Timezone> optionalTimezone = timezoneRepository.findById(myPageUpdateDTO.getTimezoneId());
        if (optionalTimezone.isEmpty()) {
            throw new IllegalArgumentException("Timezone not found with id: " + myPageUpdateDTO.getTimezoneId());
        }

        Employee employee = optionalEmployee.get();
        Timezone timezone = optionalTimezone.get();

        TimezoneEmp timezoneEmp = timezoneEmpRepository.findByEmployee(employee).orElse(null);

        if (timezoneEmp == null) {
            timezoneEmp = new TimezoneEmp(timezone, employee, true); // 기본 값으로 새로운 TimezoneEmp 객체를 생성
        } else {
            timezoneEmp.setTimezone(timezone);
        }

        timezoneEmpRepository.save(timezoneEmp);
    }


}
