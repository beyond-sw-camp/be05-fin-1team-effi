package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.Category;
import com.example.effi.domain.Entitiy.Routine;
import com.example.effi.domain.Entitiy.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class ScheduleResponseDTO {
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
    private Long routineId;

    public ScheduleResponseDTO(Schedule schedule) {
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
        if (schedule.getRoutine() != null)
            this.routineId = schedule.getRoutine().getRoutineId();
        else
            this.routineId = null;
    }


}
