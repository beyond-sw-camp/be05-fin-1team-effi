// package com.example.effi.controller;


// import com.example.effi.domain.DTO.ScheduleRequestDTO;
// import com.example.effi.domain.DTO.ScheduleResponseDTO;
// import com.example.effi.repository.EmployeeRepository;
// import com.example.effi.repository.GroupEmpRepository;
// import com.example.effi.repository.GroupRepository;
// import com.example.effi.repository.ParticipantRepository;
// import com.example.effi.service.EmployeeService;
// import com.example.effi.service.GroupService;
// import com.example.effi.service.ParticipantService;
// import com.example.effi.service.ScheduleService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.web.server.ResponseStatusException;

// import java.util.Collections;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest
// public class ScheduleControllerTest {

//     private MockMvc mockMvc;

//     @MockBean
//     private ScheduleService scheduleService;

//     @MockBean
//     private EmployeeService employeeService;

//     @MockBean
//     private ParticipantService participantService;

//     @InjectMocks
//     private ScheduleController scheduleController;
//     @Autowired
//     private EmployeeRepository employeeRepository;
//     @Autowired
//     private GroupService groupService;
//     @Autowired
//     private GroupEmpRepository groupEmpRepository;

//     @BeforeEach
//     void setUp() {
//         employeeService = mock(EmployeeService.class);
//         participantService = mock(ParticipantService.class);
//         scheduleController = new ScheduleController(scheduleService, employeeService, participantService,employeeRepository, groupService, groupEmpRepository);
//         mockMvc = MockMvcBuilders.standaloneSetup(scheduleController).build();
//     }

//     @Test
//     void testAddSchedule() throws Exception {
//         ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
//         scheduleResponseDTO.setScheduleId(1L);

//         when(scheduleService.addSchedule(any(ScheduleRequestDTO.class))).thenReturn(scheduleResponseDTO);
//         when(employeeService.findEmpIdByEmpNo(anyLong())).thenReturn(1L);

//         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/add")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content("{\"title\": \"Test\", \"context\": \"Test context\", \"startTime\": \"2023-12-01T00:00:00.000+00:00\", \"endTime\": \"2023-12-02T00:00:00.000+00:00\", \"categoryId\": 1}")
//                         .param("empNo", "123"))
//                 .andExpect(status().isOk());

//         verify(participantService, times(1)).addParticipant(eq(1L), eq(1L));
//     }

//     @Test
//     void testAddScheduleFailure() throws Exception {
//         when(scheduleService.addSchedule(any(ScheduleRequestDTO.class))).thenThrow(new RuntimeException("Service exception"));

//         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/add")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content("{\"title\": \"Test\", \"context\": \"Test context\", \"startTime\": \"2023-12-01T00:00:00.000+00:00\", \"endTime\": \"2023-12-02T00:00:00.000+00:00\", \"categoryId\": 1}")
//                         .param("empNo", "123"))
//                 .andExpect(status().isInternalServerError());
//     }

//     @Test
//     void testUpdateSchedule() throws Exception {
//         ScheduleRequestDTO scheduleRequestDTO = new ScheduleRequestDTO();
//         ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();

//         when(scheduleService.updateSchedule(any(ScheduleRequestDTO.class), anyLong())).thenReturn(scheduleResponseDTO);

//         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/update/1")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content("{\"title\": \"Test\", \"context\": \"Test context\", \"startTime\": \"2023-12-01T00:00:00.000+00:00\", \"endTime\": \"2023-12-02T00:00:00.000+00:00\", \"categoryId\": 1}"))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     void testUpdateScheduleNotFound() throws Exception {
//         when(scheduleService.updateSchedule(any(ScheduleRequestDTO.class), anyLong()))
//                 .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

//         mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/update/999")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content("{\"title\": \"Test\", \"context\": \"Test context\", \"startTime\": \"2023-12-01T00:00:00.000+00:00\", \"endTime\": \"2023-12-02T00:00:00.000+00:00\", \"categoryId\": 1}"))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     void testFindAll() throws Exception {
//         when(scheduleService.getAllSchedules()).thenReturn(Collections.emptyList());

//         mockMvc.perform(MockMvcRequestBuilders.get("/api/schedule/findAll")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     void testFindByCategory() throws Exception {
//         when(scheduleService.getSchedulesByCategory(anyLong())).thenReturn(Collections.emptyList());

//         mockMvc.perform(MockMvcRequestBuilders.get("/api/schedule/find/category/1")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     void testFindById() throws Exception {
//         ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
//         scheduleResponseDTO.setDeleteYn(false);

//         when(scheduleService.getSchedule(anyLong())).thenReturn(scheduleResponseDTO);

//         mockMvc.perform(MockMvcRequestBuilders.get("/api/schedule/find/1")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     void testFindByIdNotFound() throws Exception {
//         when(scheduleService.getSchedule(anyLong())).thenThrow(new IllegalArgumentException("Schedule not found"));

//         mockMvc.perform(MockMvcRequestBuilders.get("/api/schedule/find/999")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     void testDeleteSchedule() throws Exception {
//         mockMvc.perform(MockMvcRequestBuilders.put("/api/schedule/delete/1")
//                         .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk());

//         verify(scheduleService, times(1)).deleteSchedule(anyLong());
//     }

//     @Test
//     void testDeleteScheduleNotFound() throws Exception {
//         doThrow(new IllegalArgumentException("Schedule not found")).when(scheduleService).deleteSchedule(anyLong());

//         mockMvc.perform(MockMvcRequestBuilders.put("/api/schedule/delete/100"))
//                 .andExpect(status().isNotFound());
//     }
// }
