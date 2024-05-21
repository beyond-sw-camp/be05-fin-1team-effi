package com.example.effi.service;

import com.example.effi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // 카테고리 조회 (id)

}
