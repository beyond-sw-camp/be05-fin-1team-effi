package com.example.effi.domain;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
