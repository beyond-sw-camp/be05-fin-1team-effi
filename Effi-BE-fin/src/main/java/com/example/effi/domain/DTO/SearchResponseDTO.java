package com.example.effi.domain.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import com.example.effi.domain.Entity.Schedule;
import com.example.effi.domain.Entity.TagSchedule;

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

    private Long groupId;

    private Long routineId;

    private List<String> tagNames;


    public SearchResponseDTO(Schedule schedule, List<String> tagNames){
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

        this.categoryId = (schedule.getCategory() != null) ? schedule.getCategory().getCategoryId() : null; // null 체크
        this.categoryName = (schedule.getCategory() != null) ? schedule.getCategory().getCategoryName() : null; // null 체크

        if(schedule.getCategory().getGroup() != null){
            this.groupId=schedule.getCategory().getGroup().getGroupId();
        }
        if (schedule.getRoutine() != null)
            this.routineId = schedule.getRoutine().getRoutineId();
        else
            this.routineId = null;

        this.tagNames = tagNames;

    }

    @Builder
    public SearchResponseDTO(Long scheduleId, String title, String context, Date startTime, Date endTime,
                             Integer status, Boolean notificationYn, Boolean deleteYn, Date createdAt,
                             Date updatedAt, Long categoryId, String categoryName,Long groupId, Long routineId, List<String> tagNames) {
        this.scheduleId = scheduleId;
        this.title = title;
        this.context = context;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.notificationYn = notificationYn;
        this.deleteYn = deleteYn;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.groupId = groupId;
        this.routineId = routineId;
        this.tagNames = tagNames;
    }
}

