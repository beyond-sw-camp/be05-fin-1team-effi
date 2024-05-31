package com.example.effi.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.effi.domain.Entity.Schedule;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class SearchRepositoryTest {

    @Autowired
    private SearchRepository searchRepository;

    @Test
    @DisplayName("제목으로 일정 검색 테스트")
    void testFindAllByTitleContainingIgnoreCase() {
        // Given
        Schedule schedule1 = Schedule.builder()
                .title("Test Title One")
                .context("Sample Context")
                .startTime(new Date())
                .endTime(new Date())
                .status(1)
                .notificationYn(true)
                .deleteYn(false)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        Schedule schedule2 = Schedule.builder()
                .title("Another Test Title")
                .context("Sample Context")
                .startTime(new Date())
                .endTime(new Date())
                .status(1)
                .notificationYn(true)
                .deleteYn(false)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        Schedule schedule3 = Schedule.builder()
                .title("Irrelevant Title")
                .context("Sample Context")
                .startTime(new Date())
                .endTime(new Date())
                .status(1)
                .notificationYn(true)
                .deleteYn(false)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        searchRepository.save(schedule1);
        searchRepository.save(schedule2);
        searchRepository.save(schedule3);

        // When
        List<Schedule> results = searchRepository.findAllByTitleContainingIgnoreCase("test title");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Schedule::getTitle).containsExactlyInAnyOrder("Test Title One", "Another Test Title");
    }
}
