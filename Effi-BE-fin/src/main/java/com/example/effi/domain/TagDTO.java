package com.example.effi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TagDTO {
    private Long tagId;
    private String tagName;

    public TagDTO(Long tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public Tag toEntity(){
        return Tag.builder()
                .tagName(tagName)
                .build();
    }
}
