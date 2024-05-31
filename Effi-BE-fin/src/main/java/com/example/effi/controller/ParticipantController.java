package com.example.effi.controller;

import com.example.effi.domain.Dto.Schedule.ParticipantResponseDTO;
import com.example.effi.repository.GroupRepository;
import com.example.effi.service.EmployeeService;
import com.example.effi.service.GroupService;
import com.example.effi.service.ParticipantService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/participant")
@AllArgsConstructor
public class ParticipantController {
    private final ParticipantService participantService;
    private final EmployeeService employeeService;
    private final GroupRepository groupRepository;
    private final GroupService groupService;

    // 추가 (scheduleId & empId)
    @PostMapping("/add")
    public ResponseEntity<ParticipantResponseDTO> addParticipant(Long scheduleId, Long empId) {
        ParticipantResponseDTO rtn = participantService.addParticipant(scheduleId, empId);
        return ResponseEntity.ok(rtn);
    }

    // 추가 -> 부서에 있는 사람들
    @PostMapping("/add/dept/{deptId}")
    public ResponseEntity<List<ParticipantResponseDTO>> addParticipantDept(@PathVariable("deptId") Long deptId, Long scheduleId) {
        List<Long> lst = employeeService.findEmpIdByDept(deptId);
        List<ParticipantResponseDTO> rtn = new ArrayList<>();
        for (Long empId : lst) {
             rtn.add(participantService.addParticipant(scheduleId, empId));
        }
        return ResponseEntity.ok(rtn);
    }

    // 추가 -> 그룹에 잇는 사람들
    // 그룹 사용법 문의 후 확인 필요
    @PostMapping("/add/group/{groupId}")
    public ResponseEntity<List<ParticipantResponseDTO>> addParticipantGroup(@PathVariable("groupId") Long groupId, Long scheduleId) {
        List<Long> lst = groupService.findEmployeeIdsByGroupId(groupId);
        List<ParticipantResponseDTO> rtn = new ArrayList<>();
        for (Long empId : lst) {
            rtn.add(participantService.addParticipant(scheduleId, empId));
        }
        return ResponseEntity.ok(rtn);
    }


    // 조회 participantId
    @GetMapping("/find/participant/{participantId}")
    public ResponseEntity<ParticipantResponseDTO> findByParticipantId(@PathVariable("participantId") Long participantId){
        ParticipantResponseDTO responseDTO = participantService.findByParticipantId(participantId);
        if (responseDTO == null || responseDTO.getDeleteYn() == true) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(responseDTO);
    }

    // 조회 scheduleId
    @GetMapping("/find/schedule/{scheduleId}")
    public ResponseEntity<List<ParticipantResponseDTO>> findByScheduleId(@PathVariable("scheduleId") Long scheduleId) {
        List<ParticipantResponseDTO> lst = participantService.findAllByScheduleId(scheduleId);
        List<ParticipantResponseDTO> rtn = new ArrayList<>();
        for (ParticipantResponseDTO responseDTO : lst) {
            if (responseDTO.getDeleteYn() == false) {
                rtn.add(responseDTO);
            }
        }
        return ResponseEntity.ok(rtn);
    }

    // 조회 empId
    @GetMapping("/find/emp/{empId}")
    public ResponseEntity<List<ParticipantResponseDTO>> findByEmpId(@PathVariable("empId") Long empId) {
        List<ParticipantResponseDTO> lst = participantService.findAllByEmpId(empId);
        List<ParticipantResponseDTO> rtn = new ArrayList<>();
        for (ParticipantResponseDTO responseDTO : lst) {
            if (responseDTO.getDeleteYn() == false) {
                rtn.add(responseDTO);
            }
        }
        return ResponseEntity.ok(rtn);
    }

    // 삭제
    @PutMapping("/delete/{participantId}")
    public ResponseEntity<?> delete(@PathVariable("participantId") Long participantId) {
        participantService.delete(participantId);
        return ResponseEntity.ok().build();
    }

}
