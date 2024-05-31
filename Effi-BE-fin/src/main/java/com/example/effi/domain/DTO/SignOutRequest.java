package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignOutRequest {
    private Long empNo;

    @Builder
    public SignOutRequest(Long empNo) {
        this.empNo = empNo;
    }
}
