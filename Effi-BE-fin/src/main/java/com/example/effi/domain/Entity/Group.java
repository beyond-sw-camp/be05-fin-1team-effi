package com.example.effi.domain.Entity;

import java.sql.Date;
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
public class Group {
    @Id
    @Column(name = "group_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @Column(name = "delete_yn", nullable = false)
    private Boolean deleteYn;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Builder(toBuilder = true)
    public Group(Long groupId, String groupName, Boolean deleteYn, Date createdAt, Date updatedAt) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.deleteYn = deleteYn;
        this.createdAt = createdAt != null ? createdAt : Date.valueOf(LocalDate.now());
        this.updatedAt = updatedAt;
    }

    public Group updateGroupName(String newGroupName) {
        return this.toBuilder()
                .groupName(newGroupName)
                .updatedAt(Date.valueOf(LocalDate.now()))
                .build();
    }

    public Group markAsDeleted() {
        return this.toBuilder()
                .deleteYn(true)
                .updatedAt(Date.valueOf(LocalDate.now()))
                .build();
    }
}
