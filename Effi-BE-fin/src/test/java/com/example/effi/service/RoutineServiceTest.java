package com.example.effi.service;

import com.example.effi.domain.DTO.*;
import com.example.effi.domain.Entity.Category;
import com.example.effi.repository.CategoryRepository;
import org.junit.jupiter.api.*;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoutineServiceTest {

    /*
    addRoutine
    */

    @Autowired
    ScheduleService scheduleService;
    @Autowired
    private RoutineService routineService;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    public void setUp() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("1", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        categoryRepository.save(Category.builder().categoryId(1L).categoryName("회사").dept(null).group(null).build());
        categoryRepository.save(Category.builder().categoryId(2L).categoryName("부서").dept(null).group(null).build());
        categoryRepository.save(Category.builder().categoryId(3L).categoryName("그룹").dept(null).group(null).build());
        categoryRepository.save(Category.builder().categoryId(4L).categoryName("개인").dept(null).group(null).build());
    }

    @DisplayName("routine 추가 - 성공")
    @Test
    public void addRoutineTest(){
        RoutineRequestDTO routineRequestDTO = new RoutineRequestDTO();
        routineRequestDTO.setRoutineStart(new Date());
        routineRequestDTO.setRoutineEnd(new Date());
        routineRequestDTO.setRoutineCycle("weekly");

        Long l = routineService.addRoutine(routineRequestDTO);
        assertThat(l).isNotNull();

        RoutineResponseDTO routineById = routineService.findRoutineById(l);
        assertThat(routineById).isNotNull();
        assertThat(routineById.getRoutineCycle()).isEqualTo("weekly");
    }

    @DisplayName("routine 추가 - 실패")
    @Test
    public void addRoutineTestFail(){
        RoutineRequestDTO routineRequestDTO = new RoutineRequestDTO();
        routineRequestDTO.setRoutineId(1L);
        routineRequestDTO.setRoutineStart(new Date());
        routineRequestDTO.setRoutineEnd(new Date());
        routineRequestDTO.setRoutineCycle(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            routineService.addRoutine(routineRequestDTO);
        });
        assertEquals("루틴 정보가 유효하지 않습니다.", exception.getMessage());
    }

    @DisplayName("routine_id routine 조회 - 성공")
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

    @DisplayName("routine_id routine 조회 - 실패")
    @Test
    public void findRoutineByIdTestFail() {
        Long routineId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            routineService.findRoutineById(routineId);
        });
        assertEquals("루틴 ID가 유효하지 않습니다.", exception.getMessage());

    }

    @DisplayName("routine_id로 schedule 조회")
    @Test
    public void findAllByRoutineIdTest() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate newLocalDate = localDate.plusMonths(1);
        Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        RoutineRequestDTO routineRequestDTO = new RoutineRequestDTO();
        routineRequestDTO.setRoutineStart(date);
        routineRequestDTO.setRoutineEnd(newDate);
        routineRequestDTO.setRoutineCycle("weekly");

        Long routineId = routineService.addRoutine(routineRequestDTO);

        ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
        scheduleRequest.setTitle("bye");scheduleRequest.setContext("hello");scheduleRequest.setStatus(0);
        scheduleRequest.setNotificationYn(false);scheduleRequest.setDeleteYn(false);scheduleRequest.setCreatedAt(date);
        scheduleRequest.setUpdatedAt(null);scheduleRequest.setStartTime(date);scheduleRequest.setEndTime(date);
        scheduleRequest.setRoutineId(routineId);scheduleRequest.setCategoryNo(4L);

        ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);
        assertThat(responseDTO).isNotNull();

        List<ScheduleResponseDTO> allByRoutineId = routineService.findAllByRoutineId(routineId);
        for (ScheduleResponseDTO scheduleResponseDTO : allByRoutineId) {
            assertThat(scheduleResponseDTO).isNotNull();
            assertThat(scheduleResponseDTO.getRoutineId()).isEqualTo(routineId);
        }
    }

    @DisplayName("routine_id로 schedule 조회 - 실패")
    @Test
    public void findAllByRoutineIdTestFail() {
        Long routineId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            routineService.findAllByRoutineId(routineId);
        });
        assertEquals("루틴 ID가 유효하지 않습니다.", exception.getMessage());

    }

    @DisplayName("routine 수정")
    @Test
    public void updateRoutineTest() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate newLocalDate = localDate.plusMonths(1);
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

    @DisplayName("routine 수정 - 실패")
    @Test
    public void updateRoutineTestFail() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate newLocalDate = localDate.plusMonths(1);
        Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Long routineId = -1L;

        RoutineRequestDTO updateDto = new RoutineRequestDTO();
        updateDto.setRoutineStart(date);
        updateDto.setRoutineEnd(newDate);
        updateDto.setRoutineCycle("monthly");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            routineService.updateRoutine(routineId, updateDto);
        });
        assertEquals("루틴 ID가 유효하지 않습니다.", exception.getMessage());

    }

    @DisplayName("routine 삭제")
    @Test
    public void deleteRoutineTest() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate newLocalDate = localDate.plusMonths(1);
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

    @DisplayName("routine 삭제 - 실패")
    @Test
    public void deleteRoutineTestFail() {
        Long routineId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            routineService.deleteRoutine(routineId);
        });
        assertEquals("루틴 ID가 유효하지 않습니다.", exception.getMessage());
    }
}