package com.example.effi.domain;

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
    @Column(name = "routine_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routineId;

    @Column(name = "routine_start")
    private Date routineStart;

    @Column(name = "routine_end")
    private Date routineEnd;

    @Column(name = "routine_cycle")
    private String routineCycle;

    @Column(name = "delete_yn")
    private Boolean deleteYn;

    @Builder
    public Routine(Date routineStart, Date routineEnd, String routineCycle, Boolean deleteYn) {
        this.routineStart = routineStart;
        this.routineEnd = routineEnd;
        this.routineCycle = routineCycle;
        this.deleteYn = deleteYn;
    }
}
