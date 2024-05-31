package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponse {
    private final String msg;

    @Builder
    public ApiResponse(String msg) {
        this.msg = msg;
    }
}
