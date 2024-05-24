// package com.example.effi.service;

// import com.example.effi.domain.DTO.TimezoneDTO;
// import com.example.effi.domain.Entitiy.Employee;
// import com.example.effi.domain.Entitiy.Timezone;
// import com.example.effi.domain.Entitiy.TimezoneEmp;
// import com.example.effi.repository.TimezoneEmpRepository;
// import com.example.effi.repository.TimezoneRepository;
// import com.example.effi.repository.EmployeeRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.stream.Collectors;

// /**
//  * TimezoneEmpService는 TimezoneEmp 엔티티와 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
//  */
// @Service
// @RequiredArgsConstructor
// public class TimezoneEmpService {
//     private final TimezoneEmpRepository timezoneEmpRepository;
//     private final TimezoneRepository timezoneRepository;
//     private final EmployeeRepository employeeRepository;

//      /**
//      * 직원에게 타임존을 추가합니다.
//      * @param empId 직원 ID
//      * @param timezoneId 타임존 ID
//      * @param isDefault 기본 타임존 여부
//      */
//     @Transactional
//     public void addTimezoneForEmployee(Long empId, Long timezoneId, boolean isDefault) {
//         Employee employee = employeeRepository.findById(empId)
//                 .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));
//         Timezone timezone = timezoneRepository.findById(timezoneId)
//                 .orElseThrow(() -> new IllegalArgumentException("Invalid timezone ID"));

//         if (isDefault) {
//             boolean existsDefault = timezoneEmpRepository.findByEmployeeIdAndDefaultTimezone(empId, true).isPresent();
//             if (existsDefault) {
//                 throw new IllegalArgumentException("Employee already has a default timezone");
//             }
//         }

//         TimezoneEmp timezoneEmp = TimezoneEmp.builder()
//                 .timezone(timezone)
//                 .employee(employee)
//                 .defaultTimezone(isDefault)
//                 .build();

//         timezoneEmpRepository.save(timezoneEmp);
//     }

//     /**
//      * 직원의 타임존 목록을 조회합니다.
//      * @param empId 직원 ID
//      * @return 타임존 DTO 목록
//      */
//     @Transactional(readOnly = true)
//     public List<TimezoneDTO> getTimezonesForEmployee(Long empId) {
//         List<TimezoneEmp> timezoneEmps = timezoneEmpRepository.findByEmployeeId(empId);
//         return timezoneEmps.stream()
//                 .map(te -> TimezoneDTO.fromEntity(te.getTimezone()))
//                 .collect(Collectors.toList());
//     }

//     /**
//      * 직원의 기본 타임존을 업데이트합니다.
//      * @param empId 직원 ID
//      * @param newTimezoneId 새로운 타임존 ID
//      */
//     @Transactional
//     public void updateDefaultTimezoneForEmployee(Long empId, Long newTimezoneId) {
//         // 기존의 기본 타임존을 false로 설정
//         TimezoneEmp currentDefaultTimezone = timezoneEmpRepository.findByEmployeeIdAndDefaultTimezone(empId, true)
//                 .orElseThrow(() -> new IllegalArgumentException("No default timezone found for employee"));

//         // 새로운 기본 타임존을 찾기
//         TimezoneEmp newDefaultTimezone = timezoneEmpRepository.findByEmployeeIdAndTimezoneTimezoneId(empId, newTimezoneId)
//                 .orElseThrow(() -> new IllegalArgumentException("New timezone not found for employee"));

//         if (currentDefaultTimezone.equals(newDefaultTimezone)) {
//             throw new IllegalArgumentException("New default timezone is the same as the current default timezone");
//         }

//         // 기존의 기본 타임존을 false로 설정
//         currentDefaultTimezone = currentDefaultTimezone.toBuilder()
//                 .defaultTimezone(false)
//                 .build();
//         timezoneEmpRepository.save(currentDefaultTimezone);

//         // 새로운 기본 타임존을 true로 설정
//         newDefaultTimezone = newDefaultTimezone.toBuilder()
//                 .defaultTimezone(true)
//                 .build();
//         timezoneEmpRepository.save(newDefaultTimezone);
//     }

//     /**
//      * 직원의 타임존을 삭제합니다.
//      * @param empId 직원 ID
//      * @param timezoneId 타임존 ID
//      */
//     @Transactional
//     public void removeTimezoneForEmployee(Long empId, Long timezoneId) {
//         TimezoneEmp timezoneEmp = timezoneEmpRepository.findByEmployeeIdAndTimezoneTimezoneId(empId, timezoneId)
//                 .orElseThrow(() -> new IllegalArgumentException("Timezone not found for employee"));
//         timezoneEmpRepository.delete(timezoneEmp);
//     }
// }
package com.example.effi.service;

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
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));
        Timezone timezone = timezoneRepository.findById(timezoneId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid timezone ID"));

        boolean existsTimezone = timezoneEmpRepository.findByEmployeeIdAndTimezone_TimezoneId(empId, timezoneId).isPresent();  // 변경된 메서드 사용
        if (existsTimezone) {
            throw new IllegalArgumentException("Employee already has this timezone");
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
        TimezoneEmp currentDefaultTimezone = timezoneEmpRepository.findByEmployeeIdAndDefaultTimezone(empId, true)
                .orElseThrow(() -> new IllegalArgumentException("No default timezone found for employee"));

        TimezoneEmp newDefaultTimezone = timezoneEmpRepository.findByEmployeeIdAndTimezone_TimezoneId(empId, newTimezoneId)
                .orElseThrow(() -> new IllegalArgumentException("New timezone not found for employee"));

        if (currentDefaultTimezone.equals(newDefaultTimezone)) {
            throw new IllegalArgumentException("New default timezone is the same as the current default timezone");
        }

        currentDefaultTimezone.setDefaultTimezone(false);
        timezoneEmpRepository.save(currentDefaultTimezone);

        newDefaultTimezone.setDefaultTimezone(true);
        timezoneEmpRepository.save(newDefaultTimezone);
    }

    @Transactional
    public void removeTimezoneForEmployee(Long empId, Long timezoneId) {
        TimezoneEmp timezoneEmp = timezoneEmpRepository.findByEmployeeIdAndTimezone_TimezoneId(empId, timezoneId)
                .orElseThrow(() -> new IllegalArgumentException("Timezone not found for employee"));
        timezoneEmpRepository.delete(timezoneEmp);
    }
}
