package com.example.effi.controller;

import com.example.effi.domain.DTO.*;
import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Schedule;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.ScheduleRepository;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class RoutineControllerTest {

    @Autowired
    private RoutineService routineService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

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

    @DisplayName("Find Routine by ID - Failure")
    @Test
    public void testFindByRoutineIdFailure() {
        Long nonExistingRoutineId = -1L;

        // Then
        assertThatThrownBy(() -> routineService.findRoutineById(nonExistingRoutineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("루틴 ID가 유효하지 않습니다.");
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

    @DisplayName("Add Routine and Update Schedule - error")
    @Test
    public void testAddRoutineAndUpdateSchedule() {
        SecurityContextHolder.getContext().getAuthentication();
        categoryRepository.save(Category.builder()
                .categoryId(1L).categoryName("hi").build());
        employeeRepository.save(Employee.builder()
                .id(1L).empNo(1L).password("password1").company("Example Company").name("John Doe")
                .email("john@example.com").phoneNum("123-456-7890").extensionNum("123").rank("Manager").build());
        ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
        scheduleRequest.setTitle("bye");scheduleRequest.setContext("hello");
        scheduleRequest.setStatus(0);scheduleRequest.setNotificationYn(false);
        scheduleRequest.setDeleteYn(false);scheduleRequest.setCreatedAt(new Date());
        scheduleRequest.setUpdatedAt(null);scheduleRequest.setStartTime(new Date());
        scheduleRequest.setEndTime(new Date());scheduleRequest.setRoutineId(null);
        scheduleRequest.setCategoryNo(1L);

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

    @DisplayName("Add Routine and Update Schedule - Invalid Schedule")
    @Test
    public void testAddRoutineAndUpdateScheduleInvalidSchedule() {
        // Given
        // 잘못된 scheduleId 지정
        Long scheduleId = 999L;

        // When
        RoutineRequestDTO routineRequest = new RoutineRequestDTO();
        routineRequest.setRoutineCycle("weekly");
        routineRequest.setRoutineStart(new Date());
        routineRequest.setRoutineEnd(new Date());
        Long routineId = routineService.addRoutine(routineRequest);

        // Attempt to update with invalid scheduleId
        assertThrows(IllegalArgumentException.class, () -> {
            scheduleService.updateRoutine(routineId, scheduleId);
        });

        // Then
        // Assert that routine is not updated
        List<Schedule> all = scheduleRepository.findAll();
        List<ScheduleResponseDTO> result = new ArrayList<>();
        for (Schedule s : all){
            if (s.getRoutine().getRoutineId() == routineId)
                result.add(new ScheduleResponseDTO(s));
        }

        assertThat(result).size().isEqualTo(0);
    }

    @DisplayName("Update Routine")
    @Test
    public void testUpdateRoutine() {
        RoutineRequestDTO routineRequest = new RoutineRequestDTO();
        routineRequest.setRoutineCycle("weekly");
        routineRequest.setRoutineStart(new Date());
        routineRequest.setRoutineEnd(new Date());

        Long routineId = routineService.addRoutine(routineRequest);

        RoutineRequestDTO updateRoutine = new RoutineRequestDTO();
        updateRoutine.setRoutineCycle("monthly");
        updateRoutine.setRoutineStart(new Date());
        updateRoutine.setRoutineEnd(new Date());

        RoutineResponseDTO result = routineService.updateRoutine(routineId, updateRoutine);

        assertThat(result).isNotNull();
        assertThat(result.getRoutineCycle()).isEqualTo("monthly");
    }

    @DisplayName("Update Non-Existing Routine - Failure")
    @Test
    public void testUpdateNonExistingRoutine() {
        // When
        Long nonExistingRoutineId = 9999L;
        RoutineRequestDTO updateRoutine = new RoutineRequestDTO();
        updateRoutine.setRoutineCycle("monthly");
        updateRoutine.setRoutineStart(new Date());
        updateRoutine.setRoutineEnd(new Date());

        // Then
        assertThatThrownBy(() -> routineService.updateRoutine(nonExistingRoutineId, updateRoutine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("루틴 ID가 유효하지 않습니다.");
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

        RoutineResponseDTO result = routineService.findRoutineById(routineId);

        // Then
        assertThat(result.getDeleteYn()).isEqualTo(true);
    }

    @DisplayName("Delete Routine - Invalid Routine id")
    @Test
    public void testDeleteRoutineInvalidRoutineId() {
        Long invalidRoutineId = 999L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            routineService.deleteRoutine(invalidRoutineId);
        });

        assertThat(exception.getMessage()).isEqualTo("루틴 ID가 유효하지 않습니다.");
    }

}