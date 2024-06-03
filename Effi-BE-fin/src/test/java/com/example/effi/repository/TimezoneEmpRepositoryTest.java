package com.example.effi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.effi.domain.Entity.Dept;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Timezone;
import com.example.effi.domain.Entity.TimezoneEmp;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TimezoneEmpRepositoryTest {

    @Autowired
    private TimezoneEmpRepository timezoneEmpRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TimezoneRepository timezoneRepository;

    @Autowired
    private DeptRepository deptRepository;

    private Employee employee;
    private Timezone timezone;

    @BeforeEach
    void setUp() {
        Dept dept = deptRepository.save(Dept.builder().deptName("Engineering").build());

        employee = employeeRepository.save(Employee.builder()
                .empNo(1001L)
                .company("Example Company")
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNum("010-1234-5678")
                .extensionNum("1234")
                .rank("Manager")
                .password("password")
                .dept(dept)
                .build());

        timezone = timezoneRepository.save(Timezone.builder()
                .timezoneName("KST")
                .countryCode("KR")
                .abbreviation("KST")
                .timeStart(0L)
                .gmtOffset(9 * 3600) // GMT+9 in seconds
                .dst("N")
                .build());
    }

    @Test
    @DisplayName("EmpId로 기본 타임존을 찾는다. - 성공")
    void testFindByEmpIdAndDefaultTimezone() {
        TimezoneEmp timezoneEmp = TimezoneEmp.builder()
                .employee(employee)
                .timezone(timezone)
                .defaultTimezone(true)
                .build();
        timezoneEmpRepository.save(timezoneEmp);

        Optional<TimezoneEmp> result = timezoneEmpRepository.findByEmpIdAndDefaultTimezone(employee.getId());

        assertTrue(result.isPresent());
        assertEquals(employee.getId(), result.get().getEmployee().getId());
        assertTrue(result.get().getDefaultTimezone());
    }

    @Test
    @DisplayName("EmpId로 기본 타임존을 찾는다. - 실패: 타임존이 없을 경우")
    void testFindByEmpIdAndDefaultTimezoneFail() {
        Optional<TimezoneEmp> result = timezoneEmpRepository.findByEmpIdAndDefaultTimezone(employee.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("EmpId와 타임존 ID로 타임존을 찾는다. - 성공")
    void testFindByEmployeeIdAndTimezone_TimezoneId() {
        TimezoneEmp timezoneEmp = TimezoneEmp.builder()
                .employee(employee)
                .timezone(timezone)
                .defaultTimezone(true)
                .build();
        timezoneEmpRepository.save(timezoneEmp);

        Optional<TimezoneEmp> result = timezoneEmpRepository.findByEmployeeIdAndTimezone_TimezoneId(employee.getId(), timezone.getTimezoneId());

        assertTrue(result.isPresent());
        assertEquals(timezone.getTimezoneId(), result.get().getTimezone().getTimezoneId());
    }

    @Test
    @DisplayName("EmpId와 타임존 ID로 타임존을 찾는다. - 실패: 타임존이 없을 경우")
    void testFindByEmployeeIdAndTimezone_TimezoneIdFail() {
        Optional<TimezoneEmp> result = timezoneEmpRepository.findByEmployeeIdAndTimezone_TimezoneId(employee.getId(), timezone.getTimezoneId());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("EmpId와 기본 타임존으로 타임존을 찾는다. - 성공")
    void testFindByEmployeeIdAndDefaultTimezone() {
        TimezoneEmp timezoneEmp = TimezoneEmp.builder()
                .employee(employee)
                .timezone(timezone)
                .defaultTimezone(true)
                .build();
        timezoneEmpRepository.save(timezoneEmp);

        Optional<TimezoneEmp> result = timezoneEmpRepository.findByEmployeeIdAndDefaultTimezone(employee.getId(), true);

        assertTrue(result.isPresent());
        assertTrue(result.get().getDefaultTimezone());
    }

    @Test
    @DisplayName("EmpId와 기본 타임존으로 타임존을 찾는다. - 실패: 타임존이 없을 경우")
    void testFindByEmployeeIdAndDefaultTimezoneFail() {
        Optional<TimezoneEmp> result = timezoneEmpRepository.findByEmployeeIdAndDefaultTimezone(employee.getId(), true);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("EmpId로 타임존을 찾는다. - 성공")
    void testFindByEmployeeId() {
        TimezoneEmp timezoneEmp1 = TimezoneEmp.builder()
                .employee(employee)
                .timezone(timezone)
                .defaultTimezone(true)
                .build();
        timezoneEmpRepository.save(timezoneEmp1);

        List<TimezoneEmp> result = timezoneEmpRepository.findByEmployeeId(employee.getId());

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(employee.getId(), result.get(0).getEmployee().getId());
    }

    @Test
    @DisplayName("EmpId로 타임존을 찾는다. - 실패: 타임존이 없을 경우")
    void testFindByEmployeeIdFail() {
        List<TimezoneEmp> result = timezoneEmpRepository.findByEmployeeId(employee.getId());

        assertTrue(result.isEmpty());
    }


    @Test
    @DisplayName("EmpId로 타임존 개수를 센다. - 성공")
    void testCountByEmployeeId() {
        TimezoneEmp timezoneEmp = TimezoneEmp.builder()
                .employee(employee)
                .timezone(timezone)
                .defaultTimezone(true)
                .build();
        timezoneEmpRepository.save(timezoneEmp);

        long count = timezoneEmpRepository.countByEmployeeId(employee.getId());

        assertEquals(1, count);
    }

    @Test
    @DisplayName("EmpId로 타임존 개수를 센다. - 실패: 타임존이 없을 경우")
    void testCountByEmployeeIdFail() {
        long count = timezoneEmpRepository.countByEmployeeId(employee.getId());

        assertEquals(0, count);
    }

    @Test
    @DisplayName("기본 타임존으로 타임존 개수를 센다. - 성공")
    void testCountByDefaultTimezone() {
        TimezoneEmp timezoneEmp = TimezoneEmp.builder()
                .employee(employee)
                .timezone(timezone)
                .defaultTimezone(true)
                .build();
        timezoneEmpRepository.save(timezoneEmp);

        long count = timezoneEmpRepository.countByDefaultTimezone(true);

        assertEquals(1, count);
    }

    @Test
    @DisplayName("기본 타임존으로 타임존 개수를 센다. - 실패: 타임존이 없을 경우")
    void testCountByDefaultTimezoneFail() {
        long count = timezoneEmpRepository.countByDefaultTimezone(true);

        assertEquals(0, count);
    }
}
