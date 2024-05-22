package com.example.effi.repository;

import com.example.effi.domain.Entitiy.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findAllBySchedule_ScheduleId(Long scheduleId);
    List<Participant> findAllByEmployee_Id(Long userId);
    Participant findByEmployeeIdAndScheduleId(Long employeeId, Long scheduleId);
}
