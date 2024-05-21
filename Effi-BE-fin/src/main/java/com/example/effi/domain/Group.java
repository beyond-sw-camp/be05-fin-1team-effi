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
public class Group{
    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deptId;

    @Column(name = "group_name")
    private String deptName;

    // category와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "delete_yn")
    private Boolean deleteYn;

    @Builder
    public Group(String groupName, Boolean deleteYn, Category category) {
        this.deptName = groupName;
        this.deleteYn = deleteYn;
        this.category = category;
    }
}
