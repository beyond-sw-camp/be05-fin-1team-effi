package com.example.effi.controller;

import com.example.effi.domain.Dto.Schedule.ScheduleRequestDTO;
import com.example.effi.domain.Dto.Schedule.ScheduleResponseDTO;
import com.example.effi.service.EmployeeService;
import com.example.effi.service.ParticipantService;
import com.example.effi.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ScheduleControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private ParticipantService participantService;

    @InjectMocks
    private ScheduleController scheduleController;

    @BeforeEach
    void setUp() {
        employeeService = mock(EmployeeService.class); // Mock 객체 생성
        participantService = mock(ParticipantService.class); // Mock 객체 생성
        scheduleController = new ScheduleController(scheduleService, employeeService, participantService);
        mockMvc = MockMvcBuilders.standaloneSetup(scheduleController).build();
    }

    @Test
    void testAddSchedule() throws Exception {
        ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
        scheduleResponseDTO.setScheduleId(1L);

        when(scheduleService.addSchedule(any(ScheduleRequestDTO.class))).thenReturn(scheduleResponseDTO);
        when(employeeService.findEmpIdByEmpNo(anyLong())).thenReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test\", \"context\": \"Test context\", \"startTime\": \"2023-12-01T00:00:00.000+00:00\", \"endTime\": \"2023-12-02T00:00:00.000+00:00\", \"categoryId\": 1}")
                        .param("empNo", "123"))
                .andExpect(status().isOk());

        verify(participantService, times(1)).addParticipant(eq(1L), eq(1L));
//        verify(scheduleService, times(1)).addRoutineSchedule(any());
    }

    @Test
    void testUpdateSchedule() throws Exception {
        ScheduleRequestDTO scheduleRequestDTO = new ScheduleRequestDTO();
        ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
//        scheduleResponseDTO.setScheduleId(1L);

        when(scheduleService.updateSchedule(any(ScheduleRequestDTO.class), anyLong())).thenReturn(scheduleResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/schedule/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test\", \"context\": \"Test context\", \"startTime\": \"2023-12-01T00:00:00.000+00:00\", \"endTime\": \"2023-12-02T00:00:00.000+00:00\", \"categoryId\": 1}"))
                .andExpect(status().isOk());

//        verify(scheduleService, times(1)).addRoutineSchedule(any());
    }

    @Test
    void testFindAll() throws Exception {
        when(scheduleService.getAllSchedules()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/schedule/findAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testFindByCategory() throws Exception {
        when(scheduleService.getSchedulesByCategory(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/schedule/find/category/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testFindById() throws Exception {
        ScheduleResponseDTO scheduleResponseDTO = new ScheduleResponseDTO();
        scheduleResponseDTO.setDeleteYn(false);

        when(scheduleService.getSchedule(anyLong())).thenReturn(scheduleResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/schedule/find/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteSchedule() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/schedule/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(scheduleService, times(1)).deleteSchedule(anyLong());
    }
}
