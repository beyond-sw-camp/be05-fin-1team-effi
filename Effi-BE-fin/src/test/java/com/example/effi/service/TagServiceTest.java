
package com.example.effi.service;

import com.example.effi.domain.DTO.TagResponseDTO;
import com.example.effi.domain.Entitiy.Tag;
import com.example.effi.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        // Reflection을 사용하여 private 필드에 값을 설정할 수 있습니다.
        // 이를 통해 @Builder 없이도 필드를 설정할 수 있습니다.
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

    @Test
    void testAddTag() {
        when(tagRepository.findTagByTagName(anyString())).thenReturn(null).thenReturn(sampleTag);
        when(tagRepository.save(any(Tag.class))).thenReturn(sampleTag);

        Long tagId = tagService.addTag("Sample Tag");

        assertThat(tagId).isEqualTo(1L);
        verify(tagRepository).save(any(Tag.class));
        verify(tagRepository, times(2)).findTagByTagName(anyString());
    }


    @Test
    void testGetTagId() {
        when(tagRepository.findTagByTagName(anyString())).thenReturn(sampleTag);

        Long tagId = tagService.getTagId("Sample Tag");

        assertThat(tagId).isEqualTo(1L);
    }

    @Test
    void testGetTagName() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(sampleTag));

        String tagName = tagService.getTagName(1L);

        assertThat(tagName).isEqualTo("Sample Tag");
    }

    @Test
    void testGetTag() {
        when(tagRepository.findTagByTagName(anyString())).thenReturn(sampleTag);

        TagResponseDTO tagResponseDTO = tagService.getTag("Sample Tag");

        assertThat(tagResponseDTO).isNotNull();
        assertThat(tagResponseDTO.getTagId()).isEqualTo(1L);
        assertThat(tagResponseDTO.getTagName()).isEqualTo("Sample Tag");
    }

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
}

