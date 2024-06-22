package com.example.effi.controller;

import com.example.effi.domain.DTO.GlobalResponse;
import com.example.effi.domain.DTO.TimezoneDTO;
import com.example.effi.service.TimezoneEmpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TimezoneEmpControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TimezoneEmpService timezoneEmpService;

    @InjectMocks
    private TimezoneEmpController timezoneEmpController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(timezoneEmpController).build();
    }

    @Test
    @DisplayName("직원에게 타임존 추가 테스트 - 성공")
    public void testAddTimezoneForEmployee() throws Exception {
        GlobalResponse<Map<String, Object>> response = GlobalResponse.<Map<String, Object>>builder()
                .status(200)
                .message("타임존 추가에 성공했습니다")
                .data(Map.of("empId", 1L, "timezoneId", 1L))
                .build();

        Mockito.doNothing().when(timezoneEmpService).addTimezoneForEmployee(anyLong(), anyLong(), eq(false));

        mockMvc.perform(post("/api/timezone-emp/1/add")
                .param("timezoneId", "1")
                .param("isDefault", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("타임존 추가에 성공했습니다")))
                .andExpect(jsonPath("$.data.empId", is(1)))
                .andExpect(jsonPath("$.data.timezoneId", is(1)));
    }

    @Test
    @DisplayName("직원에게 타임존 추가 테스트 - 실패: 타임존이 이미 추가된 경우")
    public void testAddTimezoneForEmployeeFail() throws Exception {
      GlobalResponse<Map<String, Object>> response = GlobalResponse.<Map<String, Object>>builder()
          .status(400)
          .message("이미 추가된 타임존입니다")
          .data(Map.of("empId", 1L, "timezoneId", 1L))
          .build();

      Mockito.doThrow(new IllegalArgumentException("이미 추가된 타임존입니다")).when(timezoneEmpService)
          .addTimezoneForEmployee(anyLong(), anyLong(), eq(false));

      mockMvc.perform(post("/api/timezone-emp/1/add")
          .param("timezoneId", "1")
          .param("isDefault", "false")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", is(400)))
          .andExpect(jsonPath("$.message", is("이미 추가된 타임존입니다")))
          .andExpect(jsonPath("$.data").doesNotExist()); // Ensure that 'data' does not exist
    }

    @Test
    @DisplayName("직원의 모든 타임존 조회 테스트 - 성공")
    public void testGetTimezonesForEmployee() throws Exception {
        TimezoneDTO timezone1 = new TimezoneDTO(1L, "UTC", "US", "UTC",
                0L, 0, "N/A");
        TimezoneDTO timezone2 = new TimezoneDTO(2L, "PST", "US", "PST",
                0L, -28800, "N/A");
        List<TimezoneDTO> timezones = Arrays.asList(timezone1, timezone2);

        GlobalResponse<Map<String, List<TimezoneDTO>>> response = GlobalResponse
                .<Map<String, List<TimezoneDTO>>>builder()
                .status(200)
                .message("타임존 목록 조회에 성공했습니다")
                .data(Map.of("timezones", timezones))
                .build();

        given(timezoneEmpService.getTimezonesForEmployee(anyLong())).willReturn(timezones);

        mockMvc.perform(get("/api/timezone-emp/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("타임존 목록 조회에 성공했습니다")))
                .andExpect(jsonPath("$.data.timezones[0].timezoneId", is(1)))
                .andExpect(jsonPath("$.data.timezones[1].timezoneId", is(2)));
    }

    @Test
    @DisplayName("직원의 기본 타임존 업데이트 테스트 - 성공")
    public void testUpdateDefaultTimezoneForEmployee() throws Exception {
        GlobalResponse<Map<String, Object>> response = GlobalResponse.<Map<String, Object>>builder()
                .status(200)
                .message("기본 타임존 업데이트에 성공했습니다")
                .data(Map.of("empId", 1L, "newTimezoneId", 2L))
                .build();

        Mockito.doNothing().when(timezoneEmpService).updateDefaultTimezoneForEmployee(anyLong(), anyLong());

        mockMvc.perform(put("/api/timezone-emp/1/update-default")
                .param("newTimezoneId", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("기본 타임존 업데이트에 성공했습니다")))
                .andExpect(jsonPath("$.data.empId", is(1)))
                .andExpect(jsonPath("$.data.newTimezoneId", is(2)));
    }

    @Test
    @DisplayName("직원의 기본 타임존 업데이트 테스트 - 실패: 타임존이 존재하지 않을 경우")
    public void testUpdateDefaultTimezoneForEmployeeFail() throws Exception {
      GlobalResponse<Map<String, Object>> response = GlobalResponse.<Map<String, Object>>builder()
          .status(400)
          .message("타임존이 존재하지 않습니다")
          .data(Map.of("empId", 1L, "newTimezoneId", 2L))
          .build();

      Mockito.doThrow(new IllegalArgumentException("타임존이 존재하지 않습니다")).when(timezoneEmpService)
          .updateDefaultTimezoneForEmployee(anyLong(), anyLong());

      mockMvc.perform(put("/api/timezone-emp/1/update-default")
          .param("newTimezoneId", "2")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", is(400)))
          .andExpect(jsonPath("$.message", is("타임존이 존재하지 않습니다")))
          .andExpect(jsonPath("$.data").doesNotExist()); // Ensure that 'data' does not exist
    }


    @Test
    @DisplayName("직원에게서 타임존 삭제 테스트 - 성공")
    public void testRemoveTimezoneForEmployee() throws Exception {
        GlobalResponse<Map<String, Object>> response = GlobalResponse.<Map<String, Object>>builder()
                .status(200)
                .message("타임존 삭제에 성공했습니다")
                .data(Map.of("empId", 1L, "timezoneId", 1L))
                .build();

        Mockito.doNothing().when(timezoneEmpService).removeTimezoneForEmployee(anyLong(), anyLong());

        mockMvc.perform(delete("/api/timezone-emp/1/remove")
                .param("timezoneId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("타임존 삭제에 성공했습니다")))
                .andExpect(jsonPath("$.data.empId", is(1)))
                .andExpect(jsonPath("$.data.timezoneId", is(1)));
    }

    @Test
    @DisplayName("직원에게서 타임존 삭제 테스트 - 실패: 타임존이 존재하지 않을 경우")
    public void testRemoveTimezoneForEmployeeFail() throws Exception {
      GlobalResponse<Map<String, Object>> response = GlobalResponse.<Map<String, Object>>builder()
          .status(400)
          .message("타임존이 존재하지 않습니다")
          .data(Map.of("empId", 1L, "timezoneId", 1L))
          .build();

      Mockito.doThrow(new IllegalArgumentException("타임존이 존재하지 않습니다")).when(timezoneEmpService)
          .removeTimezoneForEmployee(anyLong(), anyLong());

      mockMvc.perform(delete("/api/timezone-emp/1/remove")
          .param("timezoneId", "1")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", is(400)))
          .andExpect(jsonPath("$.message", is("타임존이 존재하지 않습니다")))
          .andExpect(jsonPath("$.data").doesNotExist()); // Ensure that 'data' does not exist
    }
}
