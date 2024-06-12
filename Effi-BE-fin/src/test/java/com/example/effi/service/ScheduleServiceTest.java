// package com.example.effi.service;

// import ch.qos.logback.classic.Logger;
// import com.example.effi.domain.DTO.*;
// import com.example.effi.domain.Entity.Category;
// import com.example.effi.domain.Entity.Employee;
// import com.example.effi.domain.Entity.Routine;
// import com.example.effi.domain.Entity.Schedule;
// import com.example.effi.repository.CategoryRepository;
// import com.example.effi.repository.EmployeeRepository;
// import com.example.effi.repository.RoutineRepository;
// import com.example.effi.service.ScheduleService;
// import org.junit.jupiter.api.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;

// import java.time.LocalDate;
// import java.time.ZoneId;
// import java.util.ArrayList;
// import java.util.Date;
// import java.util.List;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertThrows;

// @SpringBootTest
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// public class ScheduleServiceTest {

//     @Autowired
//     ScheduleService scheduleService;
//     @Autowired
//     private CategoryRepository categoryRepository;
//     @Autowired
//     private RoutineRepository routineRepository;
//     @Autowired
//     private EmployeeService employeeService;
//     @Autowired
//     private ParticipantService participantService;
//     @Autowired
//     private RoutineService routineService;
//     @Autowired
//     private CategoryService categoryService;
//     @Autowired
//     private EmployeeRepository employeeRepository;

//     @BeforeEach
//     public void setUp() {
//         Authentication authentication = new UsernamePasswordAuthenticationToken("1", null);
//         SecurityContextHolder.getContext().setAuthentication(authentication);
//         employeeRepository.save(Employee.builder()
//                 .id(1L)
//                 .empNo(1L)
//                 .password("password1")
//                 .company("Example Company")
//                 .name("John Doe")
//                 .email("john@example.com")
//                 .phoneNum("123-456-7890")
//                 .extensionNum("123")
//                 .rank("Manager")
//                 .build()
//         );
//         categoryRepository.save(Category.builder()
//                 .categoryId(1L)
//                 .categoryName("hi").build());
//     }

//     @DisplayName("schedule_id로 schedule 조회")
//     @Test
//     public void getScheduleTest() {

//         ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
//         scheduleRequest.setTitle("bye");
//         scheduleRequest.setContext("hello");
//         scheduleRequest.setStatus(0);
//         scheduleRequest.setNotificationYn(false);
//         scheduleRequest.setDeleteYn(false);
//         scheduleRequest.setCreatedAt(new Date());
//         scheduleRequest.setUpdatedAt(new Date());
//         scheduleRequest.setStartTime(new Date());
//         scheduleRequest.setEndTime(new Date());
//         scheduleRequest.setRoutineId(null);

//         ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);
//         Long scheduleId = responseDTO.getScheduleId();

//         ScheduleResponseDTO findSchedule = scheduleService.getSchedule(scheduleId);
//         assertThat(findSchedule).isNotNull();
//         assertThat(findSchedule.getScheduleId()).isEqualTo(scheduleId);
//         assertThat(findSchedule.getContext()).isEqualTo("hello");
//     }

//     @DisplayName("emp_id로 본인 schedule 조회")
//     @Test
//     public void getAllScheduleTest() { // ? auth 어떻게 가져오지
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//         Long creatorEmpNo = Long.valueOf(authentication.getName());
//         Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);

//         ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
//         scheduleRequest.setTitle("bye");
//         scheduleRequest.setContext("hello");
//         scheduleRequest.setStatus(0);
//         scheduleRequest.setNotificationYn(false);
//         scheduleRequest.setDeleteYn(false);
//         scheduleRequest.setCreatedAt(new Date());
//         scheduleRequest.setUpdatedAt(new Date());
//         scheduleRequest.setStartTime(new Date());
//         scheduleRequest.setEndTime(new Date());
//         scheduleRequest.setRoutineId(null);

//         ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);

//         List<ScheduleResponseDTO> allSchedules = scheduleService.getAllSchedules();
//         assertThat(allSchedules).isNotEmpty();

//         List<ParticipantResponseDTO> allByEmpId = participantService.findAllByEmpId(empId);
//         assertThat(allByEmpId).isNotEmpty();

