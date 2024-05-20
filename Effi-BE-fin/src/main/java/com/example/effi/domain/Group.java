package com.example.effi.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "group")
public class Group {
    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deptId;

    @Column(name = "group_name")
    private String deptName;

    // category_id와 연결
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "delete_yn")
    private Boolean deleteYn;

    @Builder
    public Group(String groupName, Boolean deleteYn, Long categoryId) {
        this.deptName = groupName;
        this.deleteYn = deleteYn;
        this.categoryId = categoryId; //
    }
}
