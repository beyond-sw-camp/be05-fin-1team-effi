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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public MyPageResponseDTO getEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String empId = authentication.getName();

        String timezoneName = mypageRepository.findDefaultTimezoneName(Long.valueOf(empId));

        Optional<Employee> byemp = mypageRepository.findById(Long.valueOf(empId));
        if(!byemp.isPresent()){
            throw new IllegalArgumentException("Employee not found with id: " + empId);
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
    @Transactional
    public void updateEmployeeTimezone(MyPageUpdateDTO myPageUpdateDTO) {

        // 인증 정보를 통해 empId 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmpId = authentication.getName();

        // myPageUpdateDTO의 empId와 일치하는지 확인 (필요한 경우)
        if (!authenticatedEmpId.equals(String.valueOf(myPageUpdateDTO.getEmpId()))) {
            throw new IllegalArgumentException("인증된 사용자와 요청된 사용자가 일치하지 않습니다.");
        }

        // 기본 타임존 엔티티 조회
        TimezoneEmp timezoneEmp = timezoneEmpRepository.findByEmpIdAndDefaultTimezone(myPageUpdateDTO.getEmpId())
                .orElseThrow(() -> new IllegalArgumentException("기본 타임존을 찾을 수 없습니다. 직원 ID: " + myPageUpdateDTO.getEmpId()));

        // 새로운 타임존 엔티티 조회
        Timezone timezone = timezoneRepository.findById(myPageUpdateDTO.getTimezoneId())
                .orElseThrow(() -> new IllegalArgumentException("기본 타임존을 찾을 수 없습니다. 시간대 ID: " + myPageUpdateDTO.getTimezoneId()));

        // 타임존 업데이트 및 저장
        timezoneEmp.setTimezone(timezone);
        timezoneEmpRepository.save(timezoneEmp);
    }


}
