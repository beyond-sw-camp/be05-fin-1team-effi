package com.example.effi.domain.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "routine")
public class Routine {
    @Id
    @Column(name = "routine_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routineId;

    @Column(name = "routine_start", nullable = false)
    private Date routineStart;

    @Column(name = "routine_end", nullable = false)
    private Date routineEnd;

    @Column(name = "routine_cycle", nullable = false)
    private String routineCycle;

    @Column(name = "delete_yn", nullable = false)
    private Boolean deleteYn;

    @Builder
    public Routine(Date routineStart, Date routineEnd, String routineCycle, Boolean deleteYn) {
        this.routineStart = routineStart;
        this.routineEnd = routineEnd;
        this.routineCycle = routineCycle;
        this.deleteYn = deleteYn;
    }

    public void delete(){
        this.deleteYn = true;
    }

    public void update(Date routineStart, Date routineEnd, String routineCycle){
        this.routineStart = routineStart;
        this.routineEnd = routineEnd;
        this.routineCycle = routineCycle;
    }
}
