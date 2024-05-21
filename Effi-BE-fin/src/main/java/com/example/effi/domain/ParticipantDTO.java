package com.example.effi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParticipantDTO {
    private Long participantId;

    private Long empId;
    private Long scheduleId;

    public ParticipantDTO(Long participantId, Employee emp, Schedule schedule) {
        this.participantId = participantId;
        this.empId = emp.getId();
        this.scheduleId = schedule.getScheduleId();
    }

    public Participant toEntity(Employee emp,Schedule schedule){
        return Participant.builder()
                .employee(emp)
                .schedule(schedule)
                .build();
    }
}
