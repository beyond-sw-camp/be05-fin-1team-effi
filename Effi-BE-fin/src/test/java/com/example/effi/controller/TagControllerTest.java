package com.example.effi.controller;

import com.example.effi.domain.DTO.*;
import com.example.effi.domain.Entity.*;
import com.example.effi.repository.TagRepository;
import com.example.effi.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private TagRepository tagRepository;

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
                .category(sampleCategory)
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

    @DisplayName("addTag - 성공적으로 태그 추가")
    @Test
    void testAddTag() {
        String inputString = "sampleTag";
        Tag tag = Tag.builder().tagName(inputString).build();
        TagResponseDTO responseDTO = new TagResponseDTO();
        responseDTO.setTagId(tag.getTagId());
        responseDTO.setTagName(tag.getTagName());

        when(tagRepository.findTagByTagName(inputString)).thenReturn(null);
        when(tagService.addTag(inputString)).thenReturn(tag.getTagId());
        when(tagService.getTag(inputString)).thenReturn(responseDTO);

        TagResponseDTO result = tagController.addTag(inputString);

        assertEquals(responseDTO, result);
    }

    @DisplayName("addTag - 이미 존재하는 태그")
    @Test
    void testAddTagExisting() {
        String inputString = "sampleTag";
        Tag tag = Tag.builder().tagName(inputString).build();
        TagResponseDTO responseDTO = new TagResponseDTO();
        responseDTO.setTagId(tag.getTagId());
        responseDTO.setTagName(tag.getTagName());

        when(tagRepository.findTagByTagName(inputString)).thenReturn(tag);
        when(tagService.getTag(inputString)).thenReturn(responseDTO);

        TagResponseDTO result = tagController.addTag(inputString);

        assertEquals(responseDTO, result);
    }

    @DisplayName("findTagByScheduleId - 성공적으로 태그 조회")
    @Test
    void testFindTagByScheduleId() {
        Long scheduleId = 1L;
        List<Long> tagIds = Arrays.asList(1L, 2L);
        Tag tag1 = Tag.builder().tagName("tag1").build();
        Tag tag2 = Tag.builder().tagName("tag2").build();
        TagResponseDTO responseDTO1 = new TagResponseDTO();
        responseDTO1.setTagId(tag1.getTagId());
        responseDTO1.setTagName(tag1.getTagName());
        TagResponseDTO responseDTO2 = new TagResponseDTO();
        responseDTO2.setTagId(tag2.getTagId());
        responseDTO2.setTagName(tag2.getTagName());

        when(tagScheduleService.findTagIdList(scheduleId)).thenReturn(tagIds);
        when(tagService.getTagById(1L)).thenReturn(responseDTO1);
        when(tagService.getTagById(2L)).thenReturn(responseDTO2);

        ResponseEntity<?> response = tagController.findTagByScheduleId(scheduleId);
        List<TagResponseDTO> result = (List<TagResponseDTO>) response.getBody();

        assertEquals(2, result.size());
        assertEquals(responseDTO1, result.get(0));
        assertEquals(responseDTO2, result.get(1));
    }

    @DisplayName("findTagByScheduleId - 태그 없음")
    @Test
    void testFindTagByScheduleIdNoTags() {
        Long scheduleId = 1L;

        when(tagScheduleService.findTagIdList(scheduleId)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = tagController.findTagByScheduleId(scheduleId);
        List<TagResponseDTO> result = (List<TagResponseDTO>) response.getBody();

        assertEquals(0, result.size());
    }

    @DisplayName("addTagWithSchedule - 성공적으로 스케줄에 태그 추가")
    @Test
    void testAddTagWithSchedule() {
        when(tagScheduleService.addTagSchedule(anyLong(), anyString())).thenReturn(1L);
        TagResponseDTO sampleTagResponseDTO = new TagResponseDTO();
        sampleTagResponseDTO.setTagId(1L);
        sampleTagResponseDTO.setTagName("Sample Tag");

        when(tagService.getTag(anyString())).thenReturn(sampleTagResponseDTO);
        TagDTO tag = new TagDTO();
        tag.setTag("Sample Tag");

        TagResponseDTO response = tagController.addTagWithSchedule(1L, tag);

        assertThat(response.getTagId()).isEqualTo(1L);
        assertThat(response.getTagName()).isEqualTo("Sample Tag");

        verify(tagScheduleService).addTagSchedule(anyLong(), anyString());
        verify(tagService).getTag(anyString());
    }

    @DisplayName("addTagWithSchedule - 스케줄에 태그 추가 실패")
    @Test
    void testAddTagWithScheduleFailure() {
        doThrow(new RuntimeException("Tag creation failed")).when(tagScheduleService).addTagSchedule(anyLong(),
                anyString());

        TagDTO tag = new TagDTO();
        tag.setTag("Sample Tag");

        assertThrows(RuntimeException.class, () -> {
            tagController.addTagWithSchedule(1L, tag);
        });

        verify(tagScheduleService).addTagSchedule(anyLong(), anyString());
        verify(tagService, never()).getTag(anyString());
    }

    @DisplayName("findTag - 태그 조회 성공")
    @Test
    void testFindTag() {
        when(tagService.getTag(anyString())).thenReturn(sampleTagResponseDTO);

        ResponseEntity<TagResponseDTO> response = tagController.findTag("Sample Tag");

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTagId()).isEqualTo(1L);
        assertThat(response.getBody().getTagName()).isEqualTo("Sample Tag");

        verify(tagService).getTag(anyString());
    }

    @DisplayName("findTag - 태그 조회 실패")
    @Test
    void testFindTagFailure() {
        when(tagService.getTag(anyString())).thenThrow(new RuntimeException("Tag not found"));

        assertThrows(RuntimeException.class, () -> {
            tagController.findTag("Nonexistent Tag");
        });

        verify(tagService).getTag(anyString());
    }

    @DisplayName("findAllTag - 전체 태그 조회 성공")
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

    @DisplayName("findAllTag - 전체 태그 조회 실패")
    @Test
    void testFindAllTagFailure() {
        when(tagService.getAllTag()).thenThrow(new RuntimeException("Failed to fetch tags"));

        assertThrows(RuntimeException.class, () -> {
            tagController.findAllTag();
        });

        verify(tagService).getAllTag();
    }

    @DisplayName("findTagById - 태그 ID로 태그 조회 성공")
    @Test
    void testFindTagById() {
        when(tagScheduleService.findByTagId(anyLong()))
                .thenReturn(Collections.singletonList(sampleTagScheduleResponseDTO));
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

    @DisplayName("findTagById - 태그 ID로 태그 조회 실패")
    @Test
    void testFindTagByIdFailure() {
        when(tagScheduleService.findByTagId(anyLong())).thenThrow(new RuntimeException("Tag not found"));

        assertThrows(RuntimeException.class, () -> {
            tagController.findTagById(1L);
        });

        verify(tagScheduleService).findByTagId(anyLong());
    }

    @DisplayName("updateTag - 태그 수정 성공")
    @Test
    void testUpdateTag() {
        when(tagScheduleService.findByScheduleIdAndTagId(anyLong(), anyLong()))
                .thenReturn(sampleTagScheduleResponseDTO);
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

    @DisplayName("updateTag - 태그 수정 실패")
    @Test
    void testUpdateTagFailure() {
        when(tagScheduleService.findByScheduleIdAndTagId(anyLong(), anyLong()))
                .thenThrow(new RuntimeException("TagSchedule not found"));

        assertThrows(RuntimeException.class, () -> {
            tagController.updateTag(1L, 1L, "Updated Tag");
        });

        verify(tagScheduleService).findByScheduleIdAndTagId(anyLong(), anyLong());
    }

    @DisplayName("deleteTag - 태그 삭제 성공")
    @Test
    void testDeleteTag() {
        when(tagScheduleService.findByScheduleIdAndTagId(anyLong(), anyLong()))
                .thenReturn(sampleTagScheduleResponseDTO);

        ResponseEntity<?> response = tagController.deleteTag(1L, 1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        verify(tagScheduleService).findByScheduleIdAndTagId(anyLong(), anyLong());
        verify(tagScheduleService).deleteTag(anyLong());
    }

    @DisplayName("deleteTag - 태그 삭제 실패")
    @Test
    void testDeleteTagFailure() {
        when(tagScheduleService.findByScheduleIdAndTagId(anyLong(), anyLong()))
                .thenThrow(new RuntimeException("TagSchedule not found"));

        assertThrows(RuntimeException.class, () -> {
            tagController.deleteTag(1L, 1L);
        });

        verify(tagScheduleService).findByScheduleIdAndTagId(anyLong(), anyLong());
    }
}
