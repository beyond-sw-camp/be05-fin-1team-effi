package com.example.effi.repository;

import com.example.effi.domain.Entitiy.Schedule;
import com.example.effi.domain.Entitiy.TagSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagScheduleRepository extends JpaRepository<TagSchedule, Long> {
    List<TagSchedule> findAllBySchedule_ScheduleId(Long scheduleId);

    List<TagSchedule> findAllByTag_TagId(Long tagId);

    // tagName 이 완전히 일치할 때 + 삭제되지 않음
    @Query("SELECT ts.schedule " +
            "FROM TagSchedule ts " +
            "WHERE ts.tag.tagName = :tagName " +
            "AND ts.deleteYn = false")
    List<Schedule> findSchedulesByTagName(@Param("tagName") String tagName);
}
