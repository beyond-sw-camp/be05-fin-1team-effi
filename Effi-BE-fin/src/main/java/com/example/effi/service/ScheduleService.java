package com.example.effi.service;

import com.example.effi.domain.DTO.*;
import com.example.effi.domain.Entitiy.Category;
import com.example.effi.domain.Entitiy.Routine;
import com.example.effi.domain.Entitiy.Schedule;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.RoutineRepository;
import com.example.effi.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final CategoryRepository categoryRepository;
    private final RoutineRepository routineRepository;

    //scheduleId로 schedule 조회
    public ScheduleResponseDTO getSchedule(Long scheduleId) {
        Schedule sch = scheduleRepository.findById(scheduleId).orElse(null);
        if (sch == null) {
            return null;
        }
        return new ScheduleResponseDTO(sch);
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
