package com.example.effi.service;

import com.example.effi.domain.DTO.TagResponseDTO;
import com.example.effi.domain.DTO.TagScheduleResponseDTO;
import com.example.effi.domain.Entitiy.Category;
import com.example.effi.domain.Entitiy.Schedule;
import com.example.effi.domain.Entitiy.Tag;
import com.example.effi.domain.Entitiy.TagSchedule;
import com.example.effi.repository.ScheduleRepository;
import com.example.effi.repository.TagRepository;
import com.example.effi.repository.TagScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagScheduleServiceTest {

    @Mock
    private TagScheduleRepository tagScheduleRepository;

    @Mock
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private TagScheduleService tagScheduleService;

    private Tag sampleTag;
    private Schedule sampleSchedule;
    private TagSchedule sampleTagSchedule;
    private Category sampleCategory;

    // 각 테스트 실행 전에 샘플 데이터를 설정합니다.
    @BeforeEach
    void setUp() {
        sampleCategory = Category.builder()
                .categoryId(1L)
                .categoryName("Sample Category")
                .build();

        sampleTag = Tag.builder()
                .tagName("Sample Tag")
                .build();

        setTagId(sampleTag, 1L);

        sampleSchedule = Schedule.builder()
                .title("Sample Schedule")
                .context("Sample Context")
                .startTime(new Date())
                .endTime(new Date())
                .status(1)
                .notificationYn(true)
                .deleteYn(false)
                .category(sampleCategory)  // Category 설정
                .build();

        setScheduleId(sampleSchedule, 1L);

        sampleTagSchedule = TagSchedule.builder()
                .tag(sampleTag)
                .schedule(sampleSchedule)
                .deleteYn(false)
                .build();

        setTagScheduleId(sampleTagSchedule, 1L);
    }

    private void setTagId(Tag tag, Long tagId) {
        try {
            java.lang.reflect.Field field = Tag.class.getDeclaredField("tagId");
            field.setAccessible(true);
            field.set(tag, tagId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void setScheduleId(Schedule schedule, Long scheduleId) {
        try {
            java.lang.reflect.Field field = Schedule.class.getDeclaredField("scheduleId");
            field.setAccessible(true);
            field.set(schedule, scheduleId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void setTagScheduleId(TagSchedule tagSchedule, Long tagScheduleId) {
        try {
            java.lang.reflect.Field field = TagSchedule.class.getDeclaredField("tagScheduleId");
            field.setAccessible(true);
            field.set(tagSchedule, tagScheduleId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // Tag ID로 태그 스케줄을 찾는 테스트입니다.
    @Test
    void testFindByTagId() {
        when(tagScheduleRepository.findAllByTag_TagId(anyLong())).thenReturn(Collections.singletonList(sampleTagSchedule));

        var result = tagScheduleService.findByTagId(1L);

        System.out.println("Result of findByTagId: " + result);

        assertThat(result).isNotEmpty();
        verify(tagScheduleRepository).findAllByTag_TagId(1L);
    }

    // 스케줄 ID와 태그 ID로 태그 스케줄을 찾는 테스트입니다.
    @Test
    void testFindByScheduleIdAndTagId() {
        when(tagScheduleRepository.findAllBySchedule_ScheduleId(anyLong())).thenReturn(Collections.singletonList(sampleTagSchedule));

        var result = tagScheduleService.findByScheduleIdAndTagId(1L, 1L);

        System.out.println("Result of findByScheduleIdAndTagId: " + result);

        assertThat(result).isNotNull();
        verify(tagScheduleRepository).findAllBySchedule_ScheduleId(1L);
    }

    // 태그 스케줄 ID로 태그를 찾는 테스트입니다.
    @Test
    void testFindTag() {
        when(tagScheduleRepository.findById(anyLong())).thenReturn(Optional.of(sampleTagSchedule));

        TagResponseDTO result = tagScheduleService.findTag(1L);

        System.out.println("Result of findTag: " + result);

        assertThat(result).isNotNull();
        verify(tagScheduleRepository).findById(1L);
    }

    // 태그 스케줄 ID로 태그 스케줄을 찾는 테스트입니다.
    @Test
    void testFindTagSchedule() {
        when(tagScheduleRepository.findById(anyLong())).thenReturn(Optional.of(sampleTagSchedule));

        TagScheduleResponseDTO result = tagScheduleService.findTagSchedule(1L);

        System.out.println("Result of findTagSchedule: " + result);

        assertThat(result).isNotNull();
        verify(tagScheduleRepository).findById(1L);
    }

    // 스케줄에 태그를 추가하는 테스트입니다.
    @Test
    void testAddTagSchedule() {
        when(tagService.getTagId(anyString())).thenReturn(1L);
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(sampleTag));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(sampleSchedule));
        when(tagScheduleRepository.save(any(TagSchedule.class))).thenReturn(sampleTagSchedule);

        Long result = tagScheduleService.addTagSchedule(1L, "Sample Tag");

        System.out.println("Result of addTagSchedule: " + result);

        assertThat(result).isNotNull();
        verify(tagScheduleRepository).save(any(TagSchedule.class));
    }

    // 태그 스케줄을 삭제하는 테스트입니다.
    @Test
    void testDeleteTag() {
        when(tagScheduleRepository.findById(anyLong())).thenReturn(Optional.of(sampleTagSchedule));

        Long result = tagScheduleService.deleteTag(1L);

        System.out.println("Result of deleteTag: " + result);

        assertThat(result).isNotNull();
        verify(tagScheduleRepository).findById(1L);
    }
}
