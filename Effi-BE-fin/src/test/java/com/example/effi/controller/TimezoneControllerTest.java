package com.example.effi.controller;

import com.example.effi.domain.DTO.GlobalResponse;
import com.example.effi.domain.DTO.TimezoneDTO;
import com.example.effi.service.TimezoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
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
  @DisplayName("새로운 타임존 추가 테스트 - 성공")
  public void testAddTimezoneSuccess() throws Exception {
    TimezoneDTO timezoneDTO = new TimezoneDTO(1L, "UTC", "US", "UTC", 0L, 0, "N/A");
    given(timezoneService.addTimezone(any(TimezoneDTO.class)))
        .willReturn(GlobalResponse.<TimezoneDTO>builder()
            .status(200)
            .message("새로운 타임존을 추가했습니다")
            .data(timezoneDTO)
            .build());

    mockMvc.perform(post("/api/timezones")
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            "{\"timezoneId\":1,\"timezoneName\":\"UTC\",\"countryCode\":\"US\",\"abbreviation\":\"UTC\",\"timeStart\":0,\"gmtOffset\":0,\"dst\":\"N/A\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is(200)))
        .andExpect(jsonPath("$.message", is("새로운 타임존을 추가했습니다")))
        .andExpect(jsonPath("$.data.timezoneId", is(1)))
        .andExpect(jsonPath("$.data.timezoneName", is("UTC")));
  }

  @Test
  @DisplayName("새로운 타임존 추가 테스트 - 실패: 중복된 타임존을 추가할 경우")
  public void testAddTimezoneFail() throws Exception {
    given(timezoneService.addTimezone(any(TimezoneDTO.class)))
        .willReturn(GlobalResponse.<TimezoneDTO>builder()
            .status(400)
            .message("중복된 타임존을 추가할 수 없습니다")
            .build());

    mockMvc.perform(post("/api/timezones")
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            "{\"timezoneId\":1,\"timezoneName\":\"UTC\",\"countryCode\":\"US\",\"abbreviation\":\"UTC\",\"timeStart\":0,\"gmtOffset\":0,\"dst\":\"N/A\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message", is("중복된 타임존을 추가할 수 없습니다")));
  }

  @Test
  @DisplayName("모든 타임존 조회 테스트 - 성공")
  public void testGetAllTimezonesSuccess() throws Exception {
    TimezoneDTO timezone1 = new TimezoneDTO(1L, "UTC", "US", "UTC", 0L, 0, "N/A");
    TimezoneDTO timezone2 = new TimezoneDTO(2L, "PST", "US", "PST", 0L, -28800, "N/A");
    List<TimezoneDTO> timezones = Arrays.asList(timezone1, timezone2);

    given(timezoneService.getAllTimezones())
        .willReturn(GlobalResponse.<List<TimezoneDTO>>builder()
            .status(200)
            .message("모든 타임존을 조회했습니다")
            .data(timezones)
            .build());

    mockMvc.perform(get("/api/timezones")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is(200)))
        .andExpect(jsonPath("$.message", is("모든 타임존을 조회했습니다")))
        .andExpect(jsonPath("$.data[0].timezoneId", is(1)))
        .andExpect(jsonPath("$.data[1].timezoneId", is(2)));
  }

  @Test
  @DisplayName("모든 타임존 조회 테스트 - 실패: 타임존이 존재하지 않을 경우")
  void testGetAllTimezonesFail() throws Exception {
    given(timezoneService.getAllTimezones())
        .willReturn(GlobalResponse.<List<TimezoneDTO>>builder()
            .status(404)
            .message("타임존이 존재하지 않습니다")
            .data(Collections.emptyList())
            .build());

    mockMvc.perform(get("/api/timezones")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("타임존이 존재하지 않습니다")))
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  @DisplayName("ID로 타임존 조회 테스트 - 성공")
  public void testGetTimezoneByIdSuccess() throws Exception {
    TimezoneDTO timezoneDTO = new TimezoneDTO(1L, "UTC", "US", "UTC", 0L, 0, "N/A");
    given(timezoneService.getTimezoneById(anyLong()))
        .willReturn(GlobalResponse.<TimezoneDTO>builder()
            .status(200)
            .message("ID로 타임존을 조회했습니다")
            .data(timezoneDTO)
            .build());

    mockMvc.perform(get("/api/timezones/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is(200)))
        .andExpect(jsonPath("$.message", is("ID로 타임존을 조회했습니다")))
        .andExpect(jsonPath("$.data.timezoneId", is(1)))
        .andExpect(jsonPath("$.data.timezoneName", is("UTC")));
  }

  @Test
  @DisplayName("ID로 타임존 조회 테스트 - 실패: 타임존이 존재하지 않을 경우")
  public void testGetTimezoneByIdFail() throws Exception {
    given(timezoneService.getTimezoneById(anyLong()))
        .willReturn(GlobalResponse.<TimezoneDTO>builder()
            .status(404)
            .message("해당 ID의 타임존을 찾을 수 없습니다")
            .build());

    mockMvc.perform(get("/api/timezones/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("해당 ID의 타임존을 찾을 수 없습니다")));
  }

  @Test
  @DisplayName("타임존 업데이트 테스트 - 성공")
  public void testUpdateTimezoneSuccess() throws Exception {
    TimezoneDTO timezoneDTO = new TimezoneDTO(1L, "UTC", "US", "UTC", 0L, 0, "N/A");
    given(timezoneService.updateTimezone(anyLong(), any(TimezoneDTO.class)))
        .willReturn(GlobalResponse.<TimezoneDTO>builder()
            .status(200)
            .message("타임존을 업데이트했습니다")
            .data(timezoneDTO)
            .build());

    mockMvc.perform(put("/api/timezones/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            "{\"timezoneId\":1,\"timezoneName\":\"UTC\",\"countryCode\":\"US\",\"abbreviation\":\"UTC\",\"timeStart\":0,\"gmtOffset\":0,\"dst\":\"N/A\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is(200)))
        .andExpect(jsonPath("$.message", is("타임존을 업데이트했습니다")))
        .andExpect(jsonPath("$.data.timezoneId", is(1)))
        .andExpect(jsonPath("$.data.timezoneName", is("UTC")));
  }

  @Test
  @DisplayName("타임존 업데이트 테스트 - 실패: 타임존이 존재하지 않을 경우")
  public void testUpdateTimezoneFail() throws Exception {
    given(timezoneService.updateTimezone(anyLong(), any(TimezoneDTO.class)))
        .willReturn(GlobalResponse.<TimezoneDTO>builder()
            .status(404)
            .message("해당 ID의 타임존을 찾을 수 없습니다")
            .build());

    mockMvc.perform(put("/api/timezones/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            "{\"timezoneId\":1,\"timezoneName\":\"UTC\",\"countryCode\":\"US\",\"abbreviation\":\"UTC\",\"timeStart\":0,\"gmtOffset\":0,\"dst\":\"N/A\"}"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("해당 ID의 타임존을 찾을 수 없습니다")));
  }

  @Test
  @DisplayName("타임존 삭제 테스트 - 성공")
  public void testDeleteTimezoneSuccess() throws Exception {
    given(timezoneService.deleteTimezone(anyLong()))
        .willReturn(GlobalResponse.<TimezoneDTO>builder() // 변경
            .status(204)
            .message("타임존을 삭제했습니다")
            .build());

    mockMvc.perform(delete("/api/timezones/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("타임존 삭제 테스트 - 실패: 타임존이 존재하지 않을 경우")
  public void testDeleteTimezoneFail() throws Exception {
    given(timezoneService.deleteTimezone(anyLong()))
        .willReturn(GlobalResponse.<TimezoneDTO>builder() // 변경
            .status(404)
            .message("해당 ID의 타임존을 찾을 수 없습니다")
            .build());

    mockMvc.perform(delete("/api/timezones/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("해당 ID의 타임존을 찾을 수 없습니다")));
  }

}