package com.example.effi.repository;

import com.example.effi.domain.Entitiy.Timezone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimezoneRepository extends JpaRepository<Timezone, Long> {
}
