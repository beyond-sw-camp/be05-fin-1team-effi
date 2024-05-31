package com.example.effi.domain.DTO;

import com.example.effi.domain.Entity.Schedule;
import com.example.effi.domain.Entity.Tag;
import com.example.effi.domain.Entity.TagSchedule;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class TagScheduleUpdateDTO {
    private Long tagScheduleId;
    private Boolean deleteYn;

    public TagScheduleUpdateDTO(Long tagScheduleId) {
        this.tagScheduleId = tagScheduleId;
        this.deleteYn = true;
    }

}

