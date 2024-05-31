package com.example.effi.domain.Entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
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

    @Column(name = "delete_yn", nullable = false)
    private Boolean deleteYn;

    @Builder
    public Participant(Employee employee, Schedule schedule, Boolean deleteYn) {
        this.employee = employee;
        this.schedule = schedule;
        this.deleteYn = deleteYn;
    }

    public void delete() {
        this.deleteYn = true;
    }
}
