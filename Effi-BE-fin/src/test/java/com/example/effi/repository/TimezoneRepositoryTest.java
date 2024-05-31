package com.example.effi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.effi.domain.Entity.Timezone;

import java.util.List;
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
    void testSave() {
        Timezone savedTimezone = timezoneRepository.save(timezone);

        assertNotNull(savedTimezone.getTimezoneId());
        assertEquals("KST", savedTimezone.getTimezoneName());
    }

    @Test
    void testFindById() {
        Optional<Timezone> foundTimezone = timezoneRepository.findById(timezone.getTimezoneId());

        assertTrue(foundTimezone.isPresent());
        assertEquals("KST", foundTimezone.get().getTimezoneName());
    }

    @Test
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
    void testDeleteById() {
        timezoneRepository.deleteById(timezone.getTimezoneId());
        Optional<Timezone> deletedTimezone = timezoneRepository.findById(timezone.getTimezoneId());

        assertFalse(deletedTimezone.isPresent());
    }
}
