package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryResponseDTO {
    private Long categoryId;
    private String categoryName;

    public CategoryResponseDTO(Category category) {
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();
    }
}