package com.example.effi.repository;

import com.example.effi.domain.Entitiy.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
   Schedule findByScheduleId(Long scheduleId);
   List<Schedule> findAllByCategory_CategoryId(Long categoryId);
}
