package com.example.effi.domain.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "category")
public class Category {
    @Id
    @Column(name = "category_no", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryNo;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Dept dept;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder
    public Category(Long categoryNo, Long categoryId, String categoryName, Dept dept, Group group) {
        this.categoryNo = categoryNo;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.dept = dept;
        this.group = group;
    }
}
