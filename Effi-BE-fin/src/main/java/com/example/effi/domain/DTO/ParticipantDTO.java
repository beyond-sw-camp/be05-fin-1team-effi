package com.example.effi.domain.DTO;

import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Participant;
import com.example.effi.domain.Entity.Schedule;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParticipantDTO {
    private Long participantId;
    private Boolean deleteYn;

    private Long empId;
    private Long scheduleId;

    public ParticipantDTO(Long participantId, Employee emp, Schedule schedule, Boolean deleteYn) {
        this.participantId = participantId;
        this.empId = emp.getId();
        this.scheduleId = schedule.getScheduleId();
        this.deleteYn = deleteYn;
    }

    public Participant toEntity(Employee emp, Schedule schedule){
        return Participant.builder()
                .employee(emp)
                .schedule(schedule)
                .deleteYn(deleteYn)
                .build();
    }
}
