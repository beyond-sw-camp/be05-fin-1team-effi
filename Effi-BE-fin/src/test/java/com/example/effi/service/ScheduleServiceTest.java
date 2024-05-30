package com.example.effi.service;

import com.example.effi.domain.DTO.ScheduleRequestDTO;
import com.example.effi.domain.DTO.ScheduleResponseDTO;
import com.example.effi.domain.Entitiy.Category;
import com.example.effi.domain.Entitiy.Participant;
import com.example.effi.domain.Entitiy.Routine;
import com.example.effi.domain.Entitiy.Schedule;
import com.example.effi.repository.CategoryRepository;
import com.example.effi.repository.ParticipantRepository;
import com.example.effi.repository.RoutineRepository;
import com.example.effi.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.println;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private ParticipantService participantService;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        employeeService = mock(EmployeeService.class); // Mock 객체 생성
        participantService = mock(ParticipantService.class); // Mock 객체 생성
        scheduleService = new ScheduleService(scheduleRepository, categoryRepository, routineRepository,
                participantRepository, participantService, employeeService);
    }

//    @Test
//    void testGetSchedule() {
//        Category category = Category.builder()
//                .categoryId(1L)
//                .categoryName("1번")
//                .build();
//
//        categoryRepository.save(category);
//
//        Schedule schedule = Schedule.builder()
//                .title("title")
//                .context("context")
//                .startTime(new Date())
//                .endTime(new Date())
//                .status(0)
//                .deleteYn(false)
//                .notificationYn(false)
//                .routine(null)
//                .category(category)
//                .createdAt(new Date())
//                .updatedAt(null)
//                .build();
//
//        Schedule savedSchedule = scheduleRepository.save(schedule);
//        System.out.println("schedule = " + schedule.getContext());
//
//        verify(scheduleRepository, times(1)).save(any(Schedule.class));
//
//        System.out.println("savedschedule " + savedSchedule);
//        Long id = savedSchedule.getScheduleId();
//
//        System.out.println(id);
//
//        when(scheduleRepository.findById(id)).thenReturn(Optional.of(savedSchedule));
//
//        ScheduleResponseDTO result = scheduleService.getSchedule(id);
//        assertNotNull(result);
//        assertEquals(id, result.getScheduleId());
//    }


//    @Test
//    void testGetAllSchedules() {
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getName()).thenReturn("123");
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        Participant participant = new Participant();
//        Schedule schedule = new Schedule();
//        schedule.setDeleteYn(false);
//        participant.setSchedule(schedule);
//        when(employeeService.findEmpIdByEmpNo(anyLong())).thenReturn(1L);
//        when(participantRepository.findAllByEmployee_Id(anyLong())).thenReturn(Arrays.asList(participant));
//
//        assertNotNull(scheduleService.getAllSchedules());
//    }

//    @Test
//    void testGetSchedulesByCategory() {
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getName()).thenReturn("123");
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        Schedule schedule = new Schedule();
//        schedule.setScheduleId(1L);
//        Participant participant = new Participant();
//        participant.setDeleteYn(false);
//        participant.setSchedule(schedule);
//        when(employeeService.findEmpIdByEmpNo(anyLong())).thenReturn(1L);
//        when(scheduleRepository.findAllByCategory_CategoryId(anyLong())).thenReturn(Arrays.asList(schedule));
//        when(participantRepository.findByEmployee_IdAndSchedule_ScheduleId(anyLong(), anyLong())).thenReturn(participant);
//
//        assertNotNull(scheduleService.getSchedulesByCategory(1L));
//    }

    @Test
    void testAddSchedule() {

        Category category = Category.builder()
                .categoryId(1L)
                .categoryName("1번")
                .build();

        categoryRepository.save(category);
        verify(categoryRepository, times(1)).save(any(Category.class));


        ScheduleRequestDTO scheduleRequestDTO = new ScheduleRequestDTO();
        scheduleRequestDTO.setScheduleId(1L);
        scheduleRequestDTO.setCategoryId(1L);
        scheduleRequestDTO.setTitle("Test Title");
        scheduleRequestDTO.setContext("Test Context");
        scheduleRequestDTO.setStartTime(new Date());
        scheduleRequestDTO.setEndTime(new Date());
        scheduleRequestDTO.setDeleteYn(false);
        scheduleRequestDTO.setNotificationYn(false);
        scheduleRequestDTO.setStatus(0);


        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(i -> i.getArguments()[0]);

        ScheduleResponseDTO result = scheduleService.addSchedule(scheduleRequestDTO);
        assertNotNull(result);
        assertEquals(1L, result.getCategoryId());
    }

//    @Test
//    void testAddRoutineSchedule() {
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getName()).thenReturn("123");
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        Schedule schedule = new Schedule();
//        schedule.setScheduleId(1L);
//        schedule.setStartTime(new Date());
//        schedule.setEndTime(new Date());
//
//        ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
//        scheduleResponseDTO.setScheduleId(1L);
//        scheduleResponseDTO.setCategoryId(1L);
//        scheduleResponseDTO.setRoutineId(1L);
//
//        Category category = new Category();
//        category.setCategoryId(1L);
//
//        Routine routine = new Routine();
//        routine.setRoutineCycle("daily");
//        routine.setRoutineEnd(Date.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()));
//
//        when(employeeService.findEmpIdByEmpNo(anyLong())).thenReturn(1L);
//        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
//        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
//        when(routineRepository.findById(1L)).thenReturn(Optional.of(routine));
//        when(scheduleRepository.saveAll(anyList())).thenReturn(Arrays.asList(schedule));
//
//        assertNotNull(scheduleService.addRoutineSchedule(scheduleResponseDTO));
//    }

//    @Test
//    void testUpdateSchedule() {
//        ScheduleRequestDTO scheduleRequestDTO = new ScheduleRequestDTO();
//        scheduleRequestDTO.setCategoryId(1L);
//        scheduleRequestDTO.setRoutineId(1L);
//
//        Schedule schedule = new Schedule();
//        schedule.setScheduleId(1L);
//
//        Category category = new Category();
//        category.setCategoryId(1L);
//
//        Routine routine = new Routine();
//        routine.setRoutineId(1L);
//
//        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
//        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
//        when(routineRepository.findById(1L)).thenReturn(Optional.of(routine));
//        when(scheduleRepository.save(any(Schedule.class))).thenAnswer(i -> i.getArguments()[0]);
//
//        ScheduleResponseDTO result = scheduleService.updateSchedule(scheduleRequestDTO, 1L);
//        assertNotNull(result);
//        assertEquals(1L, result.getCategoryId());
//    }

//    @Test
//    void testDeleteSchedule() {
//        Schedule schedule = new Schedule();
//        schedule.setScheduleId(1L);
//
//        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
//
//        scheduleService.deleteSchedule(1L);
//        verify(scheduleRepository, times(1)).save(any(Schedule.class));
//    }
}
