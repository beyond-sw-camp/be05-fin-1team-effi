package com.example.effi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.effi.domain.Entitiy.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>{

    
}
