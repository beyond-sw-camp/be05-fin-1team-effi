package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageUpdateDTO {
    private Long id;
    private Long empNo;
    private String timezoneName;

    @Builder
    public MyPageUpdateDTO(Long id, Long empNo, String timezoneName){
        this.id = id;
        this.empNo = empNo;
        this.timezoneName = timezoneName;
    }

}
