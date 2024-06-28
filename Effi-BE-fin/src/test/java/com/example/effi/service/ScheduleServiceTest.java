 package com.example.effi.service;

 import ch.qos.logback.classic.Logger;
 import com.example.effi.domain.DTO.*;
 import com.example.effi.domain.Entity.*;
 import com.example.effi.repository.*;
 import com.example.effi.service.ScheduleService;
 import org.junit.jupiter.api.*;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
 import org.springframework.security.core.Authentication;
 import org.springframework.security.core.context.SecurityContextHolder;

 import java.time.LocalDate;
 import java.time.ZoneId;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;

 import static org.assertj.core.api.Assertions.assertThat;
 import static org.junit.jupiter.api.Assertions.assertThrows;

 @SpringBootTest
 @TestInstance(TestInstance.Lifecycle.PER_CLASS)
 public class ScheduleServiceTest {

     @Autowired
     ScheduleService scheduleService;
     @Autowired
     private CategoryRepository categoryRepository;
     @Autowired
     private RoutineRepository routineRepository;
     @Autowired
     private EmployeeService employeeService;
     @Autowired
     private ParticipantService participantService;
     @Autowired
     private RoutineService routineService;
     @Autowired
     private CategoryService categoryService;
     @Autowired
     private EmployeeRepository employeeRepository;
     @Autowired
     private DeptRepository deptRepository;

     @BeforeAll
     public void setUp() {
         Authentication authentication = new UsernamePasswordAuthenticationToken("1", "password1");
         SecurityContextHolder.getContext().setAuthentication(authentication);
         Dept testDept = deptRepository.save(Dept.builder()
                 .deptName("TestDept").build()
         );
         employeeRepository.save(Employee.builder()
                 .empNo(1L)
                 .password("password1")
                 .company("Example Company")
                 .name("John Doe")
                 .email("john@example.com")
                 .phoneNum("123-456-7890")
                 .extensionNum("123")
                 .rank("Manager")
                 .dept(testDept)
                 .build()
         );
         categoryRepository.save(Category.builder().categoryId(1L).categoryName("회사").dept(null).group(null).build());
         categoryRepository.save(Category.builder().categoryId(2L).categoryName("부서").dept(null).group(null).build());
         categoryRepository.save(Category.builder().categoryId(3L).categoryName("그룹").dept(null).group(null).build());
         categoryRepository.save(Category.builder().categoryId(4L).categoryName("개인").dept(null).group(null).build());
         categoryRepository.save(Category.builder().categoryId(2L).categoryName("부서").dept(testDept).group(null).build());
     }

     @DisplayName("schedule_id로 schedule 조회")
     @Test
     public void getScheduleTest() {

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
         Long scheduleId = responseDTO.getScheduleId();

         ScheduleResponseDTO findSchedule = scheduleService.getSchedule(scheduleId);
         assertThat(findSchedule).isNotNull();
         assertThat(findSchedule.getScheduleId()).isEqualTo(scheduleId);
         assertThat(findSchedule.getContext()).isEqualTo("hello");
     }

     @DisplayName("잘못된 schedule_id로 schedule 조회 시 예외 발생")
     @Test
     public void getScheduleWithInvalidIdTest() {
         Long invalidScheduleId = -1L; // 유효하지 않은 schedule_id

         assertThrows(Exception.class, () -> {
             scheduleService.getSchedule(invalidScheduleId);
         });
     }

     @DisplayName("emp_id로 본인 schedule 조회")
     @Test
     public void getAllScheduleTest() {
         Authentication authentication = new UsernamePasswordAuthenticationToken("1", "password1");
         SecurityContextHolder.getContext().setAuthentication(authentication);
         Long creatorEmpNo = Long.valueOf(authentication.getName());
         Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);

         ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
         scheduleRequest.setTitle("bye");scheduleRequest.setContext("hello");
         scheduleRequest.setStatus(0);scheduleRequest.setNotificationYn(false);scheduleRequest.setDeleteYn(false);
         scheduleRequest.setCreatedAt(new Date());scheduleRequest.setUpdatedAt(null);
         scheduleRequest.setStartTime(new Date());scheduleRequest.setEndTime(new Date());
         scheduleRequest.setRoutineId(null);scheduleRequest.setCategoryNo(1L);
         scheduleService.addSchedule(scheduleRequest);

         List<ScheduleResponseDTO> allSchedules = scheduleService.getAllSchedules();
         assertThat(allSchedules).isNotEmpty();

         List<ParticipantResponseDTO> allByEmpId = participantService.findAllByEmpId(empId);
         assertThat(allByEmpId).isNotEmpty();

         List<Long> scheduleIdList = new ArrayList<>();
         for (ParticipantResponseDTO participantResponseDTO : allByEmpId) {
             assertThat(participantResponseDTO.getEmpId()).isEqualTo(empId);
             scheduleIdList.add(participantResponseDTO.getScheduleId());
         }

         for (ScheduleResponseDTO schedule : allSchedules)
             assertThat(scheduleIdList).contains(schedule.getScheduleId());
     }

     @DisplayName("잘못된 category_id로 schedules 조회 시 예외 발생")
     @Test
     public void getSchedulesByInvalidCategoryTest() {
         Long invalidCategoryId = -1L; // 유효하지 않은 category_id

         assertThrows(Exception.class, () -> {
             scheduleService.getSchedulesByCategory(invalidCategoryId);
         });
     }

     @DisplayName("emp_id, category_id로 schedule 조회")
     @Test
     public void getSchedulesByCategoryTest() {
         Authentication authentication = new UsernamePasswordAuthenticationToken("1", "password1");
         SecurityContextHolder.getContext().setAuthentication(authentication);
         Long creatorEmpNo = Long.valueOf(authentication.getName());
         Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);

         Long categoryId = 2L;
         CategoryResponseDTO byDeptId = categoryService.findByDeptId(1L);
         Long categoryNo = byDeptId.getCategoryNo();
         System.out.println("categoryNo = " + categoryNo);

         ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
         scheduleRequest.setTitle("bye");scheduleRequest.setContext("hello");
         scheduleRequest.setStatus(0);scheduleRequest.setNotificationYn(false);scheduleRequest.setDeleteYn(false);
         scheduleRequest.setCreatedAt(new Date());scheduleRequest.setUpdatedAt(null);
         scheduleRequest.setStartTime(new Date());scheduleRequest.setEndTime(new Date());
         scheduleRequest.setRoutineId(null);scheduleRequest.setCategoryNo(categoryNo);

         ScheduleResponseDTO scheduleResponseDTO = scheduleService.addSchedule(scheduleRequest);
         assertThat(scheduleResponseDTO).isNotNull();

         ParticipantResponseDTO participantResponseDTO = participantService.addParticipant(scheduleResponseDTO.getScheduleId(), empId);
         assertThat(participantResponseDTO).isNotNull();
         assertThat(participantResponseDTO.getEmpId()).isEqualTo(empId);

         List<ScheduleResponseDTO> allSchedules = scheduleService.getSchedulesByCategory(categoryId);
          assertThat(allSchedules).isNotEmpty();

           for (ScheduleResponseDTO schedule : allSchedules) {
               CategoryResponseDTO byCategoryNo = categoryService.findByCategoryNo(schedule.getCategoryNo());
               assertThat(byCategoryNo.getCategoryId()).isEqualTo(categoryId);
           }
     }

     @DisplayName("잘못된 emp_id로 본인 schedule 조회 시 예외 발생")
     @Test
     public void getAllScheduleWithInvalidEmpIdTest() {
         Authentication authentication = new UsernamePasswordAuthenticationToken("3", "password1");
         SecurityContextHolder.getContext().setAuthentication(authentication);

         assertThrows(Exception.class, () -> {
             scheduleService.getAllSchedules();
         });
     }

     @DisplayName("routine 추가 확인")
     @Test
     public void addRoutineScheduleTest() {
         Authentication authentication = new UsernamePasswordAuthenticationToken("1", "password1");
         SecurityContextHolder.getContext().setAuthentication(authentication);
         Long creatorEmpNo = Long.valueOf(authentication.getName());
         Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);

         Date date = new Date();
         LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
         // 한 달 더하기
         LocalDate newLocalDate = localDate.plusMonths(1);
         // LocalDate를 Date로 변환
         Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

         Routine routine = Routine.builder()
                 .deleteYn(false).routineCycle("weekly")
                 .routineStart(date).routineEnd(newDate).build();
         routineRepository.save(routine);

         ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
         scheduleRequest.setTitle("bye");scheduleRequest.setContext("hello");
         scheduleRequest.setStatus(0);scheduleRequest.setNotificationYn(false);scheduleRequest.setDeleteYn(false);
         scheduleRequest.setCreatedAt(date);scheduleRequest.setUpdatedAt(null);
         scheduleRequest.setStartTime(date);scheduleRequest.setEndTime(newDate);
         scheduleRequest.setRoutineId(routine.getRoutineId());scheduleRequest.setCategoryNo(1L);

         ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);

         List<ScheduleResponseDTO> scheduleResponseDTOS = scheduleService.addRoutineSchedule(responseDTO);
         assertThat(scheduleResponseDTOS).isNotNull();
         assertThat(scheduleResponseDTOS.size()).isEqualTo(4); // 한달이라
     }

     @DisplayName("schedule 업데이트 (전체)")
     @Test
     public void updateScheduleTest() {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

         Long categoryNo = 1L;

         ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
         scheduleRequest.setTitle("bye");scheduleRequest.setContext("hello");
         scheduleRequest.setStatus(0);scheduleRequest.setNotificationYn(false);scheduleRequest.setDeleteYn(false);
         scheduleRequest.setCreatedAt(new Date());scheduleRequest.setUpdatedAt(null);
         scheduleRequest.setStartTime(new Date());scheduleRequest.setEndTime(new Date());
         scheduleRequest.setRoutineId(null);scheduleRequest.setCategoryNo(categoryNo);

         ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);
         assertThat(responseDTO).isNotNull();
         assertThat(responseDTO.getUpdatedAt()).isNull();

         ScheduleRequestDTO newRequest = new ScheduleRequestDTO();
         newRequest.setTitle("updated");newRequest.setContext("updated");
         newRequest.setStatus(0);newRequest.setNotificationYn(false);newRequest.setDeleteYn(false);
         newRequest.setCreatedAt(new Date());newRequest.setUpdatedAt(new Date());
         newRequest.setStartTime(new Date());newRequest.setEndTime(new Date());
         newRequest.setRoutineId(null);newRequest.setCategoryNo(categoryNo);

         CategoryResponseDTO responseDTO1 = new CategoryResponseDTO();
         ScheduleResponseDTO updateSchedule = scheduleService.updateSchedule(newRequest, responseDTO.getScheduleId(), responseDTO1);
         assertThat(updateSchedule).isNotNull();
         assertThat(updateSchedule.getUpdatedAt()).isNotNull();
         assertThat(updateSchedule.getTitle()).isEqualTo("updated");
         assertThat(updateSchedule.getScheduleId()).isEqualTo(responseDTO.getScheduleId());
     }

     @DisplayName("잘못된 schedule_id로 schedule 업데이트 시 예외 발생")
     @Test
     public void updateScheduleWithInvalidIdTest() {
         Long invalidScheduleId = -1L; // 유효하지 않은 schedule_id

         CategoryResponseDTO responseDTO1 = new CategoryResponseDTO();
         assertThrows(Exception.class, () -> {
             ScheduleRequestDTO scheduleRequestDTO = new ScheduleRequestDTO();
             // 업데이트할 내용 설정
             scheduleService.updateSchedule(scheduleRequestDTO, invalidScheduleId, responseDTO1);
         });
     }

     @DisplayName("schedule 업데이트 (루틴)")
     @Test
     public void updateRoutineTest() {
         Authentication authentication = new UsernamePasswordAuthenticationToken("1", "password1");
         SecurityContextHolder.getContext().setAuthentication(authentication);

         Date date = new Date();
         LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
         LocalDate newLocalDate = localDate.plusMonths(1);
         Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

         ScheduleRequestDTO scheduleRequest = new ScheduleRequestDTO();
         scheduleRequest.setTitle("bye");scheduleRequest.setContext("hello");
         scheduleRequest.setStatus(0);scheduleRequest.setNotificationYn(false);scheduleRequest.setDeleteYn(false);
         scheduleRequest.setCreatedAt(date);scheduleRequest.setUpdatedAt(null);
         scheduleRequest.setStartTime(date);scheduleRequest.setEndTime(date);
         scheduleRequest.setRoutineId(null);scheduleRequest.setCategoryNo(1L);

         ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);
         assertThat(responseDTO).isNotNull();
         assertThat(responseDTO.getUpdatedAt()).isNull();

         RoutineRequestDTO routineRequestDTO = new RoutineRequestDTO();
         routineRequestDTO.setRoutineStart(date);routineRequestDTO.setRoutineEnd(newDate);
         routineRequestDTO.setRoutineCycle("weekly");

         Long routineId = routineService.addRoutine(routineRequestDTO);


         ScheduleResponseDTO updateRoutine = scheduleService.updateRoutine(routineId, responseDTO.getScheduleId());

         assertThat(updateRoutine).isNotNull();
         assertThat(updateRoutine.getUpdatedAt()).isNotNull();
         assertThat(updateRoutine.getRoutineId()).isEqualTo(routineId);
         assertThat(updateRoutine.getTitle()).isEqualTo(responseDTO.getTitle());
         assertThat(updateRoutine.getContext()).isEqualTo(responseDTO.getContext());
     }

     @DisplayName("schedule 삭제")
     @Test
     public void deleteScheduleTest() {

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
         scheduleRequest.setCategoryNo(1L);

         ScheduleResponseDTO responseDTO = scheduleService.addSchedule(scheduleRequest);

         scheduleService.deleteSchedule(responseDTO.getScheduleId());
         assertThat(responseDTO.getDeleteYn()).isFalse();

     }

     @DisplayName("잘못된 schedule_id로 schedule 삭제 시 예외 발생")
     @Test
     public void deleteScheduleWithInvalidIdTest() {
         Long invalidScheduleId = -1L; // 유효하지 않은 schedule_id

         assertThrows(Exception.class, () -> {
             scheduleService.deleteSchedule(invalidScheduleId);
         });
     }

 }
