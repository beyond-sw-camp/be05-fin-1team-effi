package com.example.effi.domain.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import com.example.effi.domain.Entity.Routine;

@Setter
@Getter
@NoArgsConstructor
public class RoutineRequestDTO {
    private Long routineId;
    private Date routineStart;
    private Date routineEnd;
    private String routineCycle;

    public RoutineRequestDTO(Routine routine) {
        this.routineId = routine.getRoutineId();
        this.routineStart = routine.getRoutineStart();
        this.routineEnd = routine.getRoutineEnd();
        this.routineCycle = routine.getRoutineCycle();
    }

    public Routine toEntity(){
        return Routine.builder()
                .routineStart(routineStart)
                .routineEnd(routineEnd)
                .routineCycle(routineCycle)
                .deleteYn(false)
                .build();
    }
}
