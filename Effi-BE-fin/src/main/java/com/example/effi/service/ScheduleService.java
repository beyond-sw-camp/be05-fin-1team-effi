package com.example.effi.service;

import com.example.effi.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    // long id가 들어갔을때 scheduleid를 리턴
    public Long getScheduleId() {

    }

}
