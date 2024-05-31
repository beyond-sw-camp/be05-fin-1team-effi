package com.example.effi.domain.DTO;

import com.example.effi.domain.Entity.TagSchedule;

import lombok.Builder;
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
//        this.scheduleId = tagSchedule.getSchedule().getScheduleId();
//        this.tagId = tagSchedule.getTag().getTagId();

        this.scheduleId = (tagSchedule.getSchedule() != null) ? tagSchedule.getSchedule().getScheduleId() : null;
        this.tagId = (tagSchedule.getTag() != null) ? tagSchedule.getTag().getTagId() : null;
 /*
       this.categoryId = (schedule.getCategory() != null) ? schedule.getCategory().getCategoryId() : null; // null 체크
        this.categoryName = (schedule.getCategory() != null) ? schedule.getCategory().getCategoryName() : null; // null 체크

         */

    }

    @Builder
    public TagScheduleResponseDTO(Long tagScheduleId, Boolean deleteYn, Long tagId, Long scheduleId){
        this.tagScheduleId = tagScheduleId;
        this.deleteYn = deleteYn;
        this.tagId = tagId;
        this.scheduleId = scheduleId;
    }

}
