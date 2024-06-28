package com.example.effi.service;

import com.example.effi.domain.DTO.TagResponseDTO;
import com.example.effi.domain.DTO.TagScheduleResponseDTO;
import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Schedule;
import com.example.effi.domain.Entity.Tag;
import com.example.effi.domain.Entity.TagSchedule;
import com.example.effi.repository.ScheduleRepository;
import com.example.effi.repository.TagRepository;
import com.example.effi.repository.TagScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
                .startTime(new Date()).endTime(new Date())
                .status(1)
                .notificationYn(true)
                .deleteYn(false)
                .category(sampleCategory)
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

    @DisplayName("tagId로 tag 추가한 schedule 찾기 - 성공")
    @Test
    void testFindByTagId() {
        when(tagScheduleRepository.findAllByTag_TagId(anyLong())).thenReturn(Collections.singletonList(sampleTagSchedule));

        var result = tagScheduleService.findByTagId(1L);

        assertThat(result).isNotEmpty();
        verify(tagScheduleRepository).findAllByTag_TagId(1L);
    }

    @DisplayName("tagId로 tag 추가한 schedule 찾기 - 실패")
    @Test
    void testFindByTagIdFailure() {
        when(tagScheduleRepository.findAllByTag_TagId(anyLong())).thenReturn(Collections.emptyList());

        var result = tagScheduleService.findByTagId(1L);

        assertThat(result).isEmpty();
        verify(tagScheduleRepository).findAllByTag_TagId(1L);
    }

    @DisplayName("tagId와 scheduleId로 tagschedule 찾기 - 성공")
    @Test
    void testFindByScheduleIdAndTagId() {
        when(tagScheduleRepository.findAllBySchedule_ScheduleId(anyLong())).thenReturn(Collections.singletonList(sampleTagSchedule));

        var result = tagScheduleService.findByScheduleIdAndTagId(1L, 1L);

        assertThat(result).isNotNull();
        verify(tagScheduleRepository).findAllBySchedule_ScheduleId(1L);
    }

    @DisplayName("tagId와 scheduleId로 tagschedule 찾기 - 실패")
    @Test
    void testFindByScheduleIdAndTagIdFailure() {
        when(tagScheduleRepository.findAllBySchedule_ScheduleId(anyLong())).thenReturn(Collections.emptyList());

        var result = tagScheduleService.findByScheduleIdAndTagId(1L, 1L);

        assertThat(result).isNull();
        verify(tagScheduleRepository).findAllBySchedule_ScheduleId(1L);
    }

    @DisplayName("tagId로 tag 찾기 - 성공")
    @Test
    void testFindTag() {
        when(tagScheduleRepository.findById(anyLong())).thenReturn(Optional.of(sampleTagSchedule));

        TagResponseDTO result = tagScheduleService.findTag(1L);

        assertThat(result).isNotNull();
        verify(tagScheduleRepository).findById(1L);
    }

    @DisplayName("tagId로 tag 찾기 - 실패")
    @Test
    void testFindTagFailure() {
        when(tagScheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        var result = tagScheduleService.findTag(1L);

        assertThat(result).isNull();
        verify(tagScheduleRepository).findById(1L);
    }

    @DisplayName("tagscheduleId로 tagschedule 찾기 - 성공")
    @Test
    void testFindTagSchedule() {
        when(tagScheduleRepository.findById(anyLong())).thenReturn(Optional.of(sampleTagSchedule));

        TagScheduleResponseDTO result = tagScheduleService.findTagSchedule(1L);

        assertThat(result).isNotNull();
        verify(tagScheduleRepository).findById(1L);
    }

    @DisplayName("tagscheduleId로 tagschedule 찾기 - 실패")
    @Test
    void testFindTagScheduleFailure() {
        when(tagScheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        var result = tagScheduleService.findTagSchedule(1L);

        assertThat(result).isNull();
        verify(tagScheduleRepository).findById(1L);
    }

    @DisplayName("tagId랑 tagName으로 tagschedule 추가 - 성공")
    @Test
    void testAddTagSchedule() {
        when(tagService.getTagId(anyString())).thenReturn(1L);
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(sampleTag));
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(sampleSchedule));
        when(tagScheduleRepository.save(any(TagSchedule.class))).thenReturn(sampleTagSchedule);

        Long result = tagScheduleService.addTagSchedule(1L, "Sample Tag");

        assertThat(result).isNotNull();
        verify(tagScheduleRepository).save(any(TagSchedule.class));
    }

    @DisplayName("tagId랑 tagName으로 tagschedule 추가 - 실패")
    @Test
    void testAddTagScheduleFailure() {
        when(tagService.getTagId(anyString())).thenReturn(1L);
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.of(sampleSchedule));

        var result = tagScheduleService.addTagSchedule(1L, "Sample Tag");

        assertThat(result).isNull();
        verify(tagRepository).findById(anyLong());
        verify(scheduleRepository).findById(anyLong());
        verify(tagScheduleRepository, never()).save(any(TagSchedule.class));
    }

    @DisplayName("tag 삭제 - 성공")
    @Test
    void testDeleteTag() {
        when(tagScheduleRepository.findById(anyLong())).thenReturn(Optional.of(sampleTagSchedule));

        Long result = tagScheduleService.deleteTag(1L);

        assertThat(result).isNotNull();
        verify(tagScheduleRepository).findById(1L);
    }

    @DisplayName("tag 삭제 - 성공")
    @Test
    void testDeleteTagFailure() {
        when(tagScheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        var result = tagScheduleService.deleteTag(1L);

        assertThat(result).isNull();
        verify(tagScheduleRepository).findById(1L);
    }

    @DisplayName("scheduleId로 tag id 조회 - 성공")
    @Test
    void testFindTagIdList() {
        Long scheduleId = 1L;
        Schedule schedule = Schedule.builder().build();
        schedule.setScheduleId(scheduleId);

        Tag tag1 = Tag.builder().tagName("tag1").build();
        Tag tag2 = Tag.builder().tagName("tag2").build();
        tag1.setTagId(1L);
        tag2.setTagId(2L);

        List<TagSchedule> tagSchedules = Arrays.asList(
                new TagSchedule(tag1, schedule, false), new TagSchedule(tag2, schedule, false)
        );

        when(tagScheduleRepository.findAllBySchedule_ScheduleId(scheduleId)).thenReturn(tagSchedules);
        List<Long> tagIdList = tagScheduleService.findTagIdList(scheduleId);
        assertEquals(Arrays.asList(1L, 2L), tagIdList);
    }

    @DisplayName("scheduleId로 tag id 조회 - 실패")
    @Test
    void testFindTagIdListFailure() {
        Long scheduleId = 1L;
        when(tagScheduleRepository.findAllBySchedule_ScheduleId(scheduleId)).thenReturn(Collections.emptyList());

        List<Long> tagIdList = tagScheduleService.findTagIdList(scheduleId);
        assertTrue(tagIdList.isEmpty());
    }
}
