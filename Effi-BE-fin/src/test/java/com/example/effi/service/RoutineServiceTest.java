package com.example.effi.service;

import com.example.effi.domain.DTO.*;
import com.example.effi.domain.Entity.Category;
import com.example.effi.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoutineServiceTest {

    @Autowired
    ScheduleService scheduleService;
    @Autowired
    private RoutineService routineService;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("1", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        categoryRepository.save(Category.builder()
                .categoryId(1L)
                .categoryName("hi").build());
    }

    @DisplayName("routine_id routine 조회")
    @Test
    public void findRoutineByIdTest() {
        Date date = new Date();
        // Date를 LocalDate로 변환
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // 한 달 더하기
        LocalDate newLocalDate = localDate.plusMonths(1);
        // LocalDate를 Date로 변환
        Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        RoutineRequestDTO routineRequestDTO = new RoutineRequestDTO();
        routineRequestDTO.setRoutineStart(date);
        routineRequestDTO.setRoutineEnd(newDate);
        routineRequestDTO.setRoutineCycle("weekly");

        Long routineId = routineService.addRoutine(routineRequestDTO);

        RoutineResponseDTO routineById = routineService.findRoutineById(routineId);
        assertThat(routineById).isNotNull();
        assertThat(routineById.getRoutineId()).isEqualTo(routineId);
    }

    @DisplayName("routine_id로 schedule 조회")
    @Test
    public void findAllByRoutineIdTest() {
        Date date = new Date();
        // Date를 LocalDate로 변환
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // 한 달 더하기
        LocalDate newLocalDate = localDate.plusMonths(1);
        // LocalDate를 Date로 변환
        Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        RoutineRequestDTO routineRequestDTO = new RoutineRequestDTO();
        routineRequestDTO.setRoutineStart(date);
        routineRequestDTO.setRoutineEnd(newDate);
        routineRequestDTO.setRoutineCycle("weekly");

        Long routineId = routineService.addRoutine(routineRequestDTO);

        ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
        scheduleRequest.setTitle("bye");
        scheduleRequest.setContext("hello");
        scheduleRequest.setStatus(0);
        scheduleRequest.setNotificationYn(false);
        scheduleRequest.setDeleteYn(false);
        scheduleRequest.setCreatedAt(date);
        scheduleRequest.setUpdatedAt(null);
        scheduleRequest.setStartTime(date);
        scheduleRequest.setEndTime(date);
        scheduleRequest.setRoutineId(routineId);

        ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);
        assertThat(responseDTO).isNotNull();

        List<ScheduleResponseDTO> allByRoutineId = routineService.findAllByRoutineId(routineId);
        for (ScheduleResponseDTO scheduleResponseDTO : allByRoutineId) {
            assertThat(scheduleResponseDTO).isNotNull();
            assertThat(scheduleResponseDTO.getRoutineId()).isEqualTo(routineId);
        }

    }

    @DisplayName("routine 수정")
    @Test
    public void updateRoutineTest() {
        Date date = new Date();
        // Date를 LocalDate로 변환
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // 한 달 더하기
        LocalDate newLocalDate = localDate.plusMonths(1);
        // LocalDate를 Date로 변환
        Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        RoutineRequestDTO routineRequestDTO = new RoutineRequestDTO();
        routineRequestDTO.setRoutineStart(date);
        routineRequestDTO.setRoutineEnd(newDate);
        routineRequestDTO.setRoutineCycle("weekly");

        Long routineId = routineService.addRoutine(routineRequestDTO);

        RoutineRequestDTO updateDto = new RoutineRequestDTO();
        updateDto.setRoutineStart(date);
        updateDto.setRoutineEnd(newDate);
        updateDto.setRoutineCycle("monthly");

        RoutineResponseDTO updateRoutine = routineService.updateRoutine(routineId, updateDto);
        assertThat(updateRoutine).isNotNull();
        assertThat(updateRoutine.getRoutineId()).isEqualTo(routineId);
        assertThat(updateRoutine.getRoutineCycle()).isEqualTo("monthly");
    }

    @DisplayName("routine 삭제")
    @Test
    public void deleteRoutineTest() {
        Date date = new Date();
        // Date를 LocalDate로 변환
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // 한 달 더하기
        LocalDate newLocalDate = localDate.plusMonths(1);
        // LocalDate를 Date로 변환
        Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        RoutineRequestDTO routineRequestDTO = new RoutineRequestDTO();
        routineRequestDTO.setRoutineStart(date);
        routineRequestDTO.setRoutineEnd(newDate);
        routineRequestDTO.setRoutineCycle("weekly");

        Long routineId = routineService.addRoutine(routineRequestDTO);

        Long updateId = routineService.deleteRoutine(routineId);
        assertThat(updateId).isEqualTo(routineId);
        RoutineResponseDTO routineById = routineService.findRoutineById(routineId);
        RoutineResponseDTO deletedRoutineById = routineService.findRoutineById(updateId);
        assertThat(routineById.getRoutineStart()).isEqualTo(deletedRoutineById.getRoutineStart());
        assertThat(routineById.getRoutineEnd()).isEqualTo(deletedRoutineById.getRoutineEnd());
        assertThat(routineById.getRoutineCycle()).isEqualTo(deletedRoutineById.getRoutineCycle());
        assertThat(deletedRoutineById.getDeleteYn()).isEqualTo(true);
    }
}