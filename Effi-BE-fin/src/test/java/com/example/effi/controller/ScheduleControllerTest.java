 package com.example.effi.controller;


 import com.example.effi.domain.DTO.*;
 import com.example.effi.domain.Entity.*;
 import com.example.effi.repository.CategoryRepository;
 import com.example.effi.repository.EmployeeRepository;
 import com.example.effi.repository.GroupEmpRepository;
 import com.example.effi.service.*;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.boot.test.mock.mockito.MockBean;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.MediaType;
 import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
 import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
 import org.springframework.security.core.context.SecurityContextHolder;
 import org.springframework.test.web.servlet.MockMvc;
 import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
 import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
 import org.springframework.test.web.servlet.setup.MockMvcBuilders;
 import org.springframework.web.server.ResponseStatusException;

 import java.time.LocalDateTime;
 import java.time.ZoneId;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.sql.Date;

 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.ArgumentMatchers.anyLong;
 import static org.mockito.BDDMockito.given;
 import static org.mockito.Mockito.*;
 import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 @SpringBootTest
 public class ScheduleControllerTest {

     private MockMvc mockMvc;

     @Autowired
     private ObjectMapper objectMapper;

     @MockBean
     private ScheduleService scheduleService;

     @MockBean
     private EmployeeService employeeService;

     @MockBean
     private ParticipantService participantService;

     @InjectMocks
     private ScheduleController scheduleController;
     @Autowired
     private EmployeeRepository employeeRepository;
     @Autowired
     private GroupEmpRepository groupEmpRepository;
     @Autowired
     private CategoryService categoryService;
     @Autowired
     private CategoryRepository categoryRepository;

     @BeforeEach
     void setUp() {
         employeeService = mock(EmployeeService.class);
         participantService = mock(ParticipantService.class);
         employeeRepository = mock(EmployeeRepository.class);
         scheduleService = mock(ScheduleService.class);
         categoryService = mock(CategoryService.class);
         scheduleController = new ScheduleController(scheduleService, employeeService, participantService,employeeRepository,
                 groupEmpRepository, categoryService, categoryRepository);
         mockMvc = MockMvcBuilders.standaloneSetup(scheduleController).build();
     }

     @Test
     @DisplayName("스케줄 추가 - 회사 - 성공")
     void testAddScheduleCompany() throws Exception {
         ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
         scheduleResponseDTO.setScheduleId(1L);

         // Mocking behavior
         when(scheduleService.addSchedule(any(ScheduleRequestDTO.class))).thenReturn(scheduleResponseDTO);
         when(employeeRepository.findAll()).thenReturn(new ArrayList<>());

         // 요청 데이터 준비
         ScheduleRequestDTO requestDTO = new ScheduleRequestDTO();
         requestDTO.setTitle("테스트 스케줄");
         requestDTO.setContext("테스트 내용");
         LocalDateTime startTime = LocalDateTime.of(2023, 12, 1, 0, 0);
         requestDTO.setStartTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
         LocalDateTime endTime = LocalDateTime.of(2023, 12, 2, 0, 0);
         requestDTO.setEndTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));

         // MockMvc를 사용하여 POST 요청 수행
         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/add/company")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(requestDTO)))
                 .andExpect(status().isOk());
     }

     @DisplayName("Schedule 추가 회사 - 실패")
     @Test
     void testaddScheduleCompanyFail() throws Exception {
         when(scheduleService.addSchedule(any(ScheduleRequestDTO.class)))
                 .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

         ScheduleRequestDTO requestDTO = new ScheduleRequestDTO();
         requestDTO.setTitle("Test");
         requestDTO.setContext("Test context");
         LocalDateTime startTime = LocalDateTime.of(2023, 12, 1, 0, 0);
         requestDTO.setStartTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
         LocalDateTime endTime = LocalDateTime.of(2023, 12, 2, 0, 0);
         requestDTO.setEndTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));

         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/add/company")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(requestDTO)))
                 .andExpect(status().isInternalServerError());
     }

     @DisplayName("Schedule 추가 부서 - 성공")
     @Test
     void testaddScheduleDept() throws Exception {
         CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
         categoryResponseDTO.setCategoryNo(2L);

         ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
         scheduleResponseDTO.setScheduleId(1L);

         when(categoryService.findByDeptId(any())).thenReturn(categoryResponseDTO);
         when(scheduleService.addSchedule(any(ScheduleRequestDTO.class))).thenReturn(scheduleResponseDTO);
//         when(employeeService.findAllByDept_DeptId(any())).thenReturn(new ArrayList<>());

         ScheduleRequestDTO requestDTO = new ScheduleRequestDTO();
         requestDTO.setTitle("Test");
         requestDTO.setContext("Test context");
         LocalDateTime startTime = LocalDateTime.of(2023, 12, 1, 0, 0);
         requestDTO.setStartTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
         LocalDateTime endTime = LocalDateTime.of(2023, 12, 2, 0, 0);
         requestDTO.setEndTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));

         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/add/dept/1")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(requestDTO)))
                 .andExpect(status().isOk());
     }

     @DisplayName("Schedule 추가 부서 - 실패")
     @Test
     void testaddScheduleDeptFail() throws Exception {
         when(categoryService.findByDeptId(any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

         ScheduleRequestDTO requestDTO = new ScheduleRequestDTO();
         requestDTO.setTitle("Test");
         requestDTO.setContext("Test context");
         LocalDateTime startTime = LocalDateTime.of(2023, 12, 1, 0, 0);
         requestDTO.setStartTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
         LocalDateTime endTime = LocalDateTime.of(2023, 12, 2, 0, 0);
         requestDTO.setEndTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));

         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/add/dept/1")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(requestDTO)))
                 .andExpect(status().isInternalServerError());
     }

     @DisplayName("Schedule 추가 그룹 - 성공")
     @Test
     void testaddScheduleGroup() throws Exception {
         ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
         scheduleResponseDTO.setScheduleId(1L);

         when(categoryService.findByGroupId(any()))
                 .thenReturn(new CategoryResponseDTO(new Category(3L, 3L,
                         "그룹", null, Group.builder().build())));
         when(scheduleService.addSchedule(any(ScheduleRequestDTO.class))).thenReturn(scheduleResponseDTO);
         when(participantService.addParticipant(any(), any())).thenReturn(new ParticipantResponseDTO()); // 예시 반환값 사용

         ScheduleRequestDTO requestDTO = new ScheduleRequestDTO();
         requestDTO.setTitle("Test");
         requestDTO.setContext("Test context");
         LocalDateTime startTime = LocalDateTime.of(2023, 12, 1, 0, 0);
         requestDTO.setStartTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
         LocalDateTime endTime = LocalDateTime.of(2023, 12, 2, 0, 0);
         requestDTO.setEndTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));

         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/add/group/1")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(requestDTO)))
                 .andExpect(status().isOk());
     }

     @DisplayName("Schedule 추가 그룹 - 실패")
     @Test
     void testaddScheduleGroupFail() throws Exception {
         when(categoryService.findByGroupId(any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

         ScheduleRequestDTO requestDTO = new ScheduleRequestDTO();
         requestDTO.setTitle("Test");
         requestDTO.setContext("Test context");
         LocalDateTime startTime = LocalDateTime.of(2023, 12, 1, 0, 0);
         requestDTO.setStartTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
         LocalDateTime endTime = LocalDateTime.of(2023, 12, 2, 0, 0);
         requestDTO.setEndTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));

         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/add/group/1")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(requestDTO)))
                 .andExpect(status().isInternalServerError());
     }

     @DisplayName("Schedule 추가 개인 - 성공")
     @Test
     void testAddSchedule() throws Exception {
         UsernamePasswordAuthenticationToken authentication =
                 new UsernamePasswordAuthenticationToken("1", "password1");
         SecurityContextHolder.getContext().setAuthentication(authentication);

         ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
         scheduleResponseDTO.setScheduleId(1L);

         when(scheduleService.addSchedule(any(ScheduleRequestDTO.class))).thenReturn(scheduleResponseDTO);
         when(employeeService.findEmpIdByEmpNo(anyLong())).thenReturn(1L);

         ScheduleRequestDTO requestDTO = new ScheduleRequestDTO();
         requestDTO.setTitle("Test");
         requestDTO.setContext("Test context");
         LocalDateTime startTime = LocalDateTime.of(2023, 12, 1, 0, 0);
         requestDTO.setStartTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
         LocalDateTime endTime = LocalDateTime.of(2023, 12, 2, 0, 0);
         requestDTO.setEndTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
         requestDTO.setCategoryNo(4L);

         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/add")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(requestDTO)))
                 .andExpect(MockMvcResultMatchers.status().isOk());

         verify(participantService, times(1)).addParticipant(eq(1L), eq(1L));
     }

     @DisplayName("Schedule 추가 개인 - 실패")
     @Test
     void testAddScheduleFailure() throws Exception {
         when(scheduleService.addSchedule(any(ScheduleRequestDTO.class))).thenThrow(new RuntimeException("Service exception"));

         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/add")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content("{\"title\": \"Test\", \"context\": \"Test context\", \"startTime\": \"2023-12-01T00:00:00.000+00:00\", \"endTime\": \"2023-12-02T00:00:00.000+00:00\", \"categoryId\": 1}")
                         .param("empNo", "123"))
                 .andExpect(status().isInternalServerError());
     }

     @DisplayName("Schedule 수정 - 성공")
     @Test
     void testUpdateSchedule() throws Exception {
         ScheduleRequestDTO scheduleRequestDTO = new ScheduleRequestDTO();
         ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();

         when(scheduleService.updateSchedule(any(ScheduleRequestDTO.class), anyLong(), any(CategoryResponseDTO.class))).thenReturn(scheduleResponseDTO);

         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/update/1")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content("{\"title\": \"Test\", \"context\": \"Test context\", \"startTime\": \"2023-12-01T00:00:00.000+00:00\", \"endTime\": \"2023-12-02T00:00:00.000+00:00\", \"categoryId\": 1}"))
                 .andExpect(status().isOk());
     }

     @DisplayName("Schedule 수정 - 실패")
     @Test
     void testUpdateScheduleNotFound() throws Exception {
         when(scheduleService.updateSchedule(any(ScheduleRequestDTO.class), anyLong(), any(CategoryResponseDTO.class)))
                 .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/update/999")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content("{\"title\": \"Test\", \"context\": \"Test context\", \"startTime\": \"2023-12-01T00:00:00.000+00:00\", \"endTime\": \"2023-12-02T00:00:00.000+00:00\", \"categoryId\": 1}"))
                 .andExpect(status().isNotFound());
     }

     @DisplayName("전체 찾기 - 성공")
     @Test
     void testFindAll() throws Exception {
         when(scheduleService.getAllSchedules()).thenReturn(Collections.emptyList());

         mockMvc.perform(MockMvcRequestBuilders.get("/api/schedule/findAll")
                         .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(status().isOk());
     }

     @DisplayName("category별 schedule 찾기 - 성공")
     @Test
     void testFindByCategory() throws Exception {
         when(scheduleService.getSchedulesByCategory(anyLong())).thenReturn(Collections.emptyList());

         mockMvc.perform(MockMvcRequestBuilders.get("/api/schedule/find/category/1")
                         .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(status().isOk());
     }

     @DisplayName("scheduleId로 schedule 찾기 - 성공")
     @Test
     void testFindById() throws Exception {
         ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
         scheduleResponseDTO.setDeleteYn(false);

         when(scheduleService.getSchedule(anyLong())).thenReturn(scheduleResponseDTO);

         mockMvc.perform(MockMvcRequestBuilders.get("/api/schedule/find/1")
                         .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(status().isOk());
     }

     @DisplayName("scheduleId로 schedule 찾기 - 실패")
     @Test
     void testFindByIdNotFound() throws Exception {
         when(scheduleService.getSchedule(anyLong())).thenThrow(new IllegalArgumentException("Schedule not found"));

         mockMvc.perform(MockMvcRequestBuilders.get("/api/schedule/find/999")
                         .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(status().isNotFound());
     }

     @DisplayName("schedule 삭제 - 성공")
     @Test
     void testDeleteSchedule() throws Exception {
         mockMvc.perform(MockMvcRequestBuilders.put("/api/schedule/delete/1")
                         .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(status().isOk());

         verify(scheduleService, times(1)).deleteSchedule(anyLong());
     }

     @DisplayName("schedule 삭제 - 실패")
     @Test
     void testDeleteScheduleNotFound() throws Exception {
         doThrow(new IllegalArgumentException("Schedule not found")).when(scheduleService).deleteSchedule(anyLong());

         mockMvc.perform(MockMvcRequestBuilders.put("/api/schedule/delete/100"))
                 .andExpect(status().isNotFound());
     }

     @DisplayName("그룹 탈퇴 시 schedule 삭제 - 성공")
     @Test
     void testdeleteGroupSchedule() throws Exception {
         Group test = Group.builder().groupId(1L).groupName("test").createdAt(null).deleteYn(false).build();
         Category groupCategory = Category.builder().categoryNo(1L)
                 .categoryId(3L).group(test).dept(null).build();
         Schedule build = Schedule.builder()
                 .title("test").context("test").status(0)
                 .notificationYn(false).category(groupCategory).deleteYn(false)
                 .createdAt(null).startTime(null).endTime(null).build();

         mockMvc.perform(MockMvcRequestBuilders.put("/api/schedule/delete/groupSchedule/1")
                         .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(status().isOk());

         verify(scheduleService, times(1)).deleteGroupSchedule(anyLong());
     }

     @DisplayName("그룹 탈퇴 시 schedule 삭제 - 실패")
     @Test
     void testdeleteGroupScheduleNotFound() throws Exception {
         String errorMessage = "Group not found";
         doThrow(new IllegalArgumentException(errorMessage)).when(scheduleService).deleteGroupSchedule(anyLong());


         mockMvc.perform(MockMvcRequestBuilders.put("/api/schedule/delete/groupSchedule/1000000"))
                 .andExpect(status().isNotFound());
     }
 }