//         List<Long> scheduleIdList = new ArrayList<>();
//         for (ParticipantResponseDTO participantResponseDTO : allByEmpId) {
//             assertThat(participantResponseDTO.getEmpId()).isEqualTo(empId);
//             scheduleIdList.add(participantResponseDTO.getScheduleId());
//         }

//         // 본인 스케줄 검증
//         for (ScheduleResponseDTO schedule : allSchedules) {
//             assertThat(scheduleIdList).contains(schedule.getScheduleId());
//         }



//     }

//     @DisplayName("emp_id, category_id로 schedule 조회")
//     @Test
//     public void getSchedulesByCategoryTest() {
//         SecurityContextHolder.getContext().getAuthentication();

//         Long categoryId = categoryService.findCategory(1L).getCategoryId();


//         ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
//         scheduleRequest.setTitle("bye");
//         scheduleRequest.setContext("hello");
//         scheduleRequest.setStatus(0);
//         scheduleRequest.setNotificationYn(false);
//         scheduleRequest.setDeleteYn(false);
//         scheduleRequest.setCreatedAt(new Date());
//         scheduleRequest.setUpdatedAt(new Date());
//         scheduleRequest.setStartTime(new Date());
//         scheduleRequest.setEndTime(new Date());
//         scheduleRequest.setRoutineId(null);

//         // scheduleService.addSchedule(scheduleRequest);


//         // List<ScheduleResponseDTO> allSchedules = scheduleService.getSchedulesByCategory(categoryId);
//         // assertThat(allSchedules).isNotEmpty();

//         // // for (ScheduleResponseDTO schedule : allSchedules) {
//         // //     assertThat(schedule.getCategoryId()).isEqualTo(categoryId);
//         // // }

//     @DisplayName("routine 추가 확인")
//     @Test
//     public void addRoutineScheduleTest() {
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//         Date date = new Date();

//         // Date를 LocalDate로 변환
//         LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//         // 한 달 더하기
//         LocalDate newLocalDate = localDate.plusMonths(1);
//         // LocalDate를 Date로 변환
//         Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

//         Category category = Category.builder()
//                 .categoryId(1L)
//                 .categoryName("one")
//                 .build();

//         categoryRepository.save(category);

//         Routine routine = Routine.builder()
//                 .deleteYn(false)
//                 .routineCycle("weekly")
//                 .routineStart(date)
//                 .routineEnd(newDate)
//                 .build();
//         routineRepository.save(routine);

//         ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
//         scheduleRequest.setTitle("bye");
//         scheduleRequest.setContext("hello");
//         scheduleRequest.setStatus(0);
//         scheduleRequest.setNotificationYn(false);
//         scheduleRequest.setDeleteYn(false);
//         scheduleRequest.setCreatedAt(date);
//         scheduleRequest.setUpdatedAt(null);
//         scheduleRequest.setStartTime(date);
//         scheduleRequest.setEndTime(newDate);
//         scheduleRequest.setRoutineId(routine.getRoutineId());

//         ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);

//         List<ScheduleResponseDTO> scheduleResponseDTOS = scheduleService.addRoutineSchedule(responseDTO);
//         assertThat(scheduleResponseDTOS).isNotNull();
//         assertThat(scheduleResponseDTOS.size()).isEqualTo(4); // 한달이라
//     }

//     @DisplayName("schedule 업데이트 (전체)")
//     @Test
//     public void updateScheduleTest() {
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//         CategoryDTO categoryDTO = new CategoryDTO();
//         categoryDTO.setCategoryId(1L);
//         categoryDTO.setCategoryName("hi");
//         Long categoryId = categoryDTO.getCategoryId();

//         ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
//         scheduleRequest.setTitle("bye");
//         scheduleRequest.setContext("hello");
//         scheduleRequest.setStatus(0);
//         scheduleRequest.setNotificationYn(false);
//         scheduleRequest.setDeleteYn(false);
//         scheduleRequest.setCreatedAt(new Date());
//         scheduleRequest.setUpdatedAt(null);
//         scheduleRequest.setStartTime(new Date());
//         scheduleRequest.setEndTime(new Date());
//         scheduleRequest.setRoutineId(null);
//         scheduleRequest.setCategoryId(categoryId);

//         ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);
//         assertThat(responseDTO).isNotNull();
//         assertThat(responseDTO.getUpdatedAt()).isNull();

