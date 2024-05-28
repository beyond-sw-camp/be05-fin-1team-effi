package com.example.effi.service;

import com.example.effi.domain.DTO.MyPageUpdateDTO;
import com.example.effi.domain.DTO.TimezoneDTO;
import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.Timezone;
import com.example.effi.domain.Entitiy.TimezoneEmp;
import com.example.effi.repository.TimezoneEmpRepository;
import com.example.effi.repository.TimezoneRepository;
import com.example.effi.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimezoneEmpService {
    private final TimezoneEmpRepository timezoneEmpRepository;
    private final TimezoneRepository timezoneRepository;
    private final EmployeeRepository employeeRepository;

   @Transactional
    public void addTimezoneForEmployee(Long empId, Long timezoneId, boolean isDefault) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 직원 ID입니다."));
        Timezone timezone = timezoneRepository.findById(timezoneId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 시간대 ID입니다."));

        boolean existsTimezone = timezoneEmpRepository.findByEmployeeIdAndTimezone_TimezoneId(empId, timezoneId).isPresent();
        if (existsTimezone) {
            throw new IllegalArgumentException("직원에게 이미 이 시간대가 존재합니다.");
        }

        if (isDefault) {
            TimezoneEmp currentDefault = timezoneEmpRepository.findByEmployeeIdAndDefaultTimezone(empId, true).orElse(null);
            if (currentDefault != null) {
                currentDefault.setDefaultTimezone(false);
                timezoneEmpRepository.save(currentDefault);
            }
        }

        TimezoneEmp timezoneEmp = TimezoneEmp.builder()
                .timezone(timezone)
                .employee(employee)
                .defaultTimezone(isDefault)
                .build();

        timezoneEmpRepository.save(timezoneEmp);
    }

    @Transactional(readOnly = true)
    public List<TimezoneDTO> getTimezonesForEmployee(Long empId) {
        List<TimezoneEmp> timezoneEmps = timezoneEmpRepository.findByEmployeeId(empId);
        return timezoneEmps.stream()
                .map(te -> TimezoneDTO.fromEntity(te.getTimezone()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateDefaultTimezoneForEmployee(Long empId, Long newTimezoneId) {
        // 현재 기본 타임존을 비활성화
        TimezoneEmp currentDefaultTimezone = timezoneEmpRepository.findByEmployeeIdAndDefaultTimezone(empId, true)
                .orElseThrow(() -> new IllegalArgumentException("직원에게 기본 타임존이 존재하지 않습니다."));

        currentDefaultTimezone.setDefaultTimezone(false);
        timezoneEmpRepository.save(currentDefaultTimezone);

        // 새로운 타임존을 찾기
        Timezone newTimezone = timezoneRepository.findById(newTimezoneId)
                .orElseThrow(() -> new IllegalArgumentException("시간대를 찾을 수 없습니다. 시간대 ID: " + newTimezoneId));

        // 새로운 기본 타임존을 추가
        TimezoneEmp newDefaultTimezone = TimezoneEmp.builder()
                .employee(currentDefaultTimezone.getEmployee())
                .timezone(newTimezone)
                .defaultTimezone(true)
                .build();
        
        timezoneEmpRepository.save(newDefaultTimezone);
    }

    @Transactional
    public void removeTimezoneForEmployee(Long empId, Long timezoneId) {
        TimezoneEmp timezoneEmp = timezoneEmpRepository.findByEmployeeIdAndTimezone_TimezoneId(empId, timezoneId)
                .orElseThrow(() -> new IllegalArgumentException("직원에게 해당 시간대가 존재하지 않습니다."));
        timezoneEmpRepository.delete(timezoneEmp);
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
