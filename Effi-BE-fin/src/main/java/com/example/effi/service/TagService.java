package com.example.effi.service;

import com.example.effi.domain.Dto.Tag.TagResponseDTO;
import com.example.effi.domain.Entity.Tag;
import com.example.effi.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
        Optional<Tag> tag = tagRepository.findById(tagId); // 수정된 부분
        if (tag.isEmpty()) { // 수정된 부분
            return null;
        }
        return tag.get().getTagName(); // 수정된 부분
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
        List<Tag> tags = tagRepository.findAll(); // 수정된 부분
        List<TagResponseDTO> tagResponseDTOS = new ArrayList<>();
        for (Tag t : tags) { // 수정된 부분
            tagResponseDTOS.add(new TagResponseDTO(t));
        }
        return tagResponseDTOS;
    }


}
