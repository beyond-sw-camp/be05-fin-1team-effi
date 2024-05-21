package com.example.effi.repository;

import com.example.effi.domain.Entitiy.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findTagByTagName(String tagName);
}
