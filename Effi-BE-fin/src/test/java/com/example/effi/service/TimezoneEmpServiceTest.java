package com.example.effi.service;

import com.example.effi.domain.DTO.TimezoneDTO;
import com.example.effi.domain.Entitiy.Employee;
import com.example.effi.domain.Entitiy.Timezone;
import com.example.effi.domain.Entitiy.TimezoneEmp;
import com.example.effi.repository.EmployeeRepository;
import com.example.effi.repository.TimezoneEmpRepository;
import com.example.effi.repository.TimezoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    void getTimezonesForEmployee() {
        Employee employee = Employee.builder().id(1L).build();
        TimezoneEmp timezoneEmp = TimezoneEmp.builder().timezone(timezone).employee(employee).build();
        when(timezoneEmpRepository.findByEmployeeId(anyLong())).thenReturn(Arrays.asList(timezoneEmp));

        List<TimezoneDTO> result = timezoneEmpService.getTimezonesForEmployee(1L);

        assertFalse(result.isEmpty());
        verify(timezoneEmpRepository, times(1)).findByEmployeeId(anyLong());
    }

    @Test
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
}
