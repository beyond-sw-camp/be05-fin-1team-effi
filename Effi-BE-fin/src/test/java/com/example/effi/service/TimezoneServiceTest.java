package com.example.effi.service;

import com.example.effi.domain.Dto.Timezone.TimezoneDTO;
import com.example.effi.domain.Entity.Timezone;
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
    void addTimezone() {
        TimezoneDTO timezoneDTO = new TimezoneDTO();
        Timezone timezone = Timezone.builder().build();
        when(timezoneRepository.save(any(Timezone.class))).thenReturn(timezone);

        TimezoneDTO result = timezoneService.addTimezone(timezoneDTO);

        assertNotNull(result);
        verify(timezoneRepository, times(1)).save(any(Timezone.class));
    }

    @Test
    void getAllTimezones() {
        Timezone timezone = Timezone.builder().build();
        when(timezoneRepository.findAll()).thenReturn(Arrays.asList(timezone));

        List<TimezoneDTO> result = timezoneService.getAllTimezones();

        assertFalse(result.isEmpty());
        verify(timezoneRepository, times(1)).findAll();
    }

    @Test
    void getTimezoneById() {
        Timezone timezone = Timezone.builder().build();
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.of(timezone));

        TimezoneDTO result = timezoneService.getTimezoneById(1L);

        assertNotNull(result);
        verify(timezoneRepository, times(1)).findById(anyLong());
    }

    @Test
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
    void deleteTimezone() {
        doNothing().when(timezoneRepository).deleteById(anyLong());

        timezoneService.deleteTimezone(1L);

        verify(timezoneRepository, times(1)).deleteById(anyLong());
    }
}
