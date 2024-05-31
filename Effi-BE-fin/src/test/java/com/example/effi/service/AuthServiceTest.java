package com.example.effi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.effi.config.TokenProvider;
import com.example.effi.domain.DTO.SignInRequest;
import com.example.effi.domain.DTO.SignInResponse;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.RefreshToken;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.RefreshTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

public class AuthServiceTest {
    
    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("사원 로그인 테스트 : 성공")
    @Test
    public void signInTest() {
        // given
        SignInRequest request = SignInRequest.builder()
                .empNo(1L)
                .password("1234")
                .build();
        Employee employee = Employee.builder()
                .id(1L)
                .empNo(1L)
                .password("1234")
                .rank("사원")
                .build();
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenId(1L)
                .refreshToken("refreshToken")
                .employee(employee)
                .build();
        // when
        when(employeeRepository.findByEmpNo(request.getEmpNo())).thenReturn(employee);
        when(passwordEncoder.matches(request.getPassword(), employee.getPassword())).thenReturn(true);
        when(tokenProvider.createToken(String.format("%s:%s", employee.getEmpNo(), employee.getRank()))).thenReturn("accessToken");
        when(tokenProvider.createRefreshToken()).thenReturn("refreshToken");
        when(refreshTokenRepository.findById(employee.getId())).thenReturn(Optional.of(refreshToken));
        // then
        SignInResponse response = employeeService.signIn(request);
        assertEquals(response.getAccessToken(), "accessToken");
        assertEquals(response.getRefreshToken(), "refreshToken");
    }

    @DisplayName("사원 로그인 테스트 : 실패 - 사원번호가 존재하지 않음")
    @Test
    public void signInTestFailEmpNo() {
        // given
        SignInRequest request = SignInRequest.builder()
                .empNo(1L)
                .password("1234")
                .build();
        // when
        when(employeeRepository.findByEmpNo(request.getEmpNo())).thenReturn(null);
        // then
        SignInResponse response = employeeService.signIn(request);
        assertEquals(response.getMsg(), "사원번호가 존재하지 않습니다.");
    }

    @DisplayName("사원 로그인 테스트 : 실패 - 비밀번호가 일치하지 않음")
    @Test
    public void signInTestFailPassword() {
        // given
        SignInRequest request = SignInRequest.builder()
                .empNo(1L)
                .password("12345")
                .build();
        Employee employee = Employee.builder()
                .id(1L)
                .empNo(1L)
                .password("1234")
                .rank("사원")
                .build();
        // when
        when(employeeRepository.findByEmpNo(request.getEmpNo())).thenReturn(employee);
        when(passwordEncoder.matches(request.getPassword(), employee.getPassword())).thenReturn(false);
        // then
        SignInResponse response = employeeService.signIn(request);
        assertEquals(response.getMsg(), "비밀번호가 일치하지 않습니다.");
    }

    @DisplayName("사원 로그아웃 테스트 : 성공")
    @Test
    public void signOutTest() throws JsonProcessingException {
        // given
        String token = "validToken";
        String subject = "1:사원";
        Long empNo = 1L;
        Employee employee = Employee.builder()
                .id(1L)
                .empNo(empNo)
                .rank("사원")
                .build();
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenId(1L)
                .refreshToken("refreshToken")
                .employee(employee)
                .build();

        // when
        when(tokenProvider.decodeJwtPayloadSubject(token)).thenReturn(subject);
        when(employeeRepository.findByEmpNo(empNo)).thenReturn(employee);
        when(refreshTokenRepository.findById(employee.getId())).thenReturn(Optional.of(refreshToken));

        // then
        String response = employeeService.signOut(token);
        assertEquals(response, "로그아웃 성공");
    }

    @DisplayName("사원 로그아웃 테스트 : 실패")
    @Test
    public void signOutTestFail() throws JsonProcessingException {
        // given
        String token = "invalidToken";
        String subject = "1:사원";
        Long empNo = 1L;
        Employee employee = Employee.builder()
                .id(1L)
                .empNo(empNo)
                .rank("사원")
                .build();

        // when
        when(tokenProvider.decodeJwtPayloadSubject(token)).thenReturn(subject);
        when(employeeRepository.findByEmpNo(empNo)).thenReturn(employee);
        when(refreshTokenRepository.findById(employee.getId())).thenReturn(Optional.empty());

        // then
        String response = employeeService.signOut(token);
        assertEquals(response, "로그아웃 실패");
    }
}
