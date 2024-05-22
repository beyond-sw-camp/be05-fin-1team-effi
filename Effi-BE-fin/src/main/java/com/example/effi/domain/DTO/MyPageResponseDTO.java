package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageResponseDTO {
    private Long empNo;
    private String company;
    private String name;
    private String email;
    private String phoneNum;
    private String extensionNum;
    private String rank;

    private String deptName;

    private String timezoneName;

    private String msg;

    @Builder
    public MyPageResponseDTO( Long empNo, String company, String name, String email, String phoneNum,
                              String extensionNum, String rank, String deptName,String timezoneName, String msg) {
        this.empNo = empNo;
        this.company = company;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.extensionNum = extensionNum;
        this.rank = rank;
        this.deptName = deptName;
        this.timezoneName = timezoneName;
        this.msg = msg;
    }

}
