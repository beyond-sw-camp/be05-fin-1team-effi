package com.example.effi.service;

import com.example.effi.domain.DTO.ParticipantResponseDTO;
import com.example.effi.domain.Entity.Participant;
import com.example.effi.domain.Entity.Schedule; 
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.ParticipantRepository;
import com.example.effi.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final EmployeeRepository employeeRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleService scheduleService;

    // add - schedule && user가 있다는 가정
    public ParticipantResponseDTO addParticipant(Long scheduleId, Long empId) {
        if (scheduleId == null || scheduleService.getSchedule(scheduleId) == null)
            throw new IllegalArgumentException("Schedule이 존재하지 않습니다.");
        if (empId == null || employeeRepository.findById(empId) == null)
            throw new IllegalArgumentException("사원 ID가 유효하지 않습니다.");
        Participant check = participantRepository.findByEmployee_IdAndSchedule_ScheduleId(empId, scheduleId);
        if (check != null) {
            if (check.getDeleteYn() == false) {
                throw new RuntimeException("Participant already exists");
            } else {
                check.setDeleteYn(false);
                return new ParticipantResponseDTO(participantRepository.save(check));
            }
        }
        return new ParticipantResponseDTO(participantRepository.save(
                Participant.builder()
                        .employee(employeeRepository.findById(empId).orElse(null))
                        .schedule(scheduleRepository.findByScheduleId(scheduleId))
                        .deleteYn(false)
                        .build()
        ));
    }

    // select (scheduleId로)
    public List<ParticipantResponseDTO> findAllByScheduleId(Long scheduleId) {
        List<Participant> lst = participantRepository.findAllBySchedule_ScheduleId(scheduleId);
        List<ParticipantResponseDTO> lstDto = new ArrayList<>();
        for (Participant participant : lst) {
            lstDto.add(new ParticipantResponseDTO(participant));
        }
        return lstDto;
    }

    // select (empId로)
    public List<ParticipantResponseDTO> findAllByEmpId(Long empId) {
        List<Participant> lst = participantRepository.findAllByEmployee_Id(empId);
        List<ParticipantResponseDTO> lstDto = new ArrayList<>();
        for (Participant participant : lst) {
            lstDto.add(new ParticipantResponseDTO(participant));
        }
        if (lstDto == null)
            throw new NotFoundException("Nothing to show");
        return lstDto;
    }

    // select by particiapantID
    public ParticipantResponseDTO findByParticipantId(Long participantId) {
        Participant parti = participantRepository.findById(participantId).get();
        return new ParticipantResponseDTO(parti);
    }

    // delete
    public Long delete(Long participantId){
        Participant participant = participantRepository.findById(participantId).get();
        participant.delete();
        return participantRepository.save(participant).getParticipantId();
    }

}
