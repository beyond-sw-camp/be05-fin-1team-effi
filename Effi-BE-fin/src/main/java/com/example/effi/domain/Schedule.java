package com.example.effi.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @Column(name = "schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schedule_id;

    @Column(name = "title")
    private String title;

    @Column(name = "context")
    private String context;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "status")
    private Integer status;

    @Column(name = "notification_yn")
    private Boolean notificationYn;

    @Column(name = "delete_yn")
    private Boolean deleteYn;

    //category
    @Column(name = "category_id")
    private Long categoryId;

    //routine
    @Column(name = "routine_id")
    private Long routineId;

    @Builder
    public Schedule(String title, String context, Date startTime, Date endTime, Integer status,
                    Boolean notificationYn, Boolean deleteYn, Long categoryId, Long routineId){
        this.title = title;
        this.context = context;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.notificationYn = notificationYn;
        this.deleteYn = deleteYn;
        this.categoryId = categoryId; //
        this.routineId = routineId; //
    }
}
