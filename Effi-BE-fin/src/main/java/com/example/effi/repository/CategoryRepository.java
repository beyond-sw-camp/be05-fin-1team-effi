package com.example.effi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Schedule;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // category_id로 카테고리를 조회하는 메서드
    Optional<Category> findByCategoryId(int i);

    Category findByCategoryId(Long categoryId);

    // categoryName 이 완전히 일치할 때 + 삭제되지 않음
    @Query("SELECT s " +
            "FROM Schedule s " +
            "WHERE s.category.categoryName = :categoryName " +
            "AND s.deleteYn = false")
    List<Schedule> findSchedulesByCategoryName(@Param("categoryName") String categoryName);

    Category findByDept_DeptId(Long deptId);

    Category findByGroup_GroupId(Long groupId);
}

