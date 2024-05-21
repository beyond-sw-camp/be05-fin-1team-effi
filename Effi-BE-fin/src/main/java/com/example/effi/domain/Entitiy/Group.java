package com.example.effi.domain.Entitiy;

import com.example.effi.domain.Entitiy.Category;
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
    private Long groupId;

    @Column(name = "group_name")
    private String groupName;

    // category와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "delete_yn")
    private Boolean deleteYn;

    @Builder
    public Group(String groupName, Boolean deleteYn, Category category) {
        this.groupName = groupName;
        this.deleteYn = deleteYn;
        this.category = category;
    }
}
