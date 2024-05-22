package com.example.effi.domain.Entitiy;

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
    @Column(name = "participant_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    // emp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private Employee employee;

    // schedule
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Builder
    public Participant(Employee employee, Schedule schedule) {
        this.employee = employee;
        this.schedule = schedule;
    }
}