//         ScheduleRequestDTO newRequest = new ScheduleRequestDTO();
//         newRequest.setTitle("updated");
//         newRequest.setContext("updated");
//         newRequest.setStatus(0);
//         newRequest.setNotificationYn(false);
//         newRequest.setDeleteYn(false);
//         newRequest.setCreatedAt(new Date());
//         newRequest.setUpdatedAt(new Date());
//         newRequest.setStartTime(new Date());
//         newRequest.setEndTime(new Date());
//         newRequest.setRoutineId(null);
//         newRequest.setCategoryId(categoryId);

//         ScheduleResponseDTO updateSchedule = scheduleService.updateSchedule(newRequest, responseDTO.getScheduleId());
//         assertThat(updateSchedule).isNotNull();
//         assertThat(updateSchedule.getUpdatedAt()).isNotNull();
//         assertThat(updateSchedule.getTitle()).isEqualTo("updated");
//         assertThat(updateSchedule.getScheduleId()).isEqualTo(responseDTO.getScheduleId());
//     }

//     @DisplayName("schedule 업데이트 (루틴)")
//     @Test
//     public void updateRoutineTest() {
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//         Date date = new Date();
//         // Date를 LocalDate로 변환
//         LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//         // 한 달 더하기
//         LocalDate newLocalDate = localDate.plusMonths(1);
//         // LocalDate를 Date로 변환
//         Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

//         CategoryDTO categoryDTO = new CategoryDTO();
//         categoryDTO.setCategoryId(1L);
//         categoryDTO.setCategoryName("hi");
//         Long categoryId = categoryDTO.getCategoryId();

//         ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
//         scheduleRequest.setTitle("bye");
//         scheduleRequest.setContext("hello");
//         scheduleRequest.setStatus(0);
//         scheduleRequest.setNotificationYn(false);
//         scheduleRequest.setDeleteYn(false);
//         scheduleRequest.setCreatedAt(date);
//         scheduleRequest.setUpdatedAt(null);
//         scheduleRequest.setStartTime(date);
//         scheduleRequest.setEndTime(date);
//         scheduleRequest.setRoutineId(null);
//         scheduleRequest.setCategoryId(categoryId);

//         ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);
//         assertThat(responseDTO).isNotNull();
//         assertThat(responseDTO.getUpdatedAt()).isNull();

//         RoutineRequestDTO routineRequestDTO = new RoutineRequestDTO();
//         routineRequestDTO.setRoutineStart(date);
//         routineRequestDTO.setRoutineEnd(newDate);
//         routineRequestDTO.setRoutineCycle("weekly");

//         Long routineId = routineService.addRoutine(routineRequestDTO);


//         ScheduleResponseDTO updateRoutine = scheduleService.updateRoutine(routineId, responseDTO.getScheduleId());

//         assertThat(updateRoutine).isNotNull();
//         assertThat(updateRoutine.getUpdatedAt()).isNotNull();
//         assertThat(updateRoutine.getRoutineId()).isEqualTo(routineId);
//         assertThat(updateRoutine.getTitle()).isEqualTo(responseDTO.getTitle());
//         assertThat(updateRoutine.getContext()).isEqualTo(responseDTO.getContext());
//     }

//     @DisplayName("schedule 삭제")
//     @Test
//     public void deleteScheduleTest() {
//         CategoryDTO categoryDTO = new CategoryDTO();
//         categoryDTO.setCategoryId(1L);
//         categoryDTO.setCategoryName("hi");

//         ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
//         scheduleRequest.setTitle("bye");
//         scheduleRequest.setContext("hello");
//         scheduleRequest.setStatus(0);
//         scheduleRequest.setNotificationYn(false);
//         scheduleRequest.setDeleteYn(false);
//         scheduleRequest.setCreatedAt(new Date());
//         scheduleRequest.setUpdatedAt(new Date());
//         scheduleRequest.setStartTime(new Date());
//         scheduleRequest.setEndTime(new Date());
//         scheduleRequest.setRoutineId(null);
//         scheduleRequest.setCategoryId(1L);

//         ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);

//         scheduleService.deleteSchedule(responseDTO.getScheduleId());
//         assertThat(responseDTO.getDeleteYn()).isFalse();

//     }

// }
