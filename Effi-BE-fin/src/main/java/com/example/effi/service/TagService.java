package com.example.effi.service;

import com.example.effi.domain.Entitiy.Tag;
import com.example.effi.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Transactional
@Service
public class TagService {
    private final TagRepository tagRepository;

    // tag 추가
    public void addTag(String tagName) {
        tagRepository.save(new Tag(tagName));
    }

    // tag 조회 (이름으로 검색 id return)
    public Long getTagId(String tagName) {
        Tag lst =  tagRepository.findTagByTagName(tagName);
        if (lst == null) {
            return null; // 오류 처리용
        }
        return lst.getTagId();
    }

}
