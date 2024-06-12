package com.example.effi.service;

import com.example.effi.domain.DTO.*;
import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Participant;
import com.example.effi.domain.Entity.Routine;
import com.example.effi.domain.Entity.Schedule;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.ParticipantRepository;
import com.example.effi.repository.RoutineRepository;
import com.example.effi.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final CategoryRepository categoryRepository;
    private final RoutineRepository routineRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantService participantService;
    private final EmployeeService employeeService;

    //scheduleId로 schedule 조회
    public ScheduleResponseDTO getSchedule(Long scheduleId) {
        Schedule sch = scheduleRepository.findById(scheduleId).orElse(null);
        if (sch == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found");
        }
        if (sch.getDeleteYn() == false)
            return new ScheduleResponseDTO(sch);
        else
            return null;
    }

    //empId로 내 schedule만 조회
    public List<ScheduleResponseDTO> getAllSchedules() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName());

        Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);
        List<Participant> partiDTO = participantRepository.findAllByEmployee_Id(empId);
        List<ScheduleResponseDTO> schedules = new ArrayList<>();
        for (Participant p : partiDTO) {
            if (p.getSchedule().getDeleteYn() == false)
                schedules.add(new ScheduleResponseDTO(p.getSchedule()));
        }
        return schedules;
    }

    // empId & categoryId로 조회 - 기존 categoryId
    public List<ScheduleResponseDTO> getSchedulesByCategory(Long categoryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName());
        Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);

        List<Schedule> lst = scheduleRepository.findAllByCategory_CategoryId(categoryId);
        List<ScheduleResponseDTO> res = new ArrayList<>();
        for (Schedule sch : lst) {
            Participant dto = participantRepository.findByEmployee_IdAndSchedule_ScheduleId(empId, sch.getScheduleId());
            if (dto != null && dto.getDeleteYn() == false)
                res.add(new ScheduleResponseDTO(sch));
        }
        return res;
    }

    // categoryNo로 조회 (category pk)
    public List<ScheduleResponseDTO> getSchedulesByCategoryNo(Long categoryNo) {
        List<Schedule> allByCategoryCategoryNo = scheduleRepository.findAllByCategory_CategoryNo(categoryNo);
        List<ScheduleResponseDTO> res = new ArrayList<>();
        for (Schedule sch : allByCategoryCategoryNo) {
            res.add(new ScheduleResponseDTO(sch));
        }
        return res;
    }

    // add schedule
    public ScheduleResponseDTO addSchedule(ScheduleRequestDTO scheduleRequestDTO) {
        Category category = categoryRepository.findById(scheduleRequestDTO.getCategoryNo()).orElse(null);
        if (scheduleRequestDTO.getRoutineId() != null){
            Routine routine = routineRepository.findById(scheduleRequestDTO.getRoutineId()).orElse(null);
            return new ScheduleResponseDTO(scheduleRepository.save(scheduleRequestDTO.toEntity(category, routine)));
        }
        Schedule entity = scheduleRequestDTO.toEntity(category, null);
        return new ScheduleResponseDTO(scheduleRepository.save(entity));
    }

    // 루틴 스케줄 자동 추가
    public List<ScheduleResponseDTO> addRoutineSchedule(ScheduleResponseDTO scheduleResponseDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName());
        Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);

        Category category = categoryRepository.findById(scheduleResponseDTO.getCategoryNo()).orElse(null);
        Schedule schedule = scheduleRepository.findById(scheduleResponseDTO.getScheduleId()).orElse(null);
        Routine routine = null;

        if (scheduleResponseDTO.getRoutineId() != null) {
            routine = routineRepository.findById(scheduleResponseDTO.getRoutineId()).orElse(null);
        }

        if (routine == null)
            return null;

        // 일정 시작 및 종료 날짜 가져오기
        Date scheduleStartDate = schedule.getStartTime();
        Date scheduleEndDate = schedule.getEndTime();
        String routineCycle = routine.getRoutineCycle();

        LocalDate routineEndDate =  routine.getRoutineEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // 일정 시작 날짜와 종료 날짜를 LocalDate로 변환
        LocalDate startDate = scheduleStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = scheduleEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        List<Schedule> schedules = new ArrayList<>();

        // 루틴 사이클에 따라 스케줄 생성
        LocalDate currentDate = startDate;
        LocalDate nextDate = endDate;
        while (!currentDate.isAfter(routineEndDate)) {
            // 루틴 사이클에 따라 현재 날짜 증가
            switch (routineCycle) {
                case "daily":
                    currentDate = currentDate.plusDays(1);
                    nextDate = nextDate.plusDays(1);
                    break;
                case "weekly":
                    currentDate = currentDate.plusWeeks(1);
                    nextDate = nextDate.plusWeeks(1);
                    break;
                case "monthly":
                    currentDate = currentDate.plusMonths(1);
                    nextDate = nextDate.plusMonths(1);
                    break;
                case "yearly":
                    currentDate = currentDate.plusYears(1);
                    nextDate = nextDate.plusYears(1);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid routine cycle: " + routineCycle);
            }

            if (currentDate.isAfter(routineEndDate))
                break;

            ScheduleRequestDTO newScheduleRequest = new ScheduleRequestDTO();
            newScheduleRequest.setTitle(scheduleResponseDTO.getTitle());
            newScheduleRequest.setContext(scheduleResponseDTO.getContext());
            newScheduleRequest.setStartTime(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            newScheduleRequest.setEndTime(Date.from(nextDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            newScheduleRequest.setStatus(scheduleResponseDTO.getStatus());
            newScheduleRequest.setNotificationYn(scheduleResponseDTO.getNotificationYn());
            newScheduleRequest.setDeleteYn(scheduleResponseDTO.getDeleteYn());
            newScheduleRequest.setCreatedAt(scheduleResponseDTO.getCreatedAt());
            newScheduleRequest.setUpdatedAt(scheduleResponseDTO.getUpdatedAt());
            newScheduleRequest.setCategoryNo(scheduleResponseDTO.getCategoryNo());
            newScheduleRequest.setRoutineId(scheduleResponseDTO.getRoutineId());

            Schedule scheduleTmp = newScheduleRequest.toEntity(category, routine);
            schedules.add(scheduleTmp);
        }

        scheduleRepository.saveAll(schedules);

        List<ScheduleResponseDTO> res = new ArrayList<>();
        for (Schedule sch : schedules) {
            participantService.addParticipant(sch.getScheduleId(), empId);
            res.add(new ScheduleResponseDTO(sch));
        }
        return res;
    }

    // update schedule
    public ScheduleResponseDTO updateSchedule(ScheduleRequestDTO scheduleRequestDTO, Long scheduleId) {
        Schedule sch = scheduleRepository.findById(scheduleId).orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        Category category = categoryRepository.findById(scheduleRequestDTO.getCategoryNo()).get();
        if (scheduleRequestDTO.getRoutineId() != null) {
            Routine routine = routineRepository.findById(scheduleRequestDTO.getRoutineId()).orElse(null);
            sch.update(scheduleRequestDTO.getTitle(), scheduleRequestDTO.getContext(), scheduleRequestDTO.getStartTime(),
                    scheduleRequestDTO.getEndTime(), scheduleRequestDTO.getStatus(), scheduleRequestDTO.getNotificationYn(),
                    category, routine);
        }
        else
            sch.update(scheduleRequestDTO.getTitle(), scheduleRequestDTO.getContext(), scheduleRequestDTO.getStartTime(),
                    scheduleRequestDTO.getEndTime(), scheduleRequestDTO.getStatus(), scheduleRequestDTO.getNotificationYn(),
                    category, null);
        return new ScheduleResponseDTO(scheduleRepository.save(sch));
    }

    //update routine
    public ScheduleResponseDTO updateRoutine(Long routineId, Long scheduleId) {
        Schedule sch = scheduleRepository.findById(scheduleId).orElse(null);
        if (sch == null) {
            throw new IllegalArgumentException("Schedule not found with id: " + scheduleId);
        }
        Routine routine = routineRepository.findById(routineId).orElse(null);
        if (routine == null || routine.getDeleteYn() == true) {
            sch.update(sch.getTitle(), sch.getContext(), sch.getStartTime(),
                    sch.getEndTime(), sch.getStatus(), sch.getNotificationYn(),
                    sch.getCategory(), null);
            return new ScheduleResponseDTO(scheduleRepository.save(sch));
        }
        sch.update(sch.getTitle(), sch.getContext(), sch.getStartTime(),
                sch.getEndTime(), sch.getStatus(), sch.getNotificationYn(),
                sch.getCategory(), routine);
        return new ScheduleResponseDTO(scheduleRepository.save(sch));
    }

    // delete schedule
    public void deleteSchedule(Long scheduleId) {
        Schedule sch = scheduleRepository.findById(scheduleId).orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        if (sch == null || sch.getDeleteYn() == true) {
            throw new IllegalArgumentException("Schedule not found with ID: " + scheduleId);
        }
        sch.delete();
        scheduleRepository.save(sch);
    }

}
