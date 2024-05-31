package com.example.effi.service;

import com.example.effi.domain.Dto.Schedule.CategoryDTO;
import com.example.effi.domain.Dto.Schedule.ScheduleRequestDTO;
import com.example.effi.domain.Dto.Schedule.ScheduleResponseDTO;
import com.example.effi.domain.Entity.Category;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class TestServiceTest {

    @Autowired
    ScheduleService scheduleService;

    @DisplayName("스케줄을 저장하고 id로 찾는다.")
    @Test
    public void saveAndFind() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(1L);
        categoryDTO.setCategoryName("hi");

        ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();

        scheduleRequest.setTitle("bye");
        scheduleRequest.setContext("hello");
        scheduleRequest.setStatus(0);
        scheduleRequest.setNotificationYn(false);
        scheduleRequest.setDeleteYn(false);
        scheduleRequest.setCreatedAt(new Date());
        scheduleRequest.setUpdatedAt(new Date());
        scheduleRequest.setStartTime(new Date());
        scheduleRequest.setEndTime(new Date());
        scheduleRequest.setRoutineId(null);
        scheduleRequest.setCategoryId(1L);

        ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);
        Long scheduleId = responseDTO.getScheduleId();

        ScheduleResponseDTO findSchedule = scheduleService.getSchedule(scheduleId);
        assertThat(findSchedule).isNotNull();
        assertThat(findSchedule.getScheduleId()).isEqualTo(scheduleId);
        assertThat(findSchedule.getContext()).isEqualTo("hello");
    }

}
