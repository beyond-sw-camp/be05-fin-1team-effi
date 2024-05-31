package com.example.effi.domain.DTO;

import com.example.effi.domain.Entity.Schedule;
import com.example.effi.domain.Entity.Tag;
import com.example.effi.domain.Entity.TagSchedule;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TagScheduleDTO {

    private Long tagScheduleId;
    private Boolean deleteYn;

    private Long tagId;
    private Long scheduleId;

    public TagScheduleDTO(Long tagScheduleId, Boolean deleteYn, Tag tag, Schedule schedule) {
        this.tagScheduleId = tagScheduleId;
        this.deleteYn = deleteYn;
        this.scheduleId = schedule.getScheduleId();
        this.tagId = tag.getTagId();
    }

    public TagSchedule toEntity(Tag tag, Schedule schedule){
        return TagSchedule.builder()
                .deleteYn(deleteYn)
                .schedule(schedule)
                .tag(tag)
                .build();
    }

}
