package com.example.effi.controller;

import com.example.effi.service.EmailService;
import com.example.effi.service.EmployeeService;
import com.example.effi.service.GroupService;
import com.example.effi.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService mailService;
    private final EmployeeService employeeService;
    private final GroupService groupService;
    private final ParticipantService participantService;

    // 그룹 추가 시 메일 전송 Leader
    @PostMapping("/send/group/add/{groupId}")
    public ResponseEntity<?> groupAddMail(@PathVariable("groupId") Long groupId) {
        if (!groupService.findGroupLeader(groupId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("그룹 리더만 메일을 전송할 수 있습니다.");
        groupService.findEmployeeIdsByGroupId(groupId)
                        .forEach(emp -> mailService.addGroupMail(employeeService.findById(emp).getEmail(), groupId));
        return ResponseEntity.ok().build();
    }

    // 그룹 편집 시 메일 전송 (나갔을때 나간 사ㅏ람에게)
    @PostMapping("/send/group/update/{groupId}")
    public ResponseEntity<?> groupUpdateMail(@PathVariable("groupId") Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long empId = Long.valueOf(authentication.getName());

        mailService.updateGroupMail(employeeService.findById(empId).getEmail(), groupId);
        return ResponseEntity.ok().build();
    }

    // 전체 메일 전송
    @PreAuthorize("hasAuthority('사원')")
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

}