package com.example.effi.repository;

import com.example.effi.domain.Entitiy.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Schedule, Long> {

    // containing ignore case -> 대소문자 구분 x / %title$
    List<Schedule> findAllByTitleContainingIgnoreCase(String title);

    // List<Schedule> findBy
}
