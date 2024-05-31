package com.example.effi.domain.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.example.effi.domain.Entity.Routine;

@Getter
@NoArgsConstructor
public class RoutineResponseDTO {
    private Long routineId;
    private Date routineStart;
    private Date routineEnd;
    private String routineCycle;
    private Boolean deleteYn;

    public RoutineResponseDTO(Routine routine) {
        this.routineId = routine.getRoutineId();
        this.routineStart = routine.getRoutineStart();
        this.routineEnd = routine.getRoutineEnd();
        this.routineCycle = routine.getRoutineCycle();
        this.deleteYn = routine.getDeleteYn();
    }
}
