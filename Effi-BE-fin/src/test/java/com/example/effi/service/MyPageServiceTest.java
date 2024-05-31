package com.example.effi.service;

import com.example.effi.domain.Dto.Mypage.MyPageResponseDTO;
import com.example.effi.domain.Dto.Mypage.MyPageUpdateDTO;
import com.example.effi.domain.Entity.Dept;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Timezone;
import com.example.effi.domain.Entity.TimezoneEmp;
import com.example.effi.repository.MyPageRepository;
import com.example.effi.repository.TimezoneEmpRepository;
import com.example.effi.repository.TimezoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class MyPageServiceTest {

    // Mock 객체 선언
    @Mock
    private MyPageRepository myPageRepository;

    @Mock
    private TimezoneEmpRepository timezoneEmpRepository;

    @Mock
    private TimezoneRepository timezoneRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    // 테스트할 서비스 클래스에 Mock 객체 주입
    @InjectMocks
    private MyPageService myPageService;

    // 각 테스트 전에 실행될 설정 메소드
    @BeforeEach
    void setUp() {
        // Mock 객체 초기화
        MockitoAnnotations.openMocks(this);
        // SecurityContextHolder에 Mock SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);
        // Mock SecurityContext에서 인증 객체 반환 설정
        when(securityContext.getAuthentication()).thenReturn(authentication);
        // Mock 인증 객체에서 사용자 ID 반환 설정
        when(authentication.getName()).thenReturn("1");
    }

    @Test
    void getEmployee_ShouldReturnMyPageResponseDTO() {
        // 테스트 데이터 준비
        // given
        Employee employee = Employee.builder()
                .empNo(1L)
                .name("John Doe")
                .company("Example Corp")
                .email("john.doe@example.com")
                .rank("Manager")
                .phoneNum("123-456-7890")
                .extensionNum("1234")
                .dept(new Dept("Sales"))
                .build();

        // Mock 객체의 동작 정의
        when(myPageRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        when(myPageRepository.findDefaultTimezoneName(anyLong())).thenReturn("UTC");

        // 서비스 메소드 호출 및 결과 검증
        // when
        MyPageResponseDTO responseDTO = myPageService.getEmployee();

        // 예상 결과와 실제 결과 비교
        // then
        assertEquals(1L, responseDTO.getEmpNo());
        assertEquals("John Doe", responseDTO.getName());
        assertEquals("Example Corp", responseDTO.getCompany());
        assertEquals("john.doe@example.com", responseDTO.getEmail());
        assertEquals("Manager", responseDTO.getRank());
        assertEquals("123-456-7890", responseDTO.getPhoneNum());
        assertEquals("1234", responseDTO.getExtensionNum());
        assertEquals("Sales", responseDTO.getDeptName());
        assertEquals("UTC", responseDTO.getTimezoneName());
        assertEquals("회원 정보 조회", responseDTO.getMsg());

        // Mock 객체의 메소드 호출 횟수 검증
        verify(myPageRepository, times(1)).findById(anyLong());
        verify(myPageRepository, times(1)).findDefaultTimezoneName(anyLong());
    }

    @Test
    void getEmployee_ShouldThrowException_WhenEmployeeNotFound() {
        // Mock 객체의 동작 정의: 직원이 없는 경우
        when(myPageRepository.findById(anyLong())).thenReturn(Optional.empty());

        // 예외 발생 여부 검증
        assertThrows(IllegalArgumentException.class, () -> myPageService.getEmployee());

        // Mock 객체의 메소드 호출 횟수 검증
        verify(myPageRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateEmployeeTimezone_ShouldUpdateTimezone() {
        // 가상의 테스트 데이터 준비
        Timezone existingTimezone = Timezone.builder()
                .timezoneName("UTC")
                .countryCode("US")
                .abbreviation("UTC")
                .timeStart(0L)
                .gmtOffset(0)
                .dst("N")
                .build();

        Timezone newTimezone = Timezone.builder()
                .timezoneName("PST")
                .countryCode("US")
                .abbreviation("PST")
                .timeStart(0L)
                .gmtOffset(-8)
                .dst("N")
                .build();

        Employee employee = Employee.builder()
                .empNo(1L)
                .name("John Doe")
                .company("Example Corp")
                .email("john.doe@example.com")
                .rank("Manager")
                .phoneNum("123-456-7890")
                .extensionNum("1234")
                .dept(new Dept("Sales"))
                .build();

        TimezoneEmp timezoneEmp = TimezoneEmp.builder()
                .timezone(existingTimezone)
                .employee(employee)
                .defaultTimezone(true)
                .build();

        // 새로운 timezone 지정
        MyPageUpdateDTO myPageUpdateDTO = MyPageUpdateDTO.builder()
                .empId(1L)
                .timezoneId(1L)
                .build();


        when(timezoneEmpRepository.findByEmpIdAndDefaultTimezone(anyLong())).thenReturn(Optional.of(timezoneEmp));
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.of(newTimezone));

        myPageService.updateEmployeeTimezone(myPageUpdateDTO);

        assertEquals(newTimezone, timezoneEmp.getTimezone());

        verify(timezoneEmpRepository, times(1)).findByEmpIdAndDefaultTimezone(anyLong());
        verify(timezoneRepository, times(1)).findById(anyLong());
        verify(timezoneEmpRepository, times(1)).save(timezoneEmp);
    }

    @Test
    void updateEmployeeTimezone_ShouldThrowException_WhenTimezoneEmpNotFound() {
        // 테스트 데이터 준비
        MyPageUpdateDTO myPageUpdateDTO = MyPageUpdateDTO.builder()
                .empId(1L)
                .timezoneId(1L)
                .build();

        // Mock 객체의 동작 정의: 기본 타임존을 찾을 수 없는 경우
        when(timezoneEmpRepository.findByEmpIdAndDefaultTimezone(anyLong())).thenReturn(Optional.empty());

        // 예외 발생 여부 검증
        assertThrows(IllegalArgumentException.class, () -> myPageService.updateEmployeeTimezone(myPageUpdateDTO));

        // Mock 객체의 메소드 호출 횟수 검증
        verify(timezoneEmpRepository, times(1)).findByEmpIdAndDefaultTimezone(anyLong());
    }

    @Test
    void updateEmployeeTimezone_ShouldThrowException_WhenTimezoneNotFound() {
        // 가상의 테스트 데이터 준비
        Timezone existingTimezone = Timezone.builder()
                .timezoneName("UTC")
                .countryCode("US")
                .abbreviation("UTC")
                .timeStart(0L)
                .gmtOffset(0)
                .dst("N")
                .build();

        Employee employee = Employee.builder()
                .empNo(1L)
                .name("John Doe")
                .company("Example Corp")
                .email("john.doe@example.com")
                .rank("Manager")
                .phoneNum("123-456-7890")
                .extensionNum("1234")
                .dept(new Dept("Sales"))
                .build();

        // TimezoneEmp 객체 생성
        TimezoneEmp timezoneEmp = TimezoneEmp.builder()
                .timezone(existingTimezone)
                .employee(employee)
                .defaultTimezone(true)
                .build();

        // MyPageUpdateDTO 객체 생성
        MyPageUpdateDTO myPageUpdateDTO = MyPageUpdateDTO.builder()
                .empId(1L)
                .timezoneId(2L)
                .build();

        // Mock 설정
        when(timezoneEmpRepository.findByEmpIdAndDefaultTimezone(anyLong())).thenReturn(Optional.of(timezoneEmp));
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.empty());

        // 예외 발생 검증
        assertThrows(IllegalArgumentException.class, () -> myPageService.updateEmployeeTimezone(myPageUpdateDTO));

        // 메소드 호출 검증
        verify(timezoneEmpRepository, times(1)).findByEmpIdAndDefaultTimezone(anyLong());
        verify(timezoneRepository, times(1)).findById(anyLong());
    }
}