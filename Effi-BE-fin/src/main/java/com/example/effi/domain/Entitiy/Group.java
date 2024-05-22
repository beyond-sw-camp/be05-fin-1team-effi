package com.example.effi.domain.Entitiy;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

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

    @Builder(toBuilder = true)
    public Group(String groupName, Boolean deleteYn, Category category, Date createdAt, Date updatedAt) {
        this.groupName = groupName;
        this.deleteYn = false;
        this.category = category;
        this.createdAt = Date.valueOf(LocalDate.now()); // 현재 날짜로 초기화
        this.updatedAt = updatedAt;
    }
    
    // 그룹의 카테고리를 설정하는 setter 메서드
    public void setCategory(Category category) {
        this.category = category;
    }

    // 그룹 이름을 설정하는 setter 메서드
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    // 그룹 업데이트 시간을 설정하는 setter 메서드
    public void setUpdatedAt(Date timestamp) {
        this.updatedAt = timestamp;
    }

}
