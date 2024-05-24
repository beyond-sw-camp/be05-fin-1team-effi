package com.example.effi.repository;

import com.example.effi.domain.Entitiy.Timezone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimezoneRepository extends JpaRepository<Timezone, Long> {


}
