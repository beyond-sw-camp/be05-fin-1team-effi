package com.example.effi.controller;

import com.example.effi.domain.DTO.*;
import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.GroupEmp;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.GroupEmpRepository;
import com.example.effi.service.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.time.*;
import java.time.LocalDate;
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
    private final GroupEmpRepository groupEmpRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // 기타 예외로 인한 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add schedule: " + e.getMessage());
        }
    }

    // 추가 - 부서 2 -> 카테고리 수정 필요
    @PostMapping("/add/dept/{deptId}")
    public ResponseEntity<?> addScheduleDept(@RequestBody ScheduleRequestDTO schedule, @PathVariable Long deptId) {
        System.out.println("deptId = >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + deptId);
        try{
            Category tmp = categoryRepository.findByDept_DeptId(deptId);
            CategoryResponseDTO byDeptId = null;
            System.out.println("byDeptId = >>>>>>>>>>>>>>>>>>>>>>>>" + byDeptId);
            if (tmp == null)
                byDeptId = categoryService.addCategoryByDept(deptId);
            else
                byDeptId = new CategoryResponseDTO(tmp);
            schedule.setCategoryNo(byDeptId.getCategoryNo());

            ScheduleResponseDTO rtn = scheduleService.addSchedule(schedule);
            List<Employee> all = employeeRepository.findAllByDept_DeptId(deptId);
            System.out.println("all = >>>>>>>>>>>>>>>>>>>>>>>>" + all.size());
            for (Employee e : all)
                participantService.addParticipant(rtn.getScheduleId(), e.getId()); // 참여자 tbl에 추가
            if (schedule.getRoutineId() != null)
                scheduleService.addRoutineSchedule(scheduleService.getSchedule(rtn.getScheduleId()));
            System.out.println("result `>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );
            return ResponseEntity.ok(rtn);
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException error >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.out.println("Exception error >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" );
            e.printStackTrace();
            // 기타 예외로 인한 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add schedule: " + e.getMessage());
        }
    }


    // 추가 - 그룹 3 -> 카테고리 수정 필요
    @PostMapping("/add/group/{groupId}")
    public ResponseEntity<?> addScheduleGroup(@RequestBody ScheduleRequestDTO schedule, @PathVariable Long groupId) {
        try{
            // CategoryResponseDTO byGroupId = categoryService.findByGroupId(groupId);
            CategoryResponseDTO byGroupId = categoryService.addCategoryByGroup(groupId);
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
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
        }catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // 기타 예외로 인한 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add schedule: " + e.getMessage());
        }
    }

    // 수정 회사
    @PostMapping("/update/company/{scheduleId}")
    public ResponseEntity<?> updateCompanySchedule(@PathVariable Long scheduleId,
                                            @RequestBody ScheduleRequestDTO schedule) {
        try {
            CategoryResponseDTO category = categoryService.findByCategoryNo(1L);
            ScheduleResponseDTO rtn = scheduleService.updateSchedule(schedule, scheduleId, category);
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

    //수정 - 부서
    @PostMapping("/update/dept/{deptId}/{scheduleId}")
    public ResponseEntity<?> updateDeptSchedule(@PathVariable Long scheduleId, @PathVariable Long deptId,
                                            @RequestBody ScheduleRequestDTO schedule) {
        try {
            CategoryResponseDTO category = categoryService.addCategoryByDept(deptId);
            ScheduleResponseDTO rtn = scheduleService.updateSchedule(schedule, scheduleId, category);
            if (schedule.getRoutineId() != null)
                scheduleService.addRoutineSchedule(scheduleService.getSchedule(rtn.getScheduleId()));
            return ResponseEntity.ok(rtn);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Failed to update schedule: " + e.getMessage());
        }
    }

    //수정 - 그룹
    @PostMapping("/update/group/{groupId}/{scheduleId}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long scheduleId, @PathVariable Long groupId,
                                            @RequestBody ScheduleRequestDTO schedule) {
        try {
            CategoryResponseDTO category = categoryService.addCategoryByGroup(groupId);
            ScheduleResponseDTO rtn = scheduleService.updateSchedule(schedule, scheduleId, category);
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


    // 수정 - 개인 (어떤 수정인지) -> shcedule 내용만
    @PostMapping("/update/{scheduleId}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long scheduleId,
                                                              @RequestBody ScheduleRequestDTO schedule) {
        try {
            CategoryResponseDTO category = categoryService.findByCategoryNo(4L);
            ScheduleResponseDTO rtn = scheduleService.updateSchedule(schedule, scheduleId, category);
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
        try{
            List<ScheduleResponseDTO> scheduleResponseDTOS = scheduleService.deleteGroupSchedule(groupId);
            return ResponseEntity.ok(scheduleResponseDTOS);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete schedule: " + e.getMessage());
        }
    }

    // 직원 id로 직원 정보 조회
    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> findEmpById(@PathVariable("empId") Long empId) {
        try {
            EmployeeDTO employeeDTO = employeeService.findById(empId);
            return ResponseEntity.ok(employeeDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to find employee: " + e.getMessage());
        }
    }

    // 선택한 사람 emp 가지고 와서 schduleList
    @GetMapping("/find/other/{empId}")
    public ResponseEntity<?> findOtherEmployee(@PathVariable("empId") Long empId) {
        List<ParticipantResponseDTO> allByEmpId = participantService.findAllByEmpId(empId);
        List<SearchResponseDTO> scheduleList = new ArrayList<>();
        for (ParticipantResponseDTO participant : allByEmpId) {
            Long scheduleId = participant.getScheduleId();
            SearchResponseDTO schedule = scheduleService.getScheduleForSearch(scheduleId);
            if (schedule != null && schedule.getDeleteYn() == false)
                scheduleList.add(schedule);
        }
        return ResponseEntity.ok(scheduleList);
    }

    @GetMapping("/find/7days")
    public ResponseEntity<?> getScheduleCountForLast7Days() {
        LocalDate today = LocalDate.now();

        List<LocalDate> last7Days = IntStream.rangeClosed(-3, 3)
                .mapToObj(today::plusDays)
                .collect(Collectors.toList());

        // 날짜별 일정 개수를 저장할 맵 생성
        Map<String, Map<Integer, Long>> scheduleCountMap = new LinkedHashMap<>();

        for (LocalDate date : last7Days) {
            // 해당 날짜의 시작과 끝 시간
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            List<ScheduleResponseDTO> schedules = scheduleService.getAllSchedules();

            // 상태별 일정 개수를 저장할 맵
            Map<Integer, Long> statusCountMap = new HashMap<>();
            statusCountMap.put(0, 0L);
            statusCountMap.put(1, 0L);
            statusCountMap.put(2, 0L);

            for (ScheduleResponseDTO schedule : schedules) {
                LocalDateTime startTime = schedule.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                if (!startTime.isBefore(startOfDay) && !startTime.isAfter(endOfDay)) {
                    Integer status = schedule.getStatus();
                    statusCountMap.put(status, statusCountMap.getOrDefault(status, 0L) + 1);
                }
            }

            // 결과 맵에 추가
            scheduleCountMap.put(date.toString(), statusCountMap);
        }

        return ResponseEntity.ok(scheduleCountMap);
    }

    // 내 스케줄 전체 (전체 리스트를 위해 tag, category 포함)
    @GetMapping("/findAllForSearch")
    public ResponseEntity<List<SearchResponseDTO>> findAllForSearch(){
        List<SearchResponseDTO> lst = scheduleService.getAllSchedulesForSearch();
        return ResponseEntity.ok(lst);
    }





}
