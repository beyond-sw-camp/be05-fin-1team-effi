package com.example.effi.controller;

import com.example.effi.domain.DTO.ParticipantResponseDTO;
import com.example.effi.domain.Entity.Participant;
import com.example.effi.repository.ParticipantRepository;
import com.example.effi.service.EmployeeService;
import com.example.effi.service.GroupService;
import com.example.effi.service.ParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ParticipantControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ParticipantController participantController;

    @MockBean
    private ParticipantService participantService;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private GroupService groupService;
    @Autowired
    private ParticipantRepository participantRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(participantController).build();
    }

    @DisplayName("Add Participant Test")
    @Test
    public void addParticipantTest() throws Exception {
        when(participantService.addParticipant(anyLong(), anyLong())).thenReturn(new ParticipantResponseDTO());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/participant/add")
                        .param("scheduleId", "1")
                        .param("empId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Add Participant Test - Fail")
    @Test
    public void addParticipantTestFail() throws Exception {
        when(participantService.addParticipant(anyLong(), anyLong()))
                .thenThrow(new IllegalArgumentException("Failed to add participant"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/participant/add")
                        .param("scheduleId", "198989898")
                        .param("empId", "2343")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @DisplayName("Add Participant by Department Test")
    @Test
    public void addParticipantDeptTest() throws Exception {
        when(employeeService.findEmpIdByDept(anyLong())).thenReturn(List.of(1L, 2L));
        when(participantService.addParticipant(anyLong(), anyLong())).thenReturn(new ParticipantResponseDTO());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/participant/add/dept/1")
                        .param("scheduleId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Add Participant by Department Test - Failure") //
    @Test
    public void addParticipantDeptTestFailure() throws Exception {
        when(employeeService.findEmpIdByDept(anyLong())).thenThrow(new RuntimeException("Failed to find employees"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/participant/add/dept/1")
                        .param("scheduleId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @DisplayName("Add Participant by Group Test")
    @Test
    public void addParticipantGroupTest() throws Exception {
        when(groupService.findEmployeeIdsByGroupId(anyLong())).thenReturn(List.of(1L, 2L));
        when(participantService.addParticipant(anyLong(), anyLong())).thenReturn(new ParticipantResponseDTO());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/participant/add/group/1")
                        .param("scheduleId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Add Participant by Group Test - Failure")
    @Test
    public void addParticipantGroupTestFailure() throws Exception {
        when(groupService.findEmployeeIdsByGroupId(anyLong())).thenThrow(new RuntimeException("Failed to find group members"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/participant/add/group/1")
                        .param("scheduleId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @DisplayName("Find Participant by Participant ID Test")
    @Test
    public void findByParticipantIdTest() throws Exception {
        ParticipantResponseDTO participantResponseDTO = new ParticipantResponseDTO();
        participantResponseDTO.setEmpId(1L);
        participantResponseDTO.setScheduleId(1L);
        participantResponseDTO.setDeleteYn(false);

        when(participantService.findByParticipantId(anyLong())).thenReturn(participantResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/participant/find/participant/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Find Participant by Participant ID Test - Failure")
    @Test
    public void findByParticipantIdTestFailure() throws Exception {
        when(participantService.findByParticipantId(anyLong())).thenThrow(new RuntimeException("Participant not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/participant/find/participant/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("Find Participant by Schedule ID Test")
    @Test
    public void findByScheduleIdTest() throws Exception {
        when(participantService.findAllByScheduleId(anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/participant/find/schedule/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Find Participant by Schedule ID Test - Failure")
    @Test
    public void findByScheduleIdTestFailure() throws Exception {
        when(participantService.findAllByScheduleId(anyLong())).thenThrow(new RuntimeException("Schedule not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/participant/find/schedule/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("Find Participant by Employee ID Test")
    @Test
    public void findByEmpIdTest() throws Exception {
        when(participantService.findAllByEmpId(anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/participant/find/emp/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Find Participant by Employee ID Test - Failure")
    @Test
    public void findByEmpIdTestFailure() throws Exception {
        when(participantService.findAllByEmpId(anyLong())).thenThrow(new NotFoundException("Employee not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/participant/find/emp/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("Delete Participant Test")
    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/participant/delete/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("Delete Participant Test - Failure")
    @Test
    public void deleteTestFailure() throws Exception {
        when(participantService.delete(anyLong())).thenThrow(new NotFoundException("Failed to delete participant"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/participant/delete/1"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
