package com.example.effi.controller;

import com.example.effi.domain.DTO.*;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.GroupEmp;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.GroupEmpRepository;
import com.example.effi.service.*;
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
    private final CategoryService categoryService;

    // 추가 - 회사 1
    @PostMapping("/add/company")
    public ResponseEntity<?> addScheduleCompany(@RequestBody ScheduleRequestDTO schedule) {
        try{
            schedule.setCategoryNo(1L);
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

    // 추가 - 부서 2 -> 카테고리 수정 필요
    @PostMapping("/add/dept/{deptId}")
    public ResponseEntity<?> addScheduleDept(@RequestBody ScheduleRequestDTO schedule, @PathVariable Long deptId) {
        try{
            CategoryResponseDTO byDeptId = categoryService.findByDeptId(deptId);
            if (byDeptId == null){
                byDeptId = categoryService.addCategoryByDept(deptId);
            }
            schedule.setCategoryNo(byDeptId.getCategoryNo());

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


    // 추가 - 그룹 3 -> 카테고리 수정 필요
    @PostMapping("/add/group/{groupId}")
    public ResponseEntity<?> addScheduleGroup(@RequestBody ScheduleRequestDTO schedule, @PathVariable Long groupId) {
        try{
            CategoryResponseDTO byGroupId = categoryService.findByGroupId(groupId);
            if (byGroupId == null){
                byGroupId = categoryService.addCategoryByGroup(groupId);
            }
            schedule.setCategoryNo(byGroupId.getCategoryNo());

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

    // 추가 - 개인 4
    @PostMapping("/add")
    public ResponseEntity<?> addSchedule(@RequestBody ScheduleRequestDTO schedule) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long creatorEmpNo = Long.valueOf(authentication.getName());
            System.out.println("creatorEmpNo = " + creatorEmpNo);
            Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);

            schedule.setCategoryNo(4L);
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

    // 내 스케줄 전체
   @GetMapping("/findAll")
    public ResponseEntity<List<ScheduleResponseDTO>> findAll(){
       List<ScheduleResponseDTO> lst = scheduleService.getAllSchedules();
       return ResponseEntity.ok(lst);
    }

    // 조회 카테고리별 -> 기존 categoryId로 조회
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

    // 조회 그룹 별..
    @GetMapping("/find/group/{groupId}")
    public ResponseEntity<?> findByGroup(@PathVariable("groupId") Long groupId) {
        CategoryResponseDTO byGroupId = categoryService.findByGroupId(groupId);
        Long categoryNo = byGroupId.getCategoryNo();
        List<ScheduleResponseDTO> schedulesByCategoryNo = scheduleService.getSchedulesByCategoryNo(categoryNo);
        return ResponseEntity.ok(schedulesByCategoryNo);
    }

    // 조회 부서 별
    @GetMapping("/find/dept/{deptId}")
    public ResponseEntity<?> findByDept(@PathVariable("deptId") Long deptId) {
        CategoryResponseDTO byDeptId = categoryService.findByDeptId(deptId);
        Long categoryNo = byDeptId.getCategoryNo();
        List<ScheduleResponseDTO> schedulesByCategoryNo = scheduleService.getSchedulesByCategoryNo(categoryNo);
        return ResponseEntity.ok(schedulesByCategoryNo);
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

    //그룹 탈퇴시 그룹 스케줄 삭제
    @PutMapping("/delete/groupSchedule/{groupId}")
    public ResponseEntity<?> deleteGroupSchedule(@PathVariable("groupId") Long groupId){
        List<ScheduleResponseDTO> scheduleResponseDTOS = scheduleService.deleteGroupSchedule(groupId);
        return ResponseEntity.ok(scheduleResponseDTOS);
    }

}
