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

        Optional<Employee> byeId = mypageRepository.findById(empId);

        Employee employee = byeId.get();
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

    // 기본 시간대 update
    @Transactional
    public void updateEmployeeTimezone(MyPageUpdateDTO myPageUpdateDTO) {
        TimezoneEmp timezoneEmp = timezoneEmpRepository.findByEmpIdAndDefaultTimezone(myPageUpdateDTO.getEmpId())
                .orElseThrow(() -> new IllegalArgumentException("기본 타임존을 찾을 수 없습니다. 직원 ID: " + myPageUpdateDTO.getEmpId()));

        Timezone timezone = timezoneRepository.findById(myPageUpdateDTO.getTimezoneId())
                .orElseThrow(() -> new IllegalArgumentException("시간대를 찾을 수 없습니다. 시간대 ID: " + myPageUpdateDTO.getTimezoneId()));

        timezoneEmp.setTimezone(timezone);
        timezoneEmpRepository.save(timezoneEmp);
    }


}
