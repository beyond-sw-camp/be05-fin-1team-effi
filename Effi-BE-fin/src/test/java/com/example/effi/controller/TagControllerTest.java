package com.example.effi.controller;

import com.example.effi.domain.DTO.*;
import com.example.effi.domain.Entitiy.*;
import com.example.effi.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagControllerTest {

    @Mock
    private TagService tagService;

    @Mock
    private TagScheduleService tagScheduleService;

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private TagController tagController;

    private TagResponseDTO sampleTagResponseDTO;
    private TagScheduleResponseDTO sampleTagScheduleResponseDTO;
    private ScheduleResponseDTO sampleScheduleResponseDTO;
    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = Category.builder()
                .categoryId(1L)
                .categoryName("Sample Category")
                .build();

        Tag sampleTag = new Tag("Sample Tag");
        setTagId(sampleTag, 1L);
        sampleTagResponseDTO = new TagResponseDTO(sampleTag);

        Schedule sampleSchedule = Schedule.builder()
                .title("Sample Schedule")
                .context("Sample Context")
                .category(sampleCategory)  // Category 설정
                .build();
        setScheduleId(sampleSchedule, 1L);
        sampleScheduleResponseDTO = new ScheduleResponseDTO(sampleSchedule);

        TagSchedule sampleTagSchedule = TagSchedule.builder()
                .tag(sampleTag)
                .schedule(sampleSchedule)
                .deleteYn(false)
                .build();
        setTagScheduleId(sampleTagSchedule, 1L);
        sampleTagScheduleResponseDTO = new TagScheduleResponseDTO(sampleTagSchedule);
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

    @Test
    void testAddTag() {
        when(tagService.addTag(anyString())).thenReturn(1L);
        when(tagScheduleService.addTagSchedule(anyLong(), anyString())).thenReturn(1L);
        when(tagService.getTag(anyString())).thenReturn(sampleTagResponseDTO);

        TagResponseDTO response = tagController.addTag(1L, "Sample Tag");

        assertThat(response.getTagId()).isEqualTo(1L);
        assertThat(response.getTagName()).isEqualTo("Sample Tag");

        verify(tagService).addTag(anyString());
        verify(tagScheduleService).addTagSchedule(anyLong(), anyString());
        verify(tagService).getTag(anyString());
    }

    @Test
    void testFindTag() {
        when(tagService.getTag(anyString())).thenReturn(sampleTagResponseDTO);

        ResponseEntity<TagResponseDTO> response = tagController.findTag("Sample Tag");

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTagId()).isEqualTo(1L);
        assertThat(response.getBody().getTagName()).isEqualTo("Sample Tag");

        verify(tagService).getTag(anyString());
    }

    @Test
    void testFindAllTag() {
        when(tagService.getAllTag()).thenReturn(Collections.singletonList(sampleTagResponseDTO));

        ResponseEntity<List<TagResponseDTO>> response = tagController.findAllTag();

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getTagId()).isEqualTo(1L);
        assertThat(response.getBody().get(0).getTagName()).isEqualTo("Sample Tag");

        verify(tagService).getAllTag();
    }

    @Test
    void testFindTagById() {
        when(tagScheduleService.findByTagId(anyLong())).thenReturn(Collections.singletonList(sampleTagScheduleResponseDTO));
        when(scheduleService.getSchedule(anyLong())).thenReturn(sampleScheduleResponseDTO);

        ResponseEntity<List<ScheduleResponseDTO>> response = tagController.findTagById(1L);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getScheduleId()).isEqualTo(1L);
        assertThat(response.getBody().get(0).getTitle()).isEqualTo("Sample Schedule");
        assertThat(response.getBody().get(0).getContext()).isEqualTo("Sample Context");

        verify(tagScheduleService).findByTagId(anyLong());
        verify(scheduleService).getSchedule(anyLong());
    }

    @Test
    void testUpdateTag() {
        when(tagScheduleService.findByScheduleIdAndTagId(anyLong(), anyLong())).thenReturn(sampleTagScheduleResponseDTO);
        when(tagScheduleService.addTagSchedule(anyLong(), anyString())).thenReturn(1L);
        when(tagScheduleService.findTagSchedule(anyLong())).thenReturn(sampleTagScheduleResponseDTO);

        ResponseEntity<TagScheduleResponseDTO> response = tagController.updateTag(1L, 1L, "Updated Tag");

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTagScheduleId()).isEqualTo(1L);
        assertThat(response.getBody().getTagId()).isEqualTo(1L);
        assertThat(response.getBody().getScheduleId()).isEqualTo(1L);

        verify(tagScheduleService).findByScheduleIdAndTagId(anyLong(), anyLong());
        verify(tagScheduleService).deleteTag(anyLong());
        verify(tagScheduleService).addTagSchedule(anyLong(), anyString());
        verify(tagScheduleService).findTagSchedule(anyLong());
    }

    @Test
    void testDeleteTag() {
        when(tagScheduleService.findByScheduleIdAndTagId(anyLong(), anyLong())).thenReturn(sampleTagScheduleResponseDTO);

        ResponseEntity<?> response = tagController.deleteTag(1L, 1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        verify(tagScheduleService).findByScheduleIdAndTagId(anyLong(), anyLong());
        verify(tagScheduleService).deleteTag(anyLong());
    }
}
