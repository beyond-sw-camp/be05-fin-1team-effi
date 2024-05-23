package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.Dept;
import com.example.effi.domain.Entitiy.Employee;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmployeeDTO {
    private Long id;
    private Long empNo;
    private String company;
    private String name;
    private String email;
    private String phoneNum;
    private String extensionNum;
    private String rank;
    private String password;

    private Long deptId;

    @Builder(toBuilder = true)
    public EmployeeDTO(Long id, Long empNo, String company, String name, String email, String phoneNum,
                    String extensionNum, String rank, String password, Long deptId) {
        this.id = id;
        this.empNo = empNo;
        this.company = company;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.extensionNum = extensionNum;
        this.rank = rank;
        this.password = password;
        this.deptId = deptId;
    }

    public EmployeeDTO(Employee employee) {
        this.id = employee.getId();
        this.empNo = employee.getEmpNo();
        this.company = employee.getCompany();
        this.name = employee.getName();
        this.email = employee.getEmail();
        this.phoneNum = employee.getPhoneNum();
        this.extensionNum = employee.getExtensionNum();
        this.rank = employee.getRank();
        this.password = employee.getPassword();
        this.deptId = employee.getDept().getDeptId();
    }

    public Employee toEntity(Dept dept){
        return Employee.builder()
                .empNo(empNo)
                .company(company)
                .name(name)
                .email(email)
                .phoneNum(phoneNum)
                .extensionNum(extensionNum)
                .rank(rank)
                .password(password)
                .dept(dept)
                .build();
    }

    
}
