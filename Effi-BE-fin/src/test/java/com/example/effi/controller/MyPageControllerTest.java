package com.example.effi.controller;

import com.example.effi.domain.DTO.MyPageResponseDTO;
import com.example.effi.domain.DTO.MyPageUpdateDTO;
import com.example.effi.service.MyPageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MyPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MyPageService myPageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원 정보 조회 - 성공")
    void mypageView_ShouldReturnMyPageResponseDTO() throws Exception {
        MyPageResponseDTO responseDTO = MyPageResponseDTO.builder()
                .empNo(1L)
                .name("John Doe")
                .company("Example Corp")
                .email("john.doe@example.com")
                .rank("Manager")
                .phoneNum("123-456-7890")
                .extensionNum("1234")
                .deptName("Sales")
                .timezoneName("UTC")
                .msg("회원 정보 조회")
                .build();

        when(myPageService.getEmployee()).thenReturn(responseDTO);

        mockMvc.perform(get("/api/mypage/me")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        "{\"empNo\":1,\"name\":\"John Doe\",\"company\":\"Example Corp\",\"email\":\"john.doe@example.com\",\"rank\":\"Manager\",\"phoneNum\":\"123-456-7890\",\"extensionNum\":\"1234\",\"deptName\":\"Sales\",\"timezoneName\":\"UTC\",\"msg\":\"회원 정보 조회\"}"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 조회 - 실패: 직원 정보 없음")
    void mypageView_ShouldReturnNotFound_WhenEmployeeNotFound() throws Exception {
        when(myPageService.getEmployee()).thenThrow(new IllegalArgumentException("Employee not found"));

        mockMvc.perform(get("/api/mypage/me")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""))
                .andDo(print());
    }

    @Test
    @DisplayName("타임존 업데이트 - 성공")
    void updateTimezone_ShouldReturnSuccessMessage() throws Exception {
        MyPageUpdateDTO updateDTO = MyPageUpdateDTO.builder()
                .empId(1L)
                .timezoneId(2L)
                .build();

        mockMvc.perform(put("/api/mypage/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"empId\":1,\"timezoneId\":2}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Timezone updated successfully"))
                .andDo(print());
    }

    @Test
    void updateTimezone_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        doThrow(new IllegalArgumentException("Invalid input")).when(myPageService)
                .updateEmployeeTimezone(any(MyPageUpdateDTO.class));

        mockMvc.perform(put("/api/mypage/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"empId\":1,\"timezoneId\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid input"))
                .andDo(print());
    }


}
