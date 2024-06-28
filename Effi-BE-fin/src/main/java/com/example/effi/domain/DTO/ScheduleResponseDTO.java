package com.example.effi.domain.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import com.example.effi.domain.Entity.Schedule;

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

    private Long categoryNo;
    private String categoryName;

    private Long groupId;
    private Long routineId;

    private String categoryColor; 

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
        this.categoryNo = schedule.getCategory().getCategoryNo();
        this.categoryName = schedule.getCategory().getCategoryName();

        if(schedule.getCategory().getGroup() != null){
            this.groupId = schedule.getCategory().getGroup().getGroupId();
        }

        this.categoryColor = getCategoryColor(schedule.getCategory().getCategoryName());
        if (schedule.getRoutine() != null)
            this.routineId = schedule.getRoutine().getRoutineId();
        else
            this.routineId = null;
    }

    private String getCategoryColor(String categoryName) {
        switch (categoryName) {
            case "회사":
                return "#EAFFCF";
            case "부서":
                return "#ABC4FF";
            case "그룹":
                return "#EAB9F0";
            case "개인":
                return "#FFB5C9";
            default:
                return "gray";
        }
    }

}
