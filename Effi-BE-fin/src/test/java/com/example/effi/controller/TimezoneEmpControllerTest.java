package com.example.effi.controller;

import com.example.effi.domain.Dto.Timezone.TimezoneDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
    @DisplayName("직원에게 타임존 추가 테스트")
    public void testAddTimezoneForEmployee() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "타임존 추가에 성공했습니다");
        Map<String, Object> data = new HashMap<>();
        data.put("empId", 1L);
        data.put("timezoneId", 1L);
        response.put("data", data);

        Mockito.doNothing().when(timezoneEmpService).addTimezoneForEmployee(anyLong(), anyLong(), eq(false));

        mockMvc.perform(post("/api/timezone-emp/1/add")
                .param("timezoneId", "1")
                .param("isDefault", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("200")))
                .andExpect(jsonPath("$.message", is("타임존 추가에 성공했습니다")))
                .andExpect(jsonPath("$.data.empId", is(1)))
                .andExpect(jsonPath("$.data.timezoneId", is(1)));
    }

    @Test
    @DisplayName("직원의 모든 타임존 조회 테스트")
    public void testGetTimezonesForEmployee() throws Exception {
        TimezoneDTO timezone1 = new TimezoneDTO(1L, "UTC", "US", "UTC", 0L, 0, "N/A");
        TimezoneDTO timezone2 = new TimezoneDTO(2L, "PST", "US", "PST", 0L, -28800, "N/A");
        List<TimezoneDTO> timezones = Arrays.asList(timezone1, timezone2);

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "타임존 목록 조회에 성공했습니다");
        Map<String, Object> data = new HashMap<>();
        data.put("timezones", timezones);
        response.put("data", data);

        given(timezoneEmpService.getTimezonesForEmployee(anyLong())).willReturn(timezones);

        mockMvc.perform(get("/api/timezone-emp/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("200")))
                .andExpect(jsonPath("$.message", is("타임존 목록 조회에 성공했습니다")))
                .andExpect(jsonPath("$.data.timezones[0].timezoneId", is(1)))
                .andExpect(jsonPath("$.data.timezones[1].timezoneId", is(2)));
    }

    @Test
    @DisplayName("직원의 기본 타임존 업데이트 테스트")
    public void testUpdateDefaultTimezoneForEmployee() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "기본 타임존 업데이트에 성공했습니다");
        Map<String, Object> data = new HashMap<>();
        data.put("empId", 1L);
        data.put("newTimezoneId", 2L);
        response.put("data", data);

        Mockito.doNothing().when(timezoneEmpService).updateDefaultTimezoneForEmployee(anyLong(), anyLong());

        mockMvc.perform(put("/api/timezone-emp/1/update-default")
                .param("newTimezoneId", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("200")))
                .andExpect(jsonPath("$.message", is("기본 타임존 업데이트에 성공했습니다")))
                .andExpect(jsonPath("$.data.empId", is(1)))
                .andExpect(jsonPath("$.data.newTimezoneId", is(2)));
    }

    @Test
    @DisplayName("직원에게서 타임존 삭제 테스트")
    public void testRemoveTimezoneForEmployee() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "타임존 삭제에 성공했습니다");
        Map<String, Object> data = new HashMap<>();
        data.put("empId", 1L);
        data.put("timezoneId", 1L);
        response.put("data", data);

        Mockito.doNothing().when(timezoneEmpService).removeTimezoneForEmployee(anyLong(), anyLong());

        mockMvc.perform(delete("/api/timezone-emp/1/remove")
                .param("timezoneId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("200")))
                .andExpect(jsonPath("$.message", is("타임존 삭제에 성공했습니다")))
                .andExpect(jsonPath("$.data.empId", is(1)))
                .andExpect(jsonPath("$.data.timezoneId", is(1)));
    }
}
