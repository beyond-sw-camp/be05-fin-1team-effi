package com.example.effi.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignInResponse {
    private Long empNo;
    private String name;
    private String rank;
    private String msg;
    private String token;

    @Builder
    public SignInResponse(Long empNo, String name, String rank, String msg, String token) {
        this.empNo = empNo;
        this.name = name;
        this.rank = rank;
        this.msg = msg;
        this.token = token;
    }
}
