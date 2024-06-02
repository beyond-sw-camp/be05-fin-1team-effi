package com.example.effi.service;

import com.example.effi.domain.DTO.TimezoneDTO;
import com.example.effi.domain.Entity.Timezone;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class TimezoneServiceTest {

    @Mock
    private TimezoneRepository timezoneRepository;

    @InjectMocks
    private TimezoneService timezoneService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("타임존을 추가한다. - 성공")
    void addTimezone() {
        TimezoneDTO timezoneDTO = new TimezoneDTO();
        Timezone timezone = Timezone.builder().build();
        when(timezoneRepository.save(any(Timezone.class))).thenReturn(timezone);

        TimezoneDTO result = timezoneService.addTimezone(timezoneDTO);

        assertNotNull(result);
        verify(timezoneRepository, times(1)).save(any(Timezone.class));
    }

    @Test
    @DisplayName("타임존을 추가한다. - 실패")
    void addTimezoneFail() {
        TimezoneDTO timezoneDTO = new TimezoneDTO();
        when(timezoneRepository.save(any(Timezone.class))).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> timezoneService.addTimezone(timezoneDTO));
        verify(timezoneRepository, times(1)).save(any(Timezone.class));
    }

    @Test
    @DisplayName("모든 타임존을 조회한다. - 성공")
    void getAllTimezones() {
        Timezone timezone = Timezone.builder().build();
        when(timezoneRepository.findAll()).thenReturn(Arrays.asList(timezone));

        List<TimezoneDTO> result = timezoneService.getAllTimezones();

        assertFalse(result.isEmpty());
        verify(timezoneRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("모든 타임존을 조회한다. - 실패")
    void getAllTimezonesFail() {
        when(timezoneRepository.findAll()).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> timezoneService.getAllTimezones());
        verify(timezoneRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("ID로 타임존을 조회한다. - 성공")
    void getTimezoneById() {
        Timezone timezone = Timezone.builder().build();
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.of(timezone));

        TimezoneDTO result = timezoneService.getTimezoneById(1L);

        assertNotNull(result);
        verify(timezoneRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("ID로 타임존을 조회한다. - 실패")
    void getTimezoneByIdFail() {
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> timezoneService.getTimezoneById(1L));
        verify(timezoneRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("타임존을 수정한다. - 성공")
    void updateTimezone() {
        TimezoneDTO timezoneDTO = new TimezoneDTO();
        Timezone timezone = Timezone.builder().build();
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.of(timezone));
        when(timezoneRepository.save(any(Timezone.class))).thenReturn(timezone);

        TimezoneDTO result = timezoneService.updateTimezone(1L, timezoneDTO);

        assertNotNull(result);
        verify(timezoneRepository, times(1)).findById(anyLong());
        verify(timezoneRepository, times(1)).save(any(Timezone.class));
    }

    @Test
    @DisplayName("타임존을 수정한다. - 실패")
    void updateTimezoneFail() {
        TimezoneDTO timezoneDTO = new TimezoneDTO();
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> timezoneService.updateTimezone(1L, timezoneDTO));
        verify(timezoneRepository, times(1)).findById(anyLong());
        verify(timezoneRepository, times(0)).save(any(Timezone.class));
    }

    @Test
    @DisplayName("타임존을 삭제한다. - 성공")
    void deleteTimezone() {
        doNothing().when(timezoneRepository).deleteById(anyLong());

        timezoneService.deleteTimezone(1L);

        verify(timezoneRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("타임존을 삭제한다. - 실패")
    void deleteTimezoneFail() {
        doThrow(new RuntimeException()).when(timezoneRepository).deleteById(anyLong());

        assertThrows(RuntimeException.class, () -> timezoneService.deleteTimezone(1L));
        verify(timezoneRepository, times(1)).deleteById(anyLong());
    }
}
