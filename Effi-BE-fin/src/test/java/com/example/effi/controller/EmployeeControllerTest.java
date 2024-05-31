package com.example.effi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.example.effi.domain.DTO.SignInRequest;
import com.example.effi.domain.DTO.SignInResponse;
import com.example.effi.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private EmployeeController employeeController;

    @DisplayName("로그인 테스트 : 성공")
    @Test
    public void loginTest() throws Exception {
        // given
        SignInRequest request = SignInRequest.builder()
            .empNo(1L)
            .password("1234")
            .build();

        SignInResponse response = SignInResponse.builder()
            .empNo(1L)
            .name("John Doe")
            .rank("사원")
            .msg("로그인 성공")
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .build();

        // when
        when(employeeService.signIn(any(SignInRequest.class))).thenReturn(response);

        // then
        mockMvc.perform(post("/api/auth/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("로그인 성공"))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
            .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"));
    }

    @DisplayName("로그인 테스트 : 실패")
    @Test
    public void loginFailTest() throws Exception {
        // given
        SignInRequest request = SignInRequest.builder()
            .empNo(1L)
            .password("1234")
            .build();

        SignInResponse response = SignInResponse.builder()
            .msg("로그인 실패")
            .build();

        // when
        when(employeeService.signIn(any(SignInRequest.class))).thenReturn(response);

        // then
        mockMvc.perform(post("/api/auth/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("로그인 실패"))
            .andExpect(jsonPath("$.status").value(400));
    }

}