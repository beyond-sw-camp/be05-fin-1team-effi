package com.example.effi.domain.DTO;

import com.example.effi.domain.Entity.Tag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
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
