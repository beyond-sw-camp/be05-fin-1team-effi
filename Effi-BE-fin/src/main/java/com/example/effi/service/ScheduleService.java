package com.example.effi.service;

import com.example.effi.domain.Entitiy.Schedule;
import com.example.effi.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    //scheduleId로 schedule 생성
    public Schedule getSchedule(Long scheduleId) {
        Schedule sch = scheduleRepository.findById(scheduleId).orElse(null);
        if (sch == null) {
            return null;
        }
        return sch;
    }
}
