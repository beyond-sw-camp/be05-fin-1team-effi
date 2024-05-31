package com.example.effi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Schedule;
import com.example.effi.domain.Entity.Tag;
import com.example.effi.domain.Entity.TagSchedule;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class TagScheduleRepositoryTest {

    @Autowired
    private TagScheduleRepository tagScheduleRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Tag sampleTag;
    private Schedule sampleSchedule;
    private TagSchedule sampleTagSchedule;
    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        // 카테고리 설정 및 저장
        sampleCategory = Category.builder()
                .categoryName("Sample Category")
                .build();
        categoryRepository.save(sampleCategory);

        // 태그 설정 및 저장
        sampleTag = Tag.builder()
                .tagName("Sample Tag")
                .build();
        tagRepository.save(sampleTag);

        // 스케줄 설정 및 저장
        sampleSchedule = Schedule.builder()
                .title("Sample Schedule")
                .context("Sample Context")
                .startTime(new Date())
                .endTime(new Date())
                .status(1)
                .notificationYn(true)
                .deleteYn(false)
                .createdAt(new Date()) // 필수 필드 설정
                .updatedAt(new Date()) // 필수 필드 설정
                .category(sampleCategory) // Category 설정
                .build();
        scheduleRepository.save(sampleSchedule);

        // 태그 스케줄 설정 및 저장
        sampleTagSchedule = TagSchedule.builder()
                .tag(sampleTag)
                .schedule(sampleSchedule)
                .deleteYn(false)
                .build();
        tagScheduleRepository.save(sampleTagSchedule);
    }

    @Test
    void testFindAllBySchedule_ScheduleId() {
        List<TagSchedule> result = tagScheduleRepository.findAllBySchedule_ScheduleId(sampleSchedule.getScheduleId());
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getSchedule().getScheduleId()).isEqualTo(sampleSchedule.getScheduleId());
    }

    @Test
    void testFindAllByTag_TagId() {
        List<TagSchedule> result = tagScheduleRepository.findAllByTag_TagId(sampleTag.getTagId());
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getTag().getTagId()).isEqualTo(sampleTag.getTagId());
    }

    @Test
    void testFindAllByTag_TagNameContainingIgnoreCase() {
        List<Schedule> result = tagScheduleRepository.findAllByTag_TagNameContainingIgnoreCase("sample");
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getScheduleId()).isEqualTo(sampleSchedule.getScheduleId());
    }
}
