package com.example.effi.domain.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import com.example.effi.domain.Entity.Category;
import com.example.effi.domain.Entity.Routine;
import com.example.effi.domain.Entity.Schedule;

@Setter
@Getter
@NoArgsConstructor
public class ScheduleRequestDTO {
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

    private Long categoryNo;
    private Long routineId;

    public ScheduleRequestDTO(Schedule schedule) {
        this.scheduleId = schedule.getScheduleId();
        this.title = schedule.getTitle();
        this.context = schedule.getContext();
        this.startTime = schedule.getStartTime();
        this.endTime = schedule.getEndTime();
        this.status = schedule.getStatus();
        this.notificationYn = schedule.getNotificationYn();
        this.deleteYn = schedule.getDeleteYn();
        this.createdAt = schedule.getCreatedAt();
        this.updatedAt = schedule.getUpdatedAt();
        this.categoryNo = schedule.getCategory().getCategoryNo();
        if (schedule.getRoutine() != null)
            this.routineId = schedule.getRoutine().getRoutineId();
        else
            this.routineId = null;
    }

    public Schedule toEntity(Category category, Routine routine) {
        return Schedule.builder()
                .title(title)
                .context(context)
                .startTime(startTime)
                .endTime(endTime)
                .status(status)
                .notificationYn(notificationYn)
                .deleteYn(deleteYn)
                .category(category)
                .routine(routine)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
