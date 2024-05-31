package com.example.effi.domain.Entity;

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
    @Column(name = "tag_schedule_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagScheduleId;

    // tag tbl
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    // schedule tbl
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Column(name = "delete_yn", nullable = false)
    private Boolean deleteYn;

    @Builder
    public TagSchedule(Tag tag, Schedule schedule, Boolean deleteYn) {
        this.tag = tag;
        this.schedule = schedule;
        this.deleteYn = deleteYn;
    }

    public void delete(){
        this.deleteYn = true;
    }
}
