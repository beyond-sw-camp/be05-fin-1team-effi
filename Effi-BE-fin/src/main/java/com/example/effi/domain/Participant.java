package com.example.effi.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "participant")
public class Participant {
    @Id
    @Column(name = "participant_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    // emp
    @Column(name = "emp_id")
    private Long empId;

    // schedule
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Builder
    public Participant(Long empId, Long scheduleId) {
        this.empId = empId;
        this.scheduleId = scheduleId;
    }
}
