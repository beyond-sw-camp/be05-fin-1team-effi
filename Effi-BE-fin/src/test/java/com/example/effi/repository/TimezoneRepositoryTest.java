package com.example.effi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.effi.domain.Entity.Timezone;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TimezoneRepositoryTest {

    @Autowired
    private TimezoneRepository timezoneRepository;

    private Timezone timezone;

    @BeforeEach
    void setUp() {
        timezone = Timezone.builder()
                .timezoneName("KST")
                .countryCode("KR")
                .abbreviation("KST")
                .timeStart(0L)
                .gmtOffset(9 * 3600)
                .dst("0")
                .build();
        timezoneRepository.save(timezone);
    }

    @Test
    @DisplayName("타임존을 저장한다. - 성공")
    void testSave() {
        Timezone savedTimezone = timezoneRepository.save(timezone);

        assertNotNull(savedTimezone.getTimezoneId());
        assertEquals("KST", savedTimezone.getTimezoneName());
    }

    @Test
    @DisplayName("타임존을 저장한다. - 실패: null 값이 저장된 경우")
    void testSaveFail() {
        Timezone invalidTimezone = Timezone.builder().build();

        assertThrows(Exception.class, () -> timezoneRepository.save(invalidTimezone));
    }

    @Test
    @DisplayName("타임존을 조회한다. - 성공")
    void testFindById() {
        Optional<Timezone> foundTimezone = timezoneRepository.findById(timezone.getTimezoneId());

        assertTrue(foundTimezone.isPresent());
        assertEquals("KST", foundTimezone.get().getTimezoneName());
    }

    @Test
    @DisplayName("타임존을 조회한다. - 실패: 존재하지 않는 타임존 ID")
    void testFindByIdFail() {
        Optional<Timezone> foundTimezone = timezoneRepository.findById(0L);

        assertFalse(foundTimezone.isPresent());
    }

    @Test
    @DisplayName("모든 타임존을 조회한다. - 성공")
    void testFindAll() {
        Timezone anotherTimezone = Timezone.builder()
                .timezoneName("PST")
                .countryCode("US")
                .abbreviation("PST")
                .timeStart(0L)
                .gmtOffset(-8 * 3600)
                .dst("0")
                .build();
        timezoneRepository.save(anotherTimezone);

        List<Timezone> timezones = timezoneRepository.findAll();

        assertFalse(timezones.isEmpty());
        assertEquals(2, timezones.size());
    }

    @Test
    @DisplayName("모든 타임존을 조회한다. - 실패: 조회된 타임존 없음")
    void testFindAllFail() {
        timezoneRepository.deleteAll();

        List<Timezone> timezones = timezoneRepository.findAll();

        assertTrue(timezones.isEmpty());
    }

    @Test
    @DisplayName("타임존 삭제 테스트 - 존재하는 ID")
    public void testDeleteByIdExistingId() {
        // given
        Timezone timezone = Timezone.builder()
                .timezoneName("PST")
                .countryCode("US")
                .abbreviation("PST")
                .timeStart(0L)
                .gmtOffset(-8 * 3600)
                .dst("0")
                .build();

        Timezone savedTimezone = timezoneRepository.save(timezone);

        // when
        timezoneRepository.deleteById(savedTimezone.getTimezoneId());

        // then
        assertThrows(NoSuchElementException.class,
                () -> timezoneRepository.findById(savedTimezone.getTimezoneId()).orElseThrow());
    }

}
