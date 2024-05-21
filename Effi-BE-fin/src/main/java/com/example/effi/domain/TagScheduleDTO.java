package com.example.effi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
