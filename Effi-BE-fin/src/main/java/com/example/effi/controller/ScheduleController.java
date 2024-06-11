package com.example.effi.controller;

import com.example.effi.domain.DTO.ScheduleRequestDTO;
import com.example.effi.domain.DTO.ScheduleResponseDTO;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.GroupEmp;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.GroupEmpRepository;
import com.example.effi.service.EmployeeService;
import com.example.effi.service.GroupService;
import com.example.effi.service.ParticipantService;
import com.example.effi.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@AllArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final EmployeeService employeeService;
    private final ParticipantService participantService;
    private final EmployeeRepository employeeRepository;
    private final GroupService groupService;
    private final GroupEmpRepository groupEmpRepository;

    // 추가 - 개인
    @PostMapping("/add")
    public ResponseEntity<?> addSchedule(@RequestBody ScheduleRequestDTO schedule) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long creatorEmpNo = Long.valueOf(authentication.getName());
            System.out.println("creatorEmpNo = " + creatorEmpNo);
            Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);
            ScheduleResponseDTO rtn = scheduleService.addSchedule(schedule);
            participantService.addParticipant(rtn.getScheduleId(), empId); // 참여자 tbl에 추가
            if (schedule.getRoutineId() != null)
                scheduleService.addRoutineSchedule(scheduleService.getSchedule(rtn.getScheduleId()));
            return ResponseEntity.ok(rtn);
        }catch (Exception e) {
            // 기타 예외로 인한 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add schedule: " + e.getMessage());
        }
    }

    // 추가 - 회사
    @PostMapping("/add/company")
    public ResponseEntity<?> addScheduleCompany(@RequestBody ScheduleRequestDTO schedule) {
        try{
            ScheduleResponseDTO rtn = scheduleService.addSchedule(schedule);
            List<Employee> all = employeeRepository.findAll();
            for (Employee e : all)
                participantService.addParticipant(rtn.getScheduleId(), e.getId()); // 참여자 tbl에 추가
            if (schedule.getRoutineId() != null)
                scheduleService.addRoutineSchedule(scheduleService.getSchedule(rtn.getScheduleId()));
            return ResponseEntity.ok(rtn);
        }catch (Exception e) {
            // 기타 예외로 인한 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add schedule: " + e.getMessage());
        }
    }

    // 추가 - 부서
    @PostMapping("/add/dept/{deptId}")
    public ResponseEntity<?> addScheduleDept(@RequestBody ScheduleRequestDTO schedule, @PathVariable Long deptId) {
        try{
            ScheduleResponseDTO rtn = scheduleService.addSchedule(schedule);
            List<Employee> all = employeeRepository.findAllByDept_DeptId(deptId);
            for (Employee e : all)
                participantService.addParticipant(rtn.getScheduleId(), e.getId()); // 참여자 tbl에 추가
            if (schedule.getRoutineId() != null)
                scheduleService.addRoutineSchedule(scheduleService.getSchedule(rtn.getScheduleId()));
            return ResponseEntity.ok(rtn);
        }catch (Exception e) {
            // 기타 예외로 인한 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add schedule: " + e.getMessage());
        }
    }


    // 추가 - 그룹
    @PostMapping("/add/group/{groupId}")
    public ResponseEntity<?> addScheduleGroup(@RequestBody ScheduleRequestDTO schedule, @PathVariable Long groupId) {
        try{
            ScheduleResponseDTO rtn = scheduleService.addSchedule(schedule);
            List<GroupEmp> allByGroupGroupId = groupEmpRepository.findAllByGroup_GroupId(groupId);
            for (GroupEmp e : allByGroupGroupId)
                participantService.addParticipant(rtn.getScheduleId(), e.getEmployee().getId()); // 참여자 tbl에 추가
            if (schedule.getRoutineId() != null)
                scheduleService.addRoutineSchedule(scheduleService.getSchedule(rtn.getScheduleId()));
            return ResponseEntity.ok(rtn);
        }catch (Exception e) {
            // 기타 예외로 인한 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add schedule: " + e.getMessage());
        }
    }

    // 수정 (어떤 수정인지) -> shcedule 내용만
    @PostMapping("/update/{scheduleId}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long scheduleId,
                                                              @RequestBody ScheduleRequestDTO schedule) {
        try {
            ScheduleResponseDTO rtn = scheduleService.updateSchedule(schedule, scheduleId);
            if (schedule.getRoutineId() != null)
                scheduleService.addRoutineSchedule(scheduleService.getSchedule(rtn.getScheduleId()));
            return ResponseEntity.ok(rtn);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Failed to update schedule: " + e.getMessage());
        }
    }

    // 조회 (전체) -> empNo 어떻게 넣음????
   @GetMapping("/findAll")
    public ResponseEntity<List<ScheduleResponseDTO>> findAll(){
       List<ScheduleResponseDTO> lst = scheduleService.getAllSchedules();
       return ResponseEntity.ok(lst);
    }

    // 조회 카테고리별 -> empNo 어떻게 넣음????
    @GetMapping("/find/category/{categoryId}")
    public ResponseEntity<?> findByCategory(@PathVariable("categoryId") Long categoryId) {
        try {
            List<ScheduleResponseDTO> scheduleResponseDTO = scheduleService.getSchedulesByCategory(categoryId);
            return ResponseEntity.ok(scheduleResponseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to find schedule: " + e.getMessage());
        }
    }

    // 조회 (1개 scheduleId)
    @GetMapping("/find/{scheduleId}")
    public ResponseEntity<?> findById(@PathVariable("scheduleId") Long scheduleId){
        try {
            ScheduleResponseDTO schedule = scheduleService.getSchedule(scheduleId);
            if (schedule != null && schedule.getDeleteYn() == false)
                return ResponseEntity.ok(schedule);
            else
                return ResponseEntity.ok(null);

        }catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to find schedule: " + e.getMessage());
        }
    }

    // 삭제
    @PutMapping("/delete/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable("scheduleId") Long scheduleId){
        try{
            scheduleService.deleteSchedule(scheduleId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to delete schedule: " + e.getMessage());
        }
    }

}
