package com.example.effi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.effi.domain.Entity.Schedule;
import com.example.effi.domain.Entity.TagSchedule;

import java.util.List;

@Repository
public interface TagScheduleRepository extends JpaRepository<TagSchedule, Long> {
    List<TagSchedule> findAllBySchedule_ScheduleId(Long scheduleId);

    List<TagSchedule> findAllByTag_TagId(Long tagId);

    // containing ignore case -> 대소문자 구분 x / %tagName%
    @Query("SELECT s " +
            "FROM Schedule s " +
            "JOIN TagSchedule ts " +
            "ON s.scheduleId = ts.schedule.scheduleId " +
            "JOIN ts.tag t " +
            "WHERE LOWER(t.tagName) " +
            "LIKE LOWER(CONCAT('%', :tagName, '%')) " +
            "AND ts.deleteYn = false")
    List<Schedule> findAllByTag_TagNameContainingIgnoreCase(@Param("tagName") String tagName);


}

