package com.example.effi.repository;

import com.example.effi.domain.Entitiy.TagSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagScheduleRepository extends JpaRepository<TagSchedule, Long> {
    List<TagSchedule> findAllBySchedule_ScheduleId(Long scheduleId);

    List<TagSchedule> findAllByTag_TagId(Long tagId);
}
