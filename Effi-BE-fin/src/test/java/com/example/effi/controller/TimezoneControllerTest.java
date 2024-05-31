package com.example.effi.controller;

import com.example.effi.domain.DTO.TimezoneDTO;
import com.example.effi.service.TimezoneService;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TimezoneControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TimezoneService timezoneService;

    @InjectMocks
    private TimezoneController timezoneController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(timezoneController).build();
    }

    @Test
    @DisplayName("새로운 타임존 추가 테스트")
    public void testAddTimezone() throws Exception {
        TimezoneDTO timezoneDTO = new TimezoneDTO(1L, "UTC", "US", "UTC", 0L, 0, "N/A");
        given(timezoneService.addTimezone(any(TimezoneDTO.class))).willReturn(timezoneDTO);

        mockMvc.perform(post("/api/timezones")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"timezoneId\":1,\"timezoneName\":\"UTC\",\"countryCode\":\"US\",\"abbreviation\":\"UTC\",\"timeStart\":0,\"gmtOffset\":0,\"dst\":\"N/A\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timezoneId", is(1)))
                .andExpect(jsonPath("$.timezoneName", is("UTC")));
    }

    @Test
    @DisplayName("모든 타임존 조회 테스트")
    public void testGetAllTimezones() throws Exception {
        TimezoneDTO timezone1 = new TimezoneDTO(1L, "UTC", "US", "UTC", 0L, 0, "N/A");
        TimezoneDTO timezone2 = new TimezoneDTO(2L, "PST", "US", "PST", 0L, -28800, "N/A");
        List<TimezoneDTO> timezones = Arrays.asList(timezone1, timezone2);
        given(timezoneService.getAllTimezones()).willReturn(timezones);

        mockMvc.perform(get("/api/timezones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].timezoneId", is(1)))
                .andExpect(jsonPath("$[1].timezoneId", is(2)));
    }

    @Test
    @DisplayName("ID로 타임존 조회 테스트")
    public void testGetTimezoneById() throws Exception {
        TimezoneDTO timezoneDTO = new TimezoneDTO(1L, "UTC", "US", "UTC", 0L, 0, "N/A");
        given(timezoneService.getTimezoneById(anyLong())).willReturn(timezoneDTO);

        mockMvc.perform(get("/api/timezones/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timezoneId", is(1)))
                .andExpect(jsonPath("$.timezoneName", is("UTC")));
    }

    @Test
    @DisplayName("타임존 업데이트 테스트")
    public void testUpdateTimezone() throws Exception {
        TimezoneDTO timezoneDTO = new TimezoneDTO(1L, "UTC", "US", "UTC", 0L, 0, "N/A");
        given(timezoneService.updateTimezone(anyLong(), any(TimezoneDTO.class))).willReturn(timezoneDTO);

        mockMvc.perform(put("/api/timezones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"timezoneId\":1,\"timezoneName\":\"UTC\",\"countryCode\":\"US\",\"abbreviation\":\"UTC\",\"timeStart\":0,\"gmtOffset\":0,\"dst\":\"N/A\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timezoneId", is(1)))
                .andExpect(jsonPath("$.timezoneName", is("UTC")));
    }

    @Test
    @DisplayName("타임존 삭제 테스트")
    public void testDeleteTimezone() throws Exception {
        Mockito.doNothing().when(timezoneService).deleteTimezone(anyLong());

        mockMvc.perform(delete("/api/timezones/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
