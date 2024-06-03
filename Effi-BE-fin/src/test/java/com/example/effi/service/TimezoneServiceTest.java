package com.example.effi.service;

import com.example.effi.domain.DTO.GlobalResponse;
import com.example.effi.domain.DTO.TimezoneDTO;
import com.example.effi.domain.Entity.Timezone;
import com.example.effi.repository.TimezoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
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

    // addTimezone 메서드 테스트
    @Test
    @DisplayName("새로운 타임존을 추가한다 - 성공")
    void addTimezoneSuccess() {
        TimezoneDTO timezoneDTO = new TimezoneDTO();
        Timezone timezone = Timezone.builder().build();
        when(timezoneRepository.save(any(Timezone.class))).thenReturn(timezone);

        GlobalResponse<TimezoneDTO> result = timezoneService.addTimezone(timezoneDTO);

        assertNotNull(result);
        assertEquals(200, result.getStatus());
        assertEquals("Success", result.getMessage());
        assertNotNull(result.getData());
        verify(timezoneRepository, times(1)).save(any(Timezone.class));
    }

    @Test
    @DisplayName("새로운 타임존을 추가한다 - 실패: 중복된 타임존")
    void addTimezoneFailDuplicate() {
        TimezoneDTO timezoneDTO = new TimezoneDTO();
        when(timezoneRepository.save(any(Timezone.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate timezone"));

        GlobalResponse<TimezoneDTO> result = timezoneService.addTimezone(timezoneDTO);

        assertNotNull(result);
        assertEquals(400, result.getStatus());
        assertTrue(result.getMessage().contains("Failed"));
        assertNull(result.getData());
        verify(timezoneRepository, times(1)).save(any(Timezone.class));
    }

    // getAllTimezones 메서드 테스트
    @Test
    @DisplayName("모든 타임존을 조회한다 - 성공")
    void getAllTimezonesSuccess() {
        Timezone timezone = Timezone.builder().build();
        when(timezoneRepository.findAll()).thenReturn(Arrays.asList(timezone));

        GlobalResponse<List<TimezoneDTO>> result = timezoneService.getAllTimezones();

        assertNotNull(result);
        assertEquals(200, result.getStatus());
        assertEquals("성공", result.getMessage());
        assertNotNull(result.getData());
        verify(timezoneRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("모든 타임존을 조회한다 - 실패: 조회된 타임존 없음")
    void getAllTimezonesFailNoTimezone() {
        when(timezoneRepository.findAll()).thenReturn(Arrays.asList());

        GlobalResponse<List<TimezoneDTO>> result = timezoneService.getAllTimezones();

        assertNotNull(result);
        assertEquals(400, result.getStatus());
        assertTrue(result.getMessage().contains("조회된 타임존 없음"));
        assertNull(result.getData());
        verify(timezoneRepository, times(1)).findAll();
    }

    // getTimezoneById 메서드 테스트
    @Test
    @DisplayName("ID로 타임존을 조회한다 - 성공")
    void getTimezoneByIdSuccess() {
        Timezone timezone = Timezone.builder().timezoneId(1L).build();
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.of(timezone));

        GlobalResponse<TimezoneDTO> result = timezoneService.getTimezoneById(1L);

        assertNotNull(result);
        assertEquals(200, result.getStatus());
        assertEquals("Success", result.getMessage());
        assertNotNull(result.getData());
        verify(timezoneRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("ID로 타임존을 조회한다 - 실패: 잘못된 타임존 ID")
    void getTimezoneByIdFailInvalidId() {
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.empty());

        GlobalResponse<TimezoneDTO> result = timezoneService.getTimezoneById(999L);

        assertNotNull(result);
        assertEquals(400, result.getStatus());
        assertTrue(result.getMessage().contains("Invalid timezone ID"));
        assertNull(result.getData());
        verify(timezoneRepository, times(1)).findById(anyLong());
    }

    // updateTimezone 메서드 테스트
    @Test
    @DisplayName("타임존을 업데이트한다 - 성공")
    void updateTimezoneSuccess() {
        TimezoneDTO timezoneDTO = new TimezoneDTO();
        Timezone timezone = Timezone.builder().timezoneId(1L).build();
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.of(timezone));
        when(timezoneRepository.save(any(Timezone.class))).thenReturn(timezone);

        GlobalResponse<TimezoneDTO> result = timezoneService.updateTimezone(1L, timezoneDTO);

        assertNotNull(result);
        assertEquals(200, result.getStatus());
        assertEquals("Success", result.getMessage());
        assertNotNull(result.getData());
        verify(timezoneRepository, times(1)).findById(anyLong());
        verify(timezoneRepository, times(1)).save(any(Timezone.class));
    }

    @Test
    @DisplayName("타임존을 업데이트한다 - 실패: 잘못된 타임존 ID")
    void updateTimezoneFailInvalidId() {
        TimezoneDTO timezoneDTO = new TimezoneDTO();
        when(timezoneRepository.findById(anyLong())).thenReturn(Optional.empty());

        GlobalResponse<TimezoneDTO> result = timezoneService.updateTimezone(999L, timezoneDTO);

        assertNotNull(result);
        assertEquals(400, result.getStatus());
        assertTrue(result.getMessage().contains("Invalid timezone ID"));
        assertNull(result.getData());
        verify(timezoneRepository, times(1)).findById(anyLong());
        verify(timezoneRepository, times(0)).save(any(Timezone.class));
    }

    // deleteTimezone 메서드 테스트
    @Test
    @DisplayName("타임존을 삭제한다 - 성공")
    void deleteTimezoneSuccess() {
        doNothing().when(timezoneRepository).deleteById(anyLong());

        GlobalResponse<TimezoneDTO> result = timezoneService.deleteTimezone(1L);

        assertNotNull(result);
        assertEquals(200, result.getStatus());
        assertEquals("Success", result.getMessage());
        assertNull(result.getData());
        verify(timezoneRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("타임존을 삭제한다 - 실패: 잘못된 타임존 ID")
    void deleteTimezoneFailInvalidId() {
        doThrow(new IllegalArgumentException("Invalid timezone ID")).when(timezoneRepository).deleteById(anyLong());

        GlobalResponse<TimezoneDTO> result = timezoneService.deleteTimezone(999L);

        assertNotNull(result);
        assertEquals(400, result.getStatus());
        assertTrue(result.getMessage().contains("Invalid timezone ID"));
        assertNull(result.getData());
        verify(timezoneRepository, times(1)).deleteById(anyLong());
    }
}
