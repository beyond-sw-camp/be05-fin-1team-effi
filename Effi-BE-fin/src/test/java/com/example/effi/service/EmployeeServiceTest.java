package com.example.effi.service;

import com.example.effi.domain.DTO.EmployeeDTO;
import com.example.effi.domain.Entity.Dept;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.repository.DeptRepository;
import com.example.effi.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeeServiceTest {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DeptRepository deptRepository;

    @BeforeAll
    public void setUp() {
        Dept testDept = deptRepository.save(Dept.builder()
                .deptName("TestDept").build()
        );
        employeeRepository.save(Employee.builder()
                .empNo(1L)
                .name("John Doe")
                .password("password")
                .company("Example Corp")
                .email("john.doe@example.com")
                .rank("Manager")
                .phoneNum("123-456-7890")
                .extensionNum("1234")
                .dept(testDept)
                .build());

        employeeRepository.save(Employee.builder()
                .empNo(2L)
                .name("Mike Lee")
                .password("password")
                .company("Example Corp")
                .email("mikelee@example.com")
                .rank("Manager")
                .phoneNum("123-456-7890")
                .extensionNum("1234")
                .dept(testDept)
                .build());
    }

    //empno -> empId 찾기
    @DisplayName("Emp Id 찾기 - 성공")
    @Test
    public void findEmpIdByEmpNoTest(){
        Long empNo = 1L;
        Long empIdByEmpNo = employeeService.findEmpIdByEmpNo(empNo);

        assertThat(empIdByEmpNo).isNotNull();
        assertThat(empIdByEmpNo).isEqualTo(empNo);
    }

    @DisplayName("Emp Id 찾기 - 실패")
    @Test
    public void findEmpIdByEmpNoTestFail(){
        Long empNo = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.findEmpIdByEmpNo(empNo);
        });
        assertEquals("사원 No가 유효하지 않습니다.", exception.getMessage());
    }

    // dept 사람들 empid만 찾기
    @DisplayName("Dept Id로 Emp Id 찾기 - 성공")
    @Test
    public void findEmpIdByDeptTest(){
        Long deptId = 1L;
        List<Long> empIdByDept = employeeService.findEmpIdByDept(deptId);
        assertThat(empIdByDept).isNotNull();
        assertThat(empIdByDept.size()).isEqualTo(2);
        assertThat(empIdByDept.get(0)).isEqualTo(1L);
    }

    @DisplayName("Dept Id로 Emp Id 찾기 - 실패")
    @Test
    public void findEmpIdByDeptTestFail(){
        Long deptId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.findEmpIdByDept(deptId);
        });
        assertEquals("부서 Id가 유효하지 않습니다.", exception.getMessage());
    }

    // dept 사람들 emp 찾기
    @DisplayName("Dept Id로 Emp 찾기 - 성공")
    @Test
    public void findAllByDeptIdTest(){
        Long deptId = 1L;

        List<EmployeeDTO> allByDeptId = employeeService.findAllByDeptId(deptId);
        assertThat(allByDeptId).isNotNull();
        assertThat(allByDeptId.size()).isEqualTo(2);
        assertThat(allByDeptId.get(0).getId()).isEqualTo(1L);
        assertThat(allByDeptId.get(1).getId()).isEqualTo(2L);
    }

    @DisplayName("Dept Id로 Emp 찾기 - 실패")
    @Test
    public void findAllByDeptIdTestFail(){
        Long deptId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.findAllByDeptId(deptId);
        });
        assertEquals("부서 Id가 유효하지 않습니다.", exception.getMessage());
    }

    // 전체 찾기
    @DisplayName("전체 찾기 - 성공")
    @Test
    public void findAllTest(){
        List<EmployeeDTO> all = employeeService.findAll();
        assertThat(all).isNotNull();
        assertThat(all.size()).isEqualTo(2);
    }

    // empId로 emp 리턴
    @DisplayName("Emp Id로 Emp 찾기 - 성공")
    @Test
    public void findByIdTest(){
        Long empId = 1L;

        EmployeeDTO byId = employeeService.findById(empId);
        assertThat(byId).isNotNull();
        assertThat(byId.getEmpNo()).isEqualTo(empId);
        assertThat(byId.getName()).isEqualTo("John Doe");
    }

    @DisplayName("Emp Id로 Emp 찾기 - 실패")
    @Test
    public void findByIdTestFail(){
        Long empId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.findById(empId);
        });
        assertEquals("사원 Id가 유효하지 않습니다.", exception.getMessage());
    }
}
