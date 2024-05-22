package com.example.effi.domain.Entitiy;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "groups")
public class Group{
    @Id
    @Column(name = "group_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    // category와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "delete_yn", nullable = false)
    private Boolean deleteYn;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Builder
    public Group(String groupName, Boolean deleteYn, Category category, Date createdAt, Date updatedAt) {
        this.groupName = groupName;
        this.deleteYn = deleteYn;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // 그룹의 카테고리를 설정하는 setter 메서드
    public void setCategory(Category category) {
        this.category = category;
    }

}
