package com.example.effi.controller;

import com.example.effi.domain.DTO.ScheduleRequestDTO;
import com.example.effi.domain.DTO.ScheduleResponseDTO;
import com.example.effi.service.EmployeeService;
import com.example.effi.service.ParticipantService;
import com.example.effi.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@AllArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final EmployeeService employeeService;
    private final ParticipantService participantService;

    // 추가
    @PostMapping("/add")
    public ResponseEntity<ScheduleResponseDTO> addSchedule(@RequestBody ScheduleRequestDTO schedule,
                                                           Long empNo) {
        Long empId = employeeService.findEmpIdByEmpNo(empNo);
        ScheduleResponseDTO rtn = scheduleService.addSchedule(schedule);
        participantService.addParticipant(rtn.getScheduleId(), empId); // 참여자 tbl에 추가
        return ResponseEntity.ok(rtn);
    }

    // 수정 (어떤 수정인지) -> shcedule 내용만
    @PostMapping("/update/{scheduleId}")
    public ResponseEntity<ScheduleResponseDTO> updateSchedule(@PathVariable Long scheduleId,
                                                              @RequestBody ScheduleRequestDTO schedule) {
//        if (schedule.getScheduleId() != scheduleId) {
//            return ResponseEntity.ok(null);
////            return ResponseEntity.badRequest().build(); // 뭔가 이상
//        }
        ScheduleResponseDTO rtn = scheduleService.updateSchedule(schedule, scheduleId);
        return ResponseEntity.ok(rtn);
    }

    // 조회 (전체) -> empNo 어떻게 넣음????
   @GetMapping("/findAll")
    public ResponseEntity<List<ScheduleResponseDTO>> findAll(Long empNo){
       Long empId = employeeService.findEmpIdByEmpNo(empNo);
       List<ScheduleResponseDTO> lst = scheduleService.getAllSchedules(empId);
       return ResponseEntity.ok(lst);
    }

    // 조회 카테고리별 -> empNo 어떻게 넣음????
    @GetMapping("/find/category/{categoryId}")
    public ResponseEntity<List<ScheduleResponseDTO>> findByCategory(@PathVariable("categoryId") Long categoryId,
                                                                    Long empNo) {
        Long empId = employeeService.findEmpIdByEmpNo(empNo);
        List<ScheduleResponseDTO> scheduleResponseDTO = scheduleService.getSchedulesByCategory(categoryId, empId);
        return ResponseEntity.ok(scheduleResponseDTO);
    }

    // 조회 (1개 scheduleId)
    @GetMapping("/find/{scheduleId}")
    public ResponseEntity<ScheduleResponseDTO> findById(@PathVariable("scheduleId") Long scheduleId){
        ScheduleResponseDTO schedule = scheduleService.getSchedule(scheduleId);
        if (schedule != null && schedule.getDeleteYn() == false)
            return ResponseEntity.ok(schedule);
        else
            return ResponseEntity.ok(null);
    }

    // 삭제
    @PutMapping("/delete/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable("scheduleId") Long scheduleId){
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok().build();
    }

    //
}
