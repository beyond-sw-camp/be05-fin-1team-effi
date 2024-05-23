package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.TagSchedule;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TagScheduleResponseDTO {
    private Long tagScheduleId;
    private Boolean deleteYn;

    private Long tagId;
    private Long scheduleId;

    public TagScheduleResponseDTO(TagSchedule tagSchedule) {
        this.tagScheduleId = tagSchedule.getTagScheduleId();
        this.deleteYn = tagSchedule.getDeleteYn();
        this.scheduleId = tagSchedule.getSchedule().getScheduleId();
        this.tagId = tagSchedule.getTag().getTagId();
    }
}
