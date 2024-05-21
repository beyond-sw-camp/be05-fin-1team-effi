package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.Dept;
import com.example.effi.domain.Entitiy.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageResponseDTO {
    private Long id;
    private Long empNo;
    private String company;
    private String name;
    private String email;
    private String phoneNum;
    private String extensionNum;
    private String rank;

    private String deptName;

    private String msg;

    public MyPageResponseDTO(Long id, Long empNo, String company, String name, String email, String phoneNum,
                              String extensionNum, String rank, Dept dept, String msg) {
        this.id = id;
        this.empNo = empNo;
        this.company = company;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.extensionNum = extensionNum;
        this.rank = rank;
        this.deptName = dept.getDeptName();
        this.msg = msg;


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
