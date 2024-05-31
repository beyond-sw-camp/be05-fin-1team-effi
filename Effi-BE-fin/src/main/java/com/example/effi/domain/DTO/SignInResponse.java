package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignInResponse {
    private Long empNo;
    private String name;
    private String rank;
    private String msg;
    private String accessToken;
    private String refreshToken;

    @Builder
    public SignInResponse(Long empNo, String name, String rank, String msg, String accessToken, String refreshToken) {
        this.empNo = empNo;
        this.name = name;
        this.rank = rank;
        this.msg = msg;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
