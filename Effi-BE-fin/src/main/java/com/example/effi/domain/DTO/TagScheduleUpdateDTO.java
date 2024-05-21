package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.Schedule;
import com.example.effi.domain.Entitiy.Tag;
import com.example.effi.domain.Entitiy.TagSchedule;
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

