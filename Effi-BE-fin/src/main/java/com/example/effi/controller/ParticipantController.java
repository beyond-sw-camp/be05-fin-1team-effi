package com.example.effi.controller;

import com.example.effi.domain.DTO.ParticipantResponseDTO;
import com.example.effi.repository.GroupRepository;
import com.example.effi.service.EmployeeService;
import com.example.effi.service.GroupService;
import com.example.effi.service.ParticipantService;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/participant")
@AllArgsConstructor
public class ParticipantController {
    private final ParticipantService participantService;
    private final EmployeeService employeeService;
    private final GroupService groupService;

    // 추가 (scheduleId & empId)
    @PostMapping("/add")
    public ResponseEntity<?> addParticipant(Long scheduleId, Long empId) {
        try {
            ParticipantResponseDTO rtn = participantService.addParticipant(scheduleId, empId);
            return ResponseEntity.ok(rtn);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add participant: " + e.getMessage());
        }
    }


    // 추가 -> 부서에 있는 사람들
    @PostMapping("/add/dept/{deptId}")
    public ResponseEntity<?> addParticipantDept(@PathVariable("deptId") Long deptId, Long scheduleId) {
        try {
            List<Long> lst = employeeService.findEmpIdByDept(deptId);
            System.out.println("lst >>>>>>>>>>>>>>> "+lst);
            List<ParticipantResponseDTO> rtn = new ArrayList<>();
            for (Long empId : lst) {
                rtn.add(participantService.addParticipant(scheduleId, empId));
            }
            System.out.println("rtn >>>>>>>>>>>>>>> "+rtn);
            return ResponseEntity.ok(rtn);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to add participant: " + e.getMessage());
        }
    }

    // 추가 -> 그룹에 잇는 사람들
    @PostMapping("/add/group/{groupId}")
    public ResponseEntity<?> addParticipantGroup(@PathVariable("groupId") Long groupId, Long scheduleId) {
        try {
            List<Long> lst = groupService.findEmployeeIdsByGroupId(groupId);
            List<ParticipantResponseDTO> rtn = new ArrayList<>();
            for (Long empId : lst) {
                rtn.add(participantService.addParticipant(scheduleId, empId));
            }
            return ResponseEntity.ok(rtn);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add participant: " + e.getMessage());
        }
    }


    // 조회 participantId
    @GetMapping("/find/participant/{participantId}")
    public ResponseEntity<?> findByParticipantId(@PathVariable("participantId") Long participantId) {
        try {
            ParticipantResponseDTO responseDTO = participantService.findByParticipantId(participantId);
            if (responseDTO == null || responseDTO.getDeleteYn() == true) {
                return ResponseEntity.ok(null);
            }
            return ResponseEntity.ok(responseDTO);
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Failed to find participant: " + e.getMessage());
        }
    }

    // 조회 scheduleId
    @GetMapping("/find/schedule/{scheduleId}")
    public ResponseEntity<?> findByScheduleId(@PathVariable("scheduleId") Long scheduleId) {
        try {
            List<ParticipantResponseDTO> lst = participantService.findAllByScheduleId(scheduleId);
            List<ParticipantResponseDTO> rtn = new ArrayList<>();
            for (ParticipantResponseDTO responseDTO : lst) {
                if (responseDTO.getDeleteYn() == false) {
                    rtn.add(responseDTO);
                }
            }
            return ResponseEntity.ok(rtn);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Failed to find participant: " + e.getMessage());
        }
    }

    // 조회 empId
    @GetMapping("/find/emp/{empId}")
    public ResponseEntity<?> findByEmpId(@PathVariable("empId") Long empId) {
        try {
            List<ParticipantResponseDTO> lst = participantService.findAllByEmpId(empId);
            List<ParticipantResponseDTO> rtn = new ArrayList<>();
            for (ParticipantResponseDTO responseDTO : lst) {
                if (responseDTO.getDeleteYn() == false) {
                    rtn.add(responseDTO);
                }
            }
            return ResponseEntity.ok(rtn);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Failed to find participant: " + e.getMessage());
        }
    }

    // 삭제
    @PutMapping("/delete/{participantId}")
    public ResponseEntity<?> delete(@PathVariable("participantId") Long participantId) {
        try {
            participantService.delete(participantId);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete participant: " + e.getMessage());
        }
    }

}
