package com.example.effi.service;

import com.example.effi.domain.DTO.TimezoneDTO;
import com.example.effi.domain.Entity.Employee;
import com.example.effi.domain.Entity.Timezone;
import com.example.effi.domain.Entity.TimezoneEmp;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.TimezoneEmpRepository;
import com.example.effi.repository.TimezoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TimezoneEmpServiceTest {

    @Mock
    private TimezoneEmpRepository timezoneEmpRepository;

    @Mock
    private TimezoneRepository timezoneRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private TimezoneEmpService timezoneEmpService;

    private Timezone timezone;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        timezone = Timezone.builder()
                .timezoneName("KST")
                .countryCode("KR")
                .abbreviation("KST")
                .timeStart(0L)
                .gmtOffset(9 * 3600) // GMT+9 in seconds
                .dst("0")
                .build();
    }

    @Test
    @DisplayName("타임존을 직원에게 추가한다. - 성공")
    void addTimezoneForEmployee() {
        Employee employee = Employee.builder().id(1L).build();
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.of(timezone));
        when(timezoneEmpRepository.countByEmployeeId(anyLong())).thenReturn(1L);
        when(timezoneEmpRepository.save(any(TimezoneEmp.class))).thenReturn(TimezoneEmp.builder().timezone(timezone).employee(employee).build());

        assertDoesNotThrow(() -> timezoneEmpService.addTimezoneForEmployee(1L, 1L, true));

        verify(employeeRepository, times(1)).findById(anyLong());
        verify(timezoneRepository, times(1)).findById(anyLong());
        verify(timezoneEmpRepository, times(1)).countByEmployeeId(anyLong());
        verify(timezoneEmpRepository, times(1)).save(any(TimezoneEmp.class));
    }

    @Test
    @DisplayName("타임존을 직원에게 추가할 때 타임존이 존재하지 않으면 예외를 던진다. - 실패")
    void addTimezoneForEmployeeWithNonExistingTimezone() {
        Employee employee = Employee.builder().id(1L).build();
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> timezoneEmpService.addTimezoneForEmployee(1L, 1L, true));

        verify(employeeRepository, times(1)).findById(anyLong());
        verify(timezoneRepository, times(1)).findById(anyLong());
        verify(timezoneEmpRepository, times(0)).countByEmployeeId(anyLong());
        verify(timezoneEmpRepository, times(0)).save(any(TimezoneEmp.class));
    }

    @Test
    @DisplayName("타임존을 직원에게 추가할 때 직원이 존재하지 않으면 예외를 던진다. - 실패")
    void getTimezonesForEmployee() {
        Employee employee = Employee.builder().id(1L).build();
        TimezoneEmp timezoneEmp = TimezoneEmp.builder().timezone(timezone).employee(employee).build();
        when(timezoneEmpRepository.findByEmployeeId(anyLong())).thenReturn(Arrays.asList(timezoneEmp));

        List<TimezoneDTO> result = timezoneEmpService.getTimezonesForEmployee(1L);

        assertFalse(result.isEmpty());
        verify(timezoneEmpRepository, times(1)).findByEmployeeId(anyLong());
    }

    @Test
    @DisplayName("기본 타임존을 변경한다. - 성공")
    void updateDefaultTimezoneForEmployee() {
        Employee employee = Employee.builder().id(1L).build();
        TimezoneEmp timezoneEmp = TimezoneEmp.builder().timezone(timezone).employee(employee).defaultTimezone(true).build();
        when(timezoneEmpRepository.findByEmployeeIdAndDefaultTimezone(anyLong(), anyBoolean()))
                .thenReturn(Optional.of(timezoneEmp));
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.of(timezone));
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        when(timezoneEmpRepository.save(any(TimezoneEmp.class))).thenReturn(timezoneEmp);

        assertDoesNotThrow(() -> timezoneEmpService.updateDefaultTimezoneForEmployee(1L, 1L));

        verify(timezoneEmpRepository, times(1))
                .findByEmployeeIdAndDefaultTimezone(anyLong(), anyBoolean());
        verify(timezoneRepository, times(1)).findById(anyLong());
        verify(employeeRepository, times(1)).findById(anyLong());
        verify(timezoneEmpRepository, times(2)).save(any(TimezoneEmp.class));
    }

    @Test
    @DisplayName("기본 타임존을 변경할 때 타임존이 존재하지 않으면 예외를 던진다. - 실패")
    void updateDefaultTimezoneForEmployeeWithNonExistingTimezone() {
        when(timezoneEmpRepository.findByEmployeeIdAndDefaultTimezone(anyLong(), anyBoolean()))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> timezoneEmpService.updateDefaultTimezoneForEmployee(1L, 1L));

        verify(timezoneEmpRepository, times(1))
                .findByEmployeeIdAndDefaultTimezone(anyLong(), anyBoolean());
        verify(timezoneRepository, times(0)).findById(anyLong());
        verify(employeeRepository, times(0)).findById(anyLong());
        verify(timezoneEmpRepository, times(0)).save(any(TimezoneEmp.class));
    }

    @DisplayName("직원의 타임존을 삭제한다. - 성공")
    @Test
    void removeTimezoneForEmployee() {
        Employee employee = Employee.builder().id(1L).build();
        TimezoneEmp timezoneEmp = TimezoneEmp.builder().timezone(timezone).employee(employee).build();
        when(timezoneEmpRepository.findByEmployeeIdAndTimezone_TimezoneId(anyLong(), anyLong()))
                .thenReturn(Optional.of(timezoneEmp));
        doNothing().when(timezoneEmpRepository).delete(any(TimezoneEmp.class));

        assertDoesNotThrow(() -> timezoneEmpService.removeTimezoneForEmployee(1L, 1L));

        verify(timezoneEmpRepository, times(1))
                .findByEmployeeIdAndTimezone_TimezoneId(anyLong(), anyLong());
        verify(timezoneEmpRepository, times(1)).delete(any(TimezoneEmp.class));
    }

    @DisplayName("직원의 타임존을 삭제할 때 타임존이 존재하지 않으면 예외를 던진다. - 실패")
    @Test
    void removeTimezoneForEmployeeWithNonExistingTimezone() {
        when(timezoneEmpRepository.findByEmployeeIdAndTimezone_TimezoneId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> timezoneEmpService.removeTimezoneForEmployee(1L, 1L));

        verify(timezoneEmpRepository, times(1))
                .findByEmployeeIdAndTimezone_TimezoneId(anyLong(), anyLong());
        verify(timezoneEmpRepository, times(0)).delete(any(TimezoneEmp.class));
    }

    @DisplayName("직원의 타임존을 삭제할 때 직원이 존재하지 않으면 예외를 던진다. - 실패")
    @Test
    void removeTimezoneForEmployeeWithNonExistingEmployee() {
        // Arrange: 빈 Optional 반환하도록 설정
        when(timezoneEmpRepository.findByEmployeeIdAndTimezone_TimezoneId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        // Act & Assert: 예외가 던져지는지 확인
        assertThrows(IllegalArgumentException.class, () -> timezoneEmpService.removeTimezoneForEmployee(1L, 1L));

        // Verify: 저장소 메서드 호출 확인
        verify(timezoneEmpRepository, times(1))
                .findByEmployeeIdAndTimezone_TimezoneId(anyLong(), anyLong());
        verify(timezoneEmpRepository, times(0)).delete(any(TimezoneEmp.class));
    }

}
