package com.example.effi.domain.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "schedule")
@EqualsAndHashCode
public class Schedule {
    @Id
    @Column(name = "schedule_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "context", nullable = false)
    private String context;

    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "end_time", nullable = false)
    private Date endTime;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "notification_yn", nullable = false)
    private Boolean notificationYn;

    @Column(name = "delete_yn", nullable = false)
    private Boolean deleteYn;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    // category와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_no")
    private Category category;

    //routine
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @Builder
    public Schedule(String title, String context, Date startTime, Date endTime, Integer status,
                    Boolean notificationYn, Boolean deleteYn, Date createdAt, Date updatedAt, Category category, Routine routine){
        this.title = title;
        this.context = context;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.notificationYn = notificationYn;
        this.deleteYn = deleteYn;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.category = category;
        this.routine = routine;
    }

    // ?
    public void update(String title, String context, Date startTime, Date endTime, Integer status,
                       Boolean notificationYn, Category category, Routine routine){
        this.title = title;
        this.context = context;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.notificationYn = notificationYn;
        this.category = category;
        this.routine = routine;
        this.updatedAt = new Date();
    }

    public void delete() {
        this.updatedAt = new Date();
        this.deleteYn = true;
    }
}
