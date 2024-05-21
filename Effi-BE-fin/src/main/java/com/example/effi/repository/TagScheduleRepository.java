package com.example.effi.repository;

import com.example.effi.domain.Entitiy.TagSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagScheduleRepository extends JpaRepository<TagSchedule, Long> {

}