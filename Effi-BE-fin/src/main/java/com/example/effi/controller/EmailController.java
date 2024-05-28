package com.example.effi.controller;

import com.example.effi.service.EmailService;
import com.example.effi.service.EmployeeService;
import com.example.effi.service.GroupService;
import com.example.effi.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService mailService;
    private final EmployeeService employeeService;
    private final GroupService groupService;
    private final ParticipantService participantService;

    // 메일 전송 컨트롤러 -> 함수 수정하면 될듯
    // 그룹 추가,
    // 편집시 그룹 구성원들에게 메일 전송 기능 구현
    // 회사,
    // 팀,
    // 그룹의 일정이 추가 시 속한 사원들에게 메일 전송 기능 구현
    // 일정에 대한 미리 알림 메일 전송 기능 구현

    // 그룹 추가 시 메일 전송
    @PostMapping("/send/group/add/{groupId}")
    public ResponseEntity<?> groupAddMail(@PathVariable("groupId") Long groupId) {
        mailService.setEmail("a");
        return ResponseEntity.ok().build();
    }

    // 그룹 편집 시 메일 전송
    @PostMapping("/send/group/update/{groupId}")
    public ResponseEntity<?> groupUpdateMail(@PathVariable("groupId") Long groupId) {
        mailService.setEmail("a");
        return ResponseEntity.ok().build();
    }

    // 전체 메일 전송
    @PostMapping("/send/all")
    public ResponseEntity<?> allMail() {
        employeeService.findAll()
                .forEach(employeeDTO -> mailService.setEmail(employeeDTO.getEmail()));
        return ResponseEntity.ok().build();
    }

    // 부서 메일 전송
    @PostMapping("/send/dept/{deptId}")
    public ResponseEntity<?> deptMail(@PathVariable("deptId") Long deptId) {
        employeeService.findAllByDeptId(deptId)
                .forEach(employeeDTO -> mailService.setEmail(employeeDTO.getEmail()));
        return ResponseEntity.ok().build();
    }

    // 그룹 메일 전송
    @PostMapping("/send/group/{groupId}")
    public ResponseEntity<?> groupMail(@PathVariable("groupId") Long groupId) {
        groupService.findEmployeeIdsByGroupId(groupId)
                .forEach(empId -> mailService.setEmail(employeeService.findById(empId).getEmail()));
        return ResponseEntity.ok().build();
    }

    //일정 알림 메일 -> notifyYN 상관 X
    @PostMapping("/send/{scheduleId}")
    public ResponseEntity<?> scheduleMail(@PathVariable("scheduleId") Long scheduleId) {
        participantService.findAllByScheduleId(scheduleId)
                        .forEach(participantResponseDTO
                                -> mailService.setEmail(employeeService.findById(participantResponseDTO.getEmpId()).getEmail()));
        return ResponseEntity.ok().build();
    }

}