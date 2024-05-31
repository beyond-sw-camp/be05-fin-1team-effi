package com.example.effi.controller;

import com.example.effi.domain.DTO.*;
import com.example.effi.service.RoutineService;
import com.example.effi.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RoutineControllerTest {

    @Autowired
    private RoutineService routineService;

    @Autowired
    private ScheduleService scheduleService;

    @BeforeEach
    public void setUp() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("1", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @DisplayName("Find Routine by ID")
    @Test
    public void testFindByRoutineId() {
        RoutineRequestDTO routineRequest = new RoutineRequestDTO();
        routineRequest.setRoutineCycle("weekly");
        routineRequest.setRoutineStart(new Date());
        routineRequest.setRoutineEnd(new Date());

        // When
        Long routineId = routineService.addRoutine(routineRequest);

        // When
        RoutineResponseDTO result = routineService.findRoutineById(routineId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRoutineId()).isEqualTo(routineId);
        assertThat(result.getDeleteYn()).isFalse();
        assertThat(result.getRoutineCycle()).isEqualTo("weekly");
    }

    @DisplayName("Add Routine")
    @Test
    public void testAddRoutine() {
        // Given
        RoutineRequestDTO routineRequest = new RoutineRequestDTO();
        routineRequest.setRoutineCycle("weekly");
        routineRequest.setRoutineStart(new Date());
        routineRequest.setRoutineEnd(new Date());

        // When
        Long routineId = routineService.addRoutine(routineRequest);
        RoutineResponseDTO result = routineService.findRoutineById(routineId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRoutineCycle()).isEqualTo("weekly");
        assertThat(result.getRoutineId()).isEqualTo(routineId);
    }

    @DisplayName("Add Routine and Update Schedule")
    @Test
    public void testAddRoutineAndUpdateSchedule() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(1L);
        categoryDTO.setCategoryName("hi");
        Long categoryId = categoryDTO.getCategoryId();

        ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
        scheduleRequest.setTitle("bye");
        scheduleRequest.setContext("hello");
        scheduleRequest.setStatus(0);
        scheduleRequest.setNotificationYn(false);
        scheduleRequest.setDeleteYn(false);
        scheduleRequest.setCreatedAt(new Date());
        scheduleRequest.setUpdatedAt(null);
        scheduleRequest.setStartTime(new Date());
        scheduleRequest.setEndTime(new Date());
        scheduleRequest.setRoutineId(null);
        scheduleRequest.setCategoryId(categoryId);

        ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);
        assertThat(responseDTO).isNotNull();
        Long scheduleId = responseDTO.getScheduleId();

        // Given
        RoutineRequestDTO routineRequest = new RoutineRequestDTO();
        routineRequest.setRoutineCycle("weekly");
        routineRequest.setRoutineStart(new Date());
        routineRequest.setRoutineEnd(new Date());

        // When
        Long routineId = routineService.addRoutine(routineRequest);
        scheduleService.updateRoutine(routineId, scheduleId);
        scheduleService.addRoutineSchedule(scheduleService.getSchedule(scheduleId));
        RoutineResponseDTO result = routineService.findRoutineById(routineId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRoutineCycle()).isEqualTo("weekly");
    }

    @DisplayName("Update Routine")
    @Test
    public void testUpdateRoutine() {
        RoutineRequestDTO routineRequest = new RoutineRequestDTO();
        routineRequest.setRoutineCycle("weekly");
        routineRequest.setRoutineStart(new Date());
        routineRequest.setRoutineEnd(new Date());

        // When
        Long routineId = routineService.addRoutine(routineRequest);

        RoutineRequestDTO updateRoutine = new RoutineRequestDTO();
        updateRoutine.setRoutineCycle("monthly");
        updateRoutine.setRoutineStart(new Date());
        updateRoutine.setRoutineEnd(new Date());

        // When
        RoutineResponseDTO result = routineService.updateRoutine(routineId, updateRoutine);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRoutineCycle()).isEqualTo("monthly");
    }

    @DisplayName("Delete Routine")
    @Test
    public void testDeleteRoutine() {
        RoutineRequestDTO routineRequest = new RoutineRequestDTO();
        routineRequest.setRoutineCycle("weekly");
        routineRequest.setRoutineStart(new Date());
        routineRequest.setRoutineEnd(new Date());

        // When
        Long routineId = routineService.addRoutine(routineRequest);

        // When
        routineService.deleteRoutine(routineId);
        List<ScheduleResponseDTO> schedules = routineService.findAllByRoutineId(routineId);

        for (ScheduleResponseDTO s : schedules) {
            scheduleService.updateRoutine(null, s.getScheduleId());
        }

        RoutineResponseDTO result = routineService.findRoutineById(routineId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDeleteYn()).isTrue();
    }
}
