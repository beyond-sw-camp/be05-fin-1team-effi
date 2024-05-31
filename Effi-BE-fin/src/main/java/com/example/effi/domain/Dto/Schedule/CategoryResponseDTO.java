package com.example.effi.domain.Dto.Schedule;

import com.example.effi.domain.Entity.Category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
