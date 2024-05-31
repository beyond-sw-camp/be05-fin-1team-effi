package com.example.effi.domain.DTO;

import com.example.effi.domain.Entity.Dept;
import com.example.effi.domain.Entity.Employee;

import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
public class MyPageRequestDTO {

    private Long id;
    private Long empNo;
    private String company;
    private String name;
    private String email;
    private String phoneNum;
    private String extensionNum;
    private String rank;

    private Long deptId;

    public MyPageRequestDTO(Long id, Long empNo, String company, String name, String email, String phoneNum,
                       String extensionNum, String rank, Long deptId) {
        this.id = id;
        this.empNo = empNo;
        this.company = company;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.extensionNum = extensionNum;
        this.rank = rank;
        this.deptId = deptId;
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
                .dept(dept)
                .build();
    }

}
