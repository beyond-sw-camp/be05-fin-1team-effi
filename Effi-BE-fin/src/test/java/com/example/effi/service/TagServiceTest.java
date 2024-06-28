package com.example.effi.service;

import com.example.effi.domain.DTO.TagResponseDTO;
import com.example.effi.domain.Entity.Tag;
import com.example.effi.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag sampleTag;

    @BeforeEach
    void setUp() {
        sampleTag = new Tag("Sample Tag");
        setTagId(sampleTag, 1L);
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

    @DisplayName("Tag 추가 - 성공")
    @Test
    void testAddTag() {
        when(tagRepository.findTagByTagName(anyString())).thenReturn(null);
        when(tagRepository.save(any(Tag.class))).thenReturn(sampleTag);

        Long tagId = tagService.addTag("Sample Tag");

        assertThat(tagId).isEqualTo(1L);
        verify(tagRepository).save(any(Tag.class));
        verify(tagRepository).findTagByTagName(anyString());
    }

    @DisplayName("Tag 추가 - 실패")
    @Test
    void testAddTagFailure() {
        when(tagRepository.findTagByTagName(anyString())).thenReturn(sampleTag);

        assertThrows(RuntimeException.class, () -> {
            tagService.addTag("Sample Tag");
        });

        verify(tagRepository, times(1)).findTagByTagName(anyString());
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @DisplayName("Tag Id 조회 - 성공")
    @Test
    void testGetTagId() {
        when(tagRepository.findTagByTagName(anyString())).thenReturn(sampleTag);

        Long tagId = tagService.getTagId("Sample Tag");

        assertThat(tagId).isEqualTo(1L);
    }

    @DisplayName("Tag Id 조회 - 실패")
    @Test
    void testGetTagIdFailure() {
        when(tagRepository.findTagByTagName(anyString())).thenReturn(null);

        Long tagId = tagService.getTagId("Nonexistent Tag");

        assertThat(tagId).isNull();
        verify(tagRepository).findTagByTagName(anyString());
    }

    @DisplayName("Tag Name 조회 - 성공")
    @Test
    void testGetTagName() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(sampleTag));

        String tagName = tagService.getTagName(1L);

        assertThat(tagName).isEqualTo("Sample Tag");
    }

    @DisplayName("Tag Name 조회 - 실패")
    @Test
    void testGetTagNameFailure() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            tagService.getTagName(1L);
        });

        verify(tagRepository).findById(anyLong());
    }

    @DisplayName("Tag 조회 - 성공")
    @Test
    void testGetTag() {
        when(tagRepository.findTagByTagName(anyString())).thenReturn(sampleTag);

        TagResponseDTO tagResponseDTO = tagService.getTag("Sample Tag");

        assertThat(tagResponseDTO).isNotNull();
        assertThat(tagResponseDTO.getTagId()).isEqualTo(1L);
        assertThat(tagResponseDTO.getTagName()).isEqualTo("Sample Tag");
    }

    @DisplayName("Tag 조회 - 실패")
    @Test
    void testGetTagFailure() {
        when(tagRepository.findTagByTagName(anyString())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            tagService.getTag("Nonexistent Tag");
        });

        verify(tagRepository).findTagByTagName(anyString());
    }

    @DisplayName("전체 Tag 조회 - 성공")
    @Test
    void testGetAllTag() {
        Tag tag1 = new Tag("Tag 1");
        Tag tag2 = new Tag("Tag 2");
        setTagId(tag1, 1L);
        setTagId(tag2, 2L);
        when(tagRepository.findAll()).thenReturn(Arrays.asList(tag1, tag2));

        List<TagResponseDTO> allTags = tagService.getAllTag();

        assertThat(allTags).isNotNull();
        assertThat(allTags).hasSize(2);
        assertThat(allTags.get(0).getTagId()).isEqualTo(1L);
        assertThat(allTags.get(0).getTagName()).isEqualTo("Tag 1");
        assertThat(allTags.get(1).getTagId()).isEqualTo(2L);
        assertThat(allTags.get(1).getTagName()).isEqualTo("Tag 2");
    }

    @DisplayName("전체 Tag 조회 - 실패")
    @Test
    void testGetAllTagFailure() {
        when(tagRepository.findAll()).thenThrow(new RuntimeException("Failed to fetch tags"));

        assertThrows(RuntimeException.class, () -> {
            tagService.getAllTag();
        });

        verify(tagRepository).findAll();
    }
}
