package com.example.effi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.effi.domain.Entity.Participant;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findAllBySchedule_ScheduleId(Long scheduleId);
    List<Participant> findAllByEmployee_Id(Long userId);
    Participant findByEmployee_IdAndSchedule_ScheduleId(Long employeeId, Long scheduleId);
}
