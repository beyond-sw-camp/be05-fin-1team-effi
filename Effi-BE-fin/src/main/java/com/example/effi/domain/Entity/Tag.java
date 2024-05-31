package com.example.effi.domain.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @Column(name = "tag_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @Column(name = "tag_name", nullable = false)
    private String tagName;

    @Builder
    public Tag(String tagName) {
        this.tagName = tagName;
    }
}
