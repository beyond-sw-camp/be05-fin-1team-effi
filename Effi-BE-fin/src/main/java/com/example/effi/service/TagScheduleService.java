package com.example.effi.service;

import com.example.effi.repository.TagRepository;
import com.example.effi.repository.TagScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class TagScheduleService {
    private final TagScheduleRepository tagScheduleRepository;
    private final TagService tagService;
    private final ScheduleService scheduleService;


}
