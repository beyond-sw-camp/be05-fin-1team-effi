package com.example.effi.domain.DTO;

import com.example.effi.domain.Entity.Participant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ParticipantResponseDTO {
    private Long participantId;
    private Boolean deleteYn;

    private Long empId;
    private Long scheduleId;

    public ParticipantResponseDTO(Participant participant) {
        this.participantId = participant.getParticipantId();
        this.empId = participant.getEmployee().getId();
        this.scheduleId = participant.getSchedule().getScheduleId();
        this.deleteYn = participant.getDeleteYn();
    }

}