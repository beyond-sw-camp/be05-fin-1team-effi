package com.example.effi.domain;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RoutineDTO {
    private Long routineId;
    private Date routineStart;
    private Date routineEnd;
    private String routineCycle;
    private Boolean deleteYn;

    public RoutineDTO(Long routineId, Date routineStart, Date routineEnd, String routineCycle, Boolean deleteYn) {
        this.routineId = routineId;
        this.routineStart = routineStart;
        this.routineEnd = routineEnd;
        this.routineCycle = routineCycle;
        this.deleteYn = deleteYn;
    }

    public Routine toEntity(){
        return Routine.builder()
                .routineStart(routineStart)
                .routineEnd(routineEnd)
                .routineCycle(routineCycle)
                .deleteYn(deleteYn)
                .build();
    }
}
