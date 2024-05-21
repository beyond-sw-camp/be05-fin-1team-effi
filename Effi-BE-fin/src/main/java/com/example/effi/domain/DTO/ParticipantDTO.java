package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.Participant;
import com.example.effi.domain.Entitiy.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

    public Participant toEntity(Employee emp, Schedule schedule){
        return Participant.builder()
                .employee(emp)
                .schedule(schedule)
                .build();
    }
}