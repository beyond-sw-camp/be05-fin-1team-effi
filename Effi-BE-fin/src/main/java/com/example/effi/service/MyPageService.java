package com.example.effi.service;

import com.example.effi.domain.DTO.MyPageResponseDTO;
import com.example.effi.domain.DTO.MyPageTimezoneDTO;
import com.example.effi.domain.DTO.MyPageUpdateDTO;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Timezone;
import com.example.effi.domain.Entity.TimezoneEmp;
import com.example.effi.repository.MyPageRepository;
import com.example.effi.repository.TimezoneEmpRepository;
import com.example.effi.repository.TimezoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageRepository myPageRepository;
    private final TimezoneEmpRepository timezoneEmpRepository;
    private final TimezoneRepository timezoneRepository;

    // 내 정보 조회
    public MyPageResponseDTO getEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long empId = Long.valueOf(authentication.getName());

        Optional<Employee> byemp = myPageRepository.findById(empId);
        if (!byemp.isPresent()) {
            throw new IllegalArgumentException("사원이 검색되지 않습니다. id: " + empId);
        }

        String timezoneName = myPageRepository.findDefaultTimezoneName(empId);
        if (timezoneName == null) {
            throw new IllegalArgumentException("기본 타임존을 찾을 수 없습니다. 직원 ID: " + empId);
        }

        Employee employee = byemp.get();
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
    public void updateEmployeeTimezone(MyPageUpdateDTO myPageUpdateDTO) {
        // 인증 정보를 통해 empId 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long empId = Long.valueOf(authentication.getName());

        // 기본 타임존 엔티티 조회
        TimezoneEmp timezoneEmp = timezoneEmpRepository.findByEmpIdAndDefaultTimezone(empId)
                .orElseThrow(() -> new IllegalArgumentException("기본 타임존을 찾을 수 없습니다. 직원 ID: " + empId));

        // 새로운 타임존 엔티티 조회
        Timezone timezone = timezoneRepository.findById(myPageUpdateDTO.getTimezoneId())
                .orElseThrow(() -> new IllegalArgumentException("기본 타임존을 찾을 수 없습니다. 시간대 ID: " + myPageUpdateDTO.getTimezoneId()));

        // 타임존 업데이트 및 저장
        timezoneEmp.setTimezone(timezone);
        timezoneEmpRepository.save(timezoneEmp);
    }

    // 타임존 리스트
    public List<MyPageTimezoneDTO> getTimezones() {
        return myPageRepository.findAllTimezonesGroupedByName();
    }
}
