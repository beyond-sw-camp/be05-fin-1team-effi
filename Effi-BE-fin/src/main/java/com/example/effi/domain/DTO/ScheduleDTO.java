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
public class ScheduleDTO {
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

    public Schedule toEntity(Category category, Routine routine) {
        return Schedule.builder()
                .category(category)
                .routine(routine)
                .title(title)
                .context(context)
                .startTime(startTime)
                .endTime(endTime)
                .notificationYn(notificationYn)
                .deleteYn(deleteYn)
                .createdAt(new Date())
                .build();
    }

    public ScheduleDTO(Schedule schedule, Category category, Routine routine) {
        this.scheduleId = schedule.getScheduleId();
        this.title = schedule.getTitle();
        this.context = schedule.getContext();
        this.startTime = schedule.getStartTime();
        this.endTime = schedule.getEndTime();
        this.status = schedule.getStatus();
        this.notificationYn = schedule.getNotificationYn();
        this.deleteYn = schedule.getDeleteYn();
        this.categoryId = category.getCategoryId();
        this.routineId = routine.getRoutineId();
        this.createdAt = schedule.getCreatedAt();
        this.updatedAt = schedule.getUpdatedAt();
    }


}
