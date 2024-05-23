package com.example.effi.service;

import com.example.effi.domain.DTO.*;
import com.example.effi.domain.Entitiy.Category;
import com.example.effi.domain.Entitiy.Participant;
import com.example.effi.domain.Entitiy.Routine;
import com.example.effi.domain.Entitiy.Schedule;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.ParticipantRepository;
import com.example.effi.repository.RoutineRepository;
import com.example.effi.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final CategoryRepository categoryRepository;
    private final RoutineRepository routineRepository;
    private final ParticipantRepository participantRepository;

    //scheduleId로 schedule 조회
    public ScheduleResponseDTO getSchedule(Long scheduleId) {
        Schedule sch = scheduleRepository.findById(scheduleId).orElse(null);
        if (sch == null) {
            return null;
        }
        return new ScheduleResponseDTO(sch);
    }

    //empId로 내 schedule만 조회
    public List<ScheduleResponseDTO> getAllSchedules(Long empId) {
       //empID로 participant에서 list 받아와서 scheduleId List로 받기
        List<Participant> partiDTO = participantRepository.findAllByEmployee_Id(empId);
        List<ScheduleResponseDTO> schedules = new ArrayList<>();
        for (Participant p : partiDTO) {
            schedules.add(new ScheduleResponseDTO(p.getSchedule()));
        }
        return schedules;
    }

    // empId & categoryId로 조회
    public List<ScheduleResponseDTO> getSchedulesByCategory(Long categoryId, Long empId) {
        List<Schedule> lst = scheduleRepository.findAllByCategory_CategoryId(categoryId);
        List<ScheduleResponseDTO> res = new ArrayList<>();
        for (Schedule sch : lst) {
            Participant dto = participantRepository.findByEmployee_IdAndSchedule_ScheduleId(empId, sch.getScheduleId());
            if (dto == null) {
                res.add(new ScheduleResponseDTO(sch));
            }
        }
        return res;
    }

    // add schedule
    public ScheduleResponseDTO addSchedule(ScheduleRequestDTO scheduleRequestDTO) {
        Category category = categoryRepository.findById(scheduleRequestDTO.getCategoryId()).get();
        Routine routine = routineRepository.findById(scheduleRequestDTO.getRoutineId()).get();
        return new ScheduleResponseDTO(scheduleRepository.save(scheduleRequestDTO.toEntity(category, routine)));
    }

    // update schedule
    public ScheduleResponseDTO updateSchedule(ScheduleRequestDTO scheduleRequestDTO) {
        Schedule sch = scheduleRepository.findById(scheduleRequestDTO.getScheduleId()).get();
        Category category = categoryRepository.findById(scheduleRequestDTO.getCategoryId()).get();
        Routine routine = routineRepository.findById(scheduleRequestDTO.getRoutineId()).get();
        sch.update(scheduleRequestDTO.getTitle(), scheduleRequestDTO.getContext(), scheduleRequestDTO.getStartTime(),
                scheduleRequestDTO.getEndTime(), scheduleRequestDTO.getStatus(), scheduleRequestDTO.getNotificationYn(),
                category, routine);
        return new ScheduleResponseDTO(scheduleRepository.save(sch));
    }

    // delete schedule
    public void deleteSchedule(Long scheduleId) {
        Schedule sch = scheduleRepository.findById(scheduleId).orElse(null);
        if (sch == null)
            return;
        sch.delete();
        scheduleRepository.save(sch);
    }
}
