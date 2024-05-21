package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.Routine;

import java.util.Date;

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
