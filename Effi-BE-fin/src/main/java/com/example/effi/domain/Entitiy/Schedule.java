package com.example.effi.domain.Entitiy;

import com.example.effi.domain.Entitiy.Category;
import com.example.effi.domain.Entitiy.Routine;
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
    private Long scheduleId;

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

    // category와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    //routine
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @Builder
    public Schedule(String title, String context, Date startTime, Date endTime, Integer status,
                    Boolean notificationYn, Boolean deleteYn, Category category, Routine routine){
        this.title = title;
        this.context = context;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.notificationYn = notificationYn;
        this.deleteYn = deleteYn;
        this.category = category;
        this.routine = routine;
    }
}
