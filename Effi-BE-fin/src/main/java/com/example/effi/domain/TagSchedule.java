package com.example.effi.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "tag_schedule")
public class TagSchedule {
    @Id
    @Column(name = "tag_schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagScheduleId;

    // tag tbl
    @Column(name = "tag_id")
    private Long tagId;

    // schedule tbl
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "delete_yn")
    private Boolean deleteYn;

    @Builder
    public TagSchedule(Long tagId, Long scheduleId, Boolean deleteYn) {
        this.tagId = tagId; //
        this.scheduleId = scheduleId; //
        this.deleteYn = deleteYn;
    }
}
