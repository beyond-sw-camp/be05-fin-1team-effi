package com.example.effi.service;

import com.example.effi.domain.Dto.Schedule.CategoryResponseDTO;
import com.example.effi.domain.Entity.Category;
import com.example.effi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    //id로 찾기
    public CategoryResponseDTO findCategory(Long categoryId){
        Category category = categoryRepository.findById(categoryId).orElse(null);
        return new CategoryResponseDTO(category);
    }
}
