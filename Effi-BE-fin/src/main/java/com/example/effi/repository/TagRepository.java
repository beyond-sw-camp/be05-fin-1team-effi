package com.example.effi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.effi.domain.Entity.Schedule;
import com.example.effi.domain.Entity.Tag;

import java.util.List;


@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findTagByTagName(String tagName);

}
