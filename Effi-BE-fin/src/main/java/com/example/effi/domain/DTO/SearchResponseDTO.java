package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.Schedule;
import com.example.effi.domain.Entitiy.TagSchedule;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class SearchResponseDTO {

    private Long scheduleId;
    private String title;
    private String context;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Boolean notificationYn;
    private Boolean deleteYn;
    private Date createdAt;
    private Date updatedAt;

    private Long categoryId;
    private String categoryName;
    private Long routineId;


    public SearchResponseDTO(Schedule schedule){
        this.scheduleId = schedule.getScheduleId();
        this.title = schedule.getTitle();
        this.context = schedule.getContext();
        this.startTime = schedule.getStartTime();
        this.endTime = schedule.getEndTime();
        this.status = schedule.getStatus();
        this.notificationYn = schedule.getNotificationYn();
        this.createdAt = schedule.getCreatedAt();
        this.updatedAt = schedule.getUpdatedAt();
        this.deleteYn = schedule.getDeleteYn();
        this.categoryId = schedule.getCategory().getCategoryId();
        this.categoryName = schedule.getCategory().getCategoryName();
        if (schedule.getRoutine() != null)
            this.routineId = schedule.getRoutine().getRoutineId();
        else
            this.routineId = null;

    }
}
