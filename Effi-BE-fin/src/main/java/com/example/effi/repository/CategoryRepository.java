package com.example.effi.repository;

import com.example.effi.domain.Entitiy.Category;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // category_id로 카테고리를 조회하는 메서드
    Optional<Category> findByCategoryId(int i);
}

