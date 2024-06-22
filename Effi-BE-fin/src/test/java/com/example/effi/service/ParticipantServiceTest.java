package com.example.effi.service;

import com.example.effi.domain.DTO.CategoryDTO;
import com.example.effi.domain.DTO.ParticipantResponseDTO;
import com.example.effi.domain.DTO.ScheduleRequestDTO;
import com.example.effi.domain.DTO.ScheduleResponseDTO;
import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.RoutineRepository;
import io.lettuce.core.AbstractRedisAsyncCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ParticipantServiceTest {

    @Autowired
    ScheduleService scheduleService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ParticipantService participantService;
    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setUp() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("1", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        employeeRepository.save(Employee.builder()
                .id(1L)
                .empNo(1L)
                .password("password1")
                .company("Example Company")
                .name("John Doe")
                .email("john@example.com")
                .phoneNum("123-456-7890")
                .extensionNum("123")
                .rank("Manager")
                .build()
        );
        categoryRepository.save(Category.builder()
                .categoryId(1L)
                .categoryName("hi").build());
    }

    @DisplayName("schedule_id랑 empId로 participant 추가")
    @Test
    public void addParticipantTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName());
        Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);

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
        scheduleRequest.setCategoryNo(1L);
    

        ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);
        assertThat(responseDTO).isNotNull();

        ParticipantResponseDTO participantResponseDTO = participantService.addParticipant(responseDTO.getScheduleId(),empId);
        assertThat(participantResponseDTO).isNotNull();
        assertThat(participantResponseDTO.getEmpId()).isEqualTo(empId);
        assertThat(participantResponseDTO.getScheduleId()).isEqualTo(responseDTO.getScheduleId());
    }

    @DisplayName("schedule_id로 participantList 조회")
    @Test
    public void findAllByScheduleIdTest() {
        Long schedule_id = 1L;

        List<ParticipantResponseDTO> allByScheduleId = participantService.findAllByScheduleId(schedule_id);
        assertThat(allByScheduleId).isNotNull();
        for (ParticipantResponseDTO participantResponseDTO : allByScheduleId) {
            assertThat(participantResponseDTO).isNotNull();
            assertThat(participantResponseDTO.getScheduleId()).isEqualTo(schedule_id);
        }
    }

    @DisplayName("emp_id로 participantList 조회")
    @Test
    public void findAllByEmpIdTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName());
        Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);

        List<ParticipantResponseDTO> allByEmpId = participantService.findAllByEmpId(empId);
        assertThat(allByEmpId).isNotNull();
        for (ParticipantResponseDTO participantResponseDTO : allByEmpId) {
            assertThat(participantResponseDTO).isNotNull();
            assertThat(participantResponseDTO.getEmpId()).isEqualTo(creatorEmpNo);
        }
    }

    @DisplayName("participant_id로 participant 조회")
    @Test
    public void findByParticipantIdTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName());
        Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);


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
        scheduleRequest.setCategoryNo(1L);
 

        ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);
        assertThat(responseDTO).isNotNull();

        ParticipantResponseDTO participantResponseDTO = participantService.addParticipant(responseDTO.getScheduleId(), empId);
        assertThat(participantResponseDTO).isNotNull();

        Long participantId = participantResponseDTO.getParticipantId();

        ParticipantResponseDTO byParticipantId = participantService.findByParticipantId(participantId);
        assertThat(byParticipantId).isNotNull();
        assertThat(byParticipantId.getParticipantId()).isEqualTo(participantId);
    }

    @DisplayName("participant 삭제")
    @Test
    public void deleteTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName());
        Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(1L);
        categoryDTO.setCategoryName("hi");

        ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
        scheduleRequest.setTitle("bye");
        scheduleRequest.setContext("hello");
        scheduleRequest.setStatus(0);
        scheduleRequest.setNotificationYn(false);scheduleRequest.setDeleteYn(false);
        scheduleRequest.setCreatedAt(new Date());scheduleRequest.setUpdatedAt(null);
        scheduleRequest.setStartTime(new Date());scheduleRequest.setEndTime(new Date());
        scheduleRequest.setRoutineId(null);
        scheduleRequest.setCategoryNo(1L);

        ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);
        assertThat(responseDTO).isNotNull();

        ParticipantResponseDTO participantResponseDTO = participantService.addParticipant(responseDTO.getScheduleId(), empId);
        assertThat(participantResponseDTO).isNotNull();

        Long participantId = participantResponseDTO.getParticipantId();

        Long deleteParticiantId = participantService.delete(participantId);

        ParticipantResponseDTO byParticipantId = participantService.findByParticipantId(deleteParticiantId);
        assertThat(byParticipantId).isNotNull();
        assertThat(byParticipantId.getParticipantId()).isEqualTo(participantId);
        assertThat(byParticipantId.getDeleteYn()).isTrue();
    }

}