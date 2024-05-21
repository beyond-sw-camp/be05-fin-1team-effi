package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryDTO {
    private Long categoryId;
    private String categoryName;

    public CategoryDTO(Long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Category toEntity(){
        return Category.builder()
                .categoryName(categoryName)
                .build();
    }
}