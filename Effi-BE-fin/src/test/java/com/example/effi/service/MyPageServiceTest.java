package com.example.effi.service;

import com.example.effi.domain.DTO.MyPageResponseDTO;
import com.example.effi.domain.DTO.MyPageUpdateDTO;
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

    @InjectMocks
    private MyPageService myPageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("1");
    }

    @Test
    void getEmployee_ShouldReturnMyPageResponseDTO() {
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

        when(myPageRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        when(myPageRepository.findDefaultTimezoneName(anyLong())).thenReturn("UTC");

        MyPageResponseDTO responseDTO = myPageService.getEmployee();

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

        verify(myPageRepository, times(1)).findById(anyLong());
        verify(myPageRepository, times(1)).findDefaultTimezoneName(anyLong());
    }

    @Test
    void getEmployee_ShouldThrowException_WhenEmployeeNotFound() {
        when(myPageRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.getEmployee());

        verify(myPageRepository, times(1)).findById(anyLong());
        verify(myPageRepository, never()).findDefaultTimezoneName(anyLong());
    }

    @Test
    void getEmployee_ShouldThrowException_WhenDefaultTimezoneNotFound() {
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

        when(myPageRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        when(myPageRepository.findDefaultTimezoneName(anyLong())).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> myPageService.getEmployee());

        verify(myPageRepository, times(1)).findById(anyLong());
        verify(myPageRepository, times(1)).findDefaultTimezoneName(anyLong());
    }

    @Test
    void updateEmployeeTimezone_ShouldUpdateTimezone() {
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
        MyPageUpdateDTO myPageUpdateDTO = MyPageUpdateDTO.builder()
                .empId(1L)
                .timezoneId(1L)
                .build();

        when(timezoneEmpRepository.findByEmpIdAndDefaultTimezone(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.updateEmployeeTimezone(myPageUpdateDTO));

        verify(timezoneEmpRepository, times(1)).findByEmpIdAndDefaultTimezone(anyLong());
    }

    @Test
    void updateEmployeeTimezone_ShouldThrowException_WhenTimezoneNotFound() {
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

        TimezoneEmp timezoneEmp = TimezoneEmp.builder()
                .timezone(existingTimezone)
                .employee(employee)
                .defaultTimezone(true)
                .build();

        MyPageUpdateDTO myPageUpdateDTO = MyPageUpdateDTO.builder()
                .empId(1L)
                .timezoneId(2L)
                .build();

        when(timezoneEmpRepository.findByEmpIdAndDefaultTimezone(anyLong())).thenReturn(Optional.of(timezoneEmp));
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> myPageService.updateEmployeeTimezone(myPageUpdateDTO));

        verify(timezoneEmpRepository, times(1)).findByEmpIdAndDefaultTimezone(anyLong());
        verify(timezoneRepository, times(1)).findById(anyLong());
    }
}
