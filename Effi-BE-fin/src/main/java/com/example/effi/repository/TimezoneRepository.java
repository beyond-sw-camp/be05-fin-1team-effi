package com.example.effi.repository;

import com.example.effi.domain.DTO.TimezoneListDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import com.example.effi.domain.Entity.Timezone;

import java.util.List;

@Repository
public interface TimezoneRepository extends JpaRepository<Timezone, Long> {

    @Query("select new com.example.effi.domain.DTO.TimezoneListDTO(t.timezoneId, t.timezoneName) " +
            "from Timezone t " +
            "group by t.timezoneName")
    List<TimezoneListDTO> findAllTimezonesGroupedByName();

}
