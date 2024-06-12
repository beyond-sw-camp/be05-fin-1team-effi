package com.example.effi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.effi.domain.Entity.Schedule;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Schedule findByScheduleId(Long scheduleId);

    List<Schedule> findAllByCategory_CategoryId(Long categoryId);

    List<Schedule> findAllByRoutine_RoutineId(Long routineId);

    List<Schedule> findAllByCategory_CategoryNo(Long categoryNo);
}
