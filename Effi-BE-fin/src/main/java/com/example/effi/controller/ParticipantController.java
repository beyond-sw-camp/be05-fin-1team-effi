package com.example.effi.controller;

import com.example.effi.domain.DTO.ParticipantResponseDTO;
import com.example.effi.domain.Entitiy.Participant;
import com.example.effi.service.ParticipantService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participant")
@AllArgsConstructor
public class ParticipantController {
    private final ParticipantService participantService;

    // 추가 (scheduleId & empId)
    @PostMapping("/add")
    public ResponseEntity<ParticipantResponseDTO> addParticipant(Long scheduleId, Long empId) {
        ParticipantResponseDTO rtn = participantService.addParticipant(scheduleId, empId);
        return ResponseEntity.ok(rtn);
    }


    // 조회 participantId
    @GetMapping("/find/participant/{participantId}")
    public ResponseEntity<ParticipantResponseDTO> findByParticipantId(@PathVariable("participantId") Long participantId){
        ParticipantResponseDTO responseDTO = participantService.findByParticipantId();
        return ResponseEntity.ok(responseDTO);
    }

    // 조회 scheduleId
    @GetMapping("/find/schedule/{scheduleId}")
    public ResponseEntity<List<ParticipantResponseDTO>> findByScheduleId(@PathVariable("scheduleId") Long scheduleId) {
        List<ParticipantResponseDTO> lst = participantService.findAllByScheduleId(scheduleId);
        return ResponseEntity.ok(lst);
    }

    // 조회 empId
    @GetMapping("/find/emp/{empId}")
    public ResponseEntity<List<ParticipantResponseDTO>> findByEmpId(@PathVariable("empId") Long empId) {
        List<ParticipantResponseDTO> lst = participantService.findAllByEmpId(empId);
        return ResponseEntity.ok(lst);
    }

    // 수정


    // 삭제
    @PutMapping("/delete/{participantId}")
    public ResponseEntity<?> delete(@PathVariable("participantId") Long participantId) {
        participantService.delete(participantId);
        return ResponseEntity.ok().build();
    }

}
