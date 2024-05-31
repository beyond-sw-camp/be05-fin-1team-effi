package com.example.effi.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import com.example.effi.domain.Entity.Dept;
import com.example.effi.domain.Entity.Employee;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DeptRepository deptRepository;

    @DisplayName("사원 정보 저장 및 조회 테스트")
    @Test
    public void testSaveAndFindEmployee() {
        // given
        Dept dept = Dept.builder()
            .deptName("개발부")
            .build();
        Dept savedDept = deptRepository.save(dept);

        Employee employee = Employee.builder()
            .empNo(1L)
            .company("encore")
            .name("John Doe")
            .email("encore@naver.com")
            .phoneNum("123456789")
            .extensionNum("123456")
            .password("1234")
            .rank("사원")
            .dept(savedDept)
            .build();

        
        
        // when
        
        Employee savedEmployee = employeeRepository.save(employee);
        Employee foundEmployee = employeeRepository.findByEmpNo(1L);

        // then
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getName()).isEqualTo("John Doe");
    }
}
