package com.example.effi.service;

import com.example.effi.domain.DTO.TimezoneDTO;
import com.example.effi.repository.TimezoneRepository;
import com.example.effi.domain.Entitiy.Timezone;
import lombok.RequiredArgsConstructor;
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
     * @param timezoneDTO 타임존 DTO
     * @return 추가된 타임존 DTO
     */
    @Transactional
    public TimezoneDTO addTimezone(TimezoneDTO timezoneDTO) {
        Timezone timezone = timezoneDTO.toEntity();
        Timezone savedTimezone = timezoneRepository.save(timezone);
        return TimezoneDTO.fromEntity(savedTimezone);
    }

    /**
     * 모든 타임존을 조회합니다.
     * @return 타임존 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<TimezoneDTO> getAllTimezones() {
        return timezoneRepository.findAll().stream()
                .map(TimezoneDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ID로 타임존을 조회합니다.
     * @param timezoneId 타임존 ID
     * @return 조회된 타임존 DTO
     */
    @Transactional(readOnly = true)
    public TimezoneDTO getTimezoneById(Long timezoneId) {
        Timezone timezone = timezoneRepository.findById(timezoneId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid timezone ID"));
        return TimezoneDTO.fromEntity(timezone);
    }

    /**
     * 타임존을 업데이트합니다.
     * @param timezoneId 타임존 ID
     * @param timezoneDTO 타임존 DTO
     * @return 업데이트된 타임존 DTO
     */
    @Transactional
    public TimezoneDTO updateTimezone(Long timezoneId, TimezoneDTO timezoneDTO) {
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
        return TimezoneDTO.fromEntity(updatedTimezone);
    }

    /**
     * 타임존을 삭제합니다.
     * @param timezoneId 타임존 ID
     */
    @Transactional
    public void deleteTimezone(Long timezoneId) {
        timezoneRepository.deleteById(timezoneId);
    }
}

