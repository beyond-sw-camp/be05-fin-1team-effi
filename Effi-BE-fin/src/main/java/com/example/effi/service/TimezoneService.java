package com.example.effi.service;

import com.example.effi.domain.DTO.GlobalResponse;
import com.example.effi.domain.DTO.TimezoneDTO;
import com.example.effi.domain.DTO.TimezoneListDTO;
import com.example.effi.domain.Entity.Timezone;
import com.example.effi.repository.TimezoneRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TimezoneService는 Timezone 엔티티와 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class TimezoneService {
    private final TimezoneRepository timezoneRepository;

    /**
     * 새로운 타임존을 추가합니다.
     * 
     * @param timezoneDTO 타임존 DTO
     * @return 추가된 타임존 DTO
     */
    @Transactional
    public GlobalResponse<TimezoneDTO> addTimezone(TimezoneDTO timezoneDTO) {
        try {
            Timezone timezone = timezoneDTO.toEntity();
            Timezone savedTimezone = timezoneRepository.save(timezone);
            TimezoneDTO savedTimezoneDTO = TimezoneDTO.fromEntity(savedTimezone);
            return new GlobalResponse<>(200, "Success", savedTimezoneDTO);
        } catch (DataIntegrityViolationException e) {
            return new GlobalResponse<>(400, "Failed: Duplicate timezone", null);
        }
    }

    /**
     * 모든 타임존을 조회합니다.
     * 
     * @return 타임존 DTO 목록
     */
    @Transactional(readOnly = true)
    public GlobalResponse<List<TimezoneDTO>> getAllTimezones() {
        List<Timezone> timezones = timezoneRepository.findAll();
        if (timezones.isEmpty()) {
            return new GlobalResponse<>(400, "실패: 조회된 타임존 없음", null);
        } else {
            List<TimezoneDTO> timezoneDTOs = timezones.stream()
                    .map(TimezoneDTO::fromEntity)
                    .collect(Collectors.toList());
            return new GlobalResponse<>(200, "성공", timezoneDTOs);
        }
    }

    /**
     * ID로 타임존을 조회합니다.
     * 
     * @param timezoneId 타임존 ID
     * @return 조회된 타임존 DTO
     */
    @Transactional(readOnly = true)
    public GlobalResponse<TimezoneDTO> getTimezoneById(Long timezoneId) {
        try {
            Timezone timezone = timezoneRepository.findById(timezoneId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid timezone ID"));
            TimezoneDTO timezoneDTO = TimezoneDTO.fromEntity(timezone);
            return new GlobalResponse<>(200, "Success", timezoneDTO);
        } catch (IllegalArgumentException e) {
            return new GlobalResponse<>(400, e.getMessage(), null);
        }
    }

    /**
     * 타임존을 업데이트합니다.
     * 
     * @param timezoneId  타임존 ID
     * @param timezoneDTO 타임존 DTO
     * @return 업데이트된 타임존 DTO
     */
    @Transactional
    public GlobalResponse<TimezoneDTO> updateTimezone(Long timezoneId, TimezoneDTO timezoneDTO) {
        try {
            Timezone existingTimezone = timezoneRepository.findById(timezoneId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid timezone ID"));

            existingTimezone = existingTimezone.toBuilder()
                    .timezoneName(timezoneDTO.getTimezoneName())
                    .countryCode(timezoneDTO.getCountryCode())
                    .abbreviation(timezoneDTO.getAbbreviation())
                    .timeStart(timezoneDTO.getTimeStart())
                    .gmtOffset(timezoneDTO.getGmtOffset())
                    .dst(timezoneDTO.getDst())
                    .build();

            Timezone updatedTimezone = timezoneRepository.save(existingTimezone);
            TimezoneDTO updatedTimezoneDTO = TimezoneDTO.fromEntity(updatedTimezone);
            return new GlobalResponse<>(200, "Success", updatedTimezoneDTO);
        } catch (IllegalArgumentException e) {
            return new GlobalResponse<>(400, e.getMessage(), null);
        }
    }

    /**
     * 타임존을 삭제합니다.
     * 
     * @param timezoneId 타임존 ID
     */
    @Transactional
    public GlobalResponse<TimezoneDTO> deleteTimezone(Long timezoneId) {
        try {
            timezoneRepository.deleteById(timezoneId);
            return new GlobalResponse<>(200, "Success", null);
        } catch (IllegalArgumentException e) {
            return new GlobalResponse<>(400, e.getMessage(), null);
        }
    }

    // 타임존 리스트 출력
    @Transactional
    public List<TimezoneListDTO> getTimezones() {
        return timezoneRepository.findAllTimezonesGroupedByName();
    }

}
