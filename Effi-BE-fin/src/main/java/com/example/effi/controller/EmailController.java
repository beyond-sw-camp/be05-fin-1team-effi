package com.example.effi.controller;

import com.example.effi.service.EmailService;
import com.example.effi.service.EmployeeService;
import com.example.effi.service.GroupService;
import com.example.effi.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService mailService;
    private final EmployeeService employeeService;
    private final GroupService groupService;
    private final ParticipantService participantService;

    // 그룹 추가 시 메일 전송
    @PostMapping("/send/group/add/{groupId}")
    public ResponseEntity<?> groupAddMail(@PathVariable("groupId") Long groupId) {
        groupService.findEmployeeIdsByGroupId(groupId)
                        .forEach(emp -> mailService.addGroupMail(employeeService.findById(emp).getEmail(), groupId));
        return ResponseEntity.ok().build();
    }

    // 그룹 편집 시 메일 전송
    @PostMapping("/send/group/update/{groupId}")
    public ResponseEntity<?> groupUpdateMail(@PathVariable("groupId") Long groupId) {
        groupService.findEmployeeIdsByGroupId(groupId)
                .forEach(emp -> mailService.updateGroupMail(employeeService.findById(emp).getEmail(), groupId));
        return ResponseEntity.ok().build();
    }

    // 전체 메일 전송
    @PostMapping("/send/all/{scheduleId}")
    public ResponseEntity<?> allMail(@PathVariable("scheduleId") Long scheduleId) {
        employeeService.findAll()
                .forEach(employeeDTO -> mailService.allEmployeesMail(employeeDTO.getEmail(), scheduleId));
        return ResponseEntity.ok().build();
    }

    // 부서 메일 전송
    @PostMapping("/send/dept/{deptId}/{scheduleId}")
    public ResponseEntity<?> deptMail(@PathVariable("deptId") Long deptId,
                                      @PathVariable("scheduleId") Long scheduleId) {
        employeeService.findAllByDeptId(deptId)
                .forEach(employeeDTO -> mailService.deptEmplyeesMail(employeeDTO.getEmail(), scheduleId));
        return ResponseEntity.ok().build();
    }

    // 그룹 메일 전송
    @PostMapping("/send/group/{groupId}/{scheduleId}")
    public ResponseEntity<?> groupMail(@PathVariable("groupId") Long groupId,
                                       @PathVariable("scheduleId") Long scheduleId) {
        groupService.findEmployeeIdsByGroupId(groupId)
                .forEach(empId -> mailService.groupEmplyesMail(employeeService.findById(empId).getEmail(), scheduleId));
        return ResponseEntity.ok().build();
    }

    //일정 알림 메일 -> notifyYN 상관 X
    @PostMapping("/send/{scheduleId}")
    public ResponseEntity<?> scheduleMail(@PathVariable("scheduleId") Long scheduleId) {
        participantService.findAllByScheduleId(scheduleId)
                        .forEach(participantResponseDTO
                                -> mailService.scheduleNotifyMail(employeeService.
                                findById(participantResponseDTO.getEmpId()).getEmail(), scheduleId));
        return ResponseEntity.ok().build();
    }

}