package com.example.effi.domain.DTO;

import com.example.effi.domain.Entitiy.Category;
import com.example.effi.domain.Entitiy.Routine;
import com.example.effi.domain.Entitiy.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

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

    private Long categoryId;
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
        this.categoryId = schedule.getCategory().getCategoryId();
        this.routineId = schedule.getRoutine().getRoutineId();
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
                .build();
    }
}
