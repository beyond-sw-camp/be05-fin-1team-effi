package com.example.effi.domain.Dto.Tag;

import com.example.effi.domain.Entity.Tag;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TagResponseDTO {
    private Long tagId;
    private String tagName;

    public TagResponseDTO(Tag tag) {
        this.tagId = tag.getTagId();
        this.tagName = tag.getTagName();
    }
}
