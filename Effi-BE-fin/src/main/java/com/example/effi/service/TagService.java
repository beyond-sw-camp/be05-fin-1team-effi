package com.example.effi.service;

import com.example.effi.domain.DTO.TagResponseDTO;
import com.example.effi.domain.Entitiy.Tag;
import com.example.effi.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Transactional
@Service
public class TagService {
    private final TagRepository tagRepository;

    // tag 추가 tagId 리턴
    public Long addTag(String tagName) {
        if (getTagId(tagName) == null)
            tagRepository.save(new Tag(tagName));
        return getTagId(tagName);
    }

    // tag 조회 (이름으로 검색 id return)
    public Long getTagId(String tagName) {
        Tag lst =  tagRepository.findTagByTagName(tagName);
        if (lst == null) {
            return null; // 오류 처리용
        }
        return lst.getTagId();
    }

    //tag 조회(id로 검색 name 리턴)
    public String getTagName(Long tagId) {
        Tag tag = tagRepository.findById(tagId).get();
        if (tag == null) {
            return null;
        }
        return tag.getTagName();
    }

    // tag 조회 (tag 리턴)
    public TagResponseDTO getTag(String tagName) {
        Tag tag = tagRepository.findTagByTagName(tagName);
        if (tag == null) {
            return null;
        }
        return new TagResponseDTO(tag);
    }

    //  tag 전체 조회
    public List<TagResponseDTO> getAllTag() {
        List<Tag> tag = tagRepository.findAll();
        if (tag == null) {
            return null;
        }
        List<TagResponseDTO> tagResponseDTOS = new ArrayList<>();
        for (Tag t : tag) {
            tagResponseDTOS.add(new TagResponseDTO(t));
        }
        return tagResponseDTOS;
    }


}
