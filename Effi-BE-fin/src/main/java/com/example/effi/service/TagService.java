package com.example.effi.service;

import com.example.effi.domain.DTO.TagResponseDTO;
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

    /*
    // 기존 코드

    // tag 추가 tagId 리턴
    public Long addTag(String tagName) {
        if (getTagId(tagName) == null) {
            tagRepository.save(new Tag(tagName));
        }else{
            throw new RuntimeException("Tag already exists");
        }

        return getTagId(tagName);
    }

    // tag 조회 (이름으로 검색 id return)
    public Long getTagId(String tagName) {
        Tag lst =  tagRepository.findTagByTagName(tagName);
        if (lst == null) {
            throw new RuntimeException("Tag not found");
        }
        return lst.getTagId();
    }

    */

    // 테스트 후 수정 코드
    // tag 추가 tagId 리턴
    public Long addTag(String tagName) {
        Tag existingTag = tagRepository.findTagByTagName(tagName);
        if (existingTag == null) {
            Tag newTag = tagRepository.save(new Tag(tagName));
            return newTag.getTagId();
        } else {
            throw new RuntimeException("Tag already exists");
        }
    }

    // tag 조회 (이름으로 검색 id return)
    public Long getTagId(String tagName) {
        Tag lst = tagRepository.findTagByTagName(tagName);
        if (lst == null) {
            return null;
        }
        return lst.getTagId();
    }


    //tag 조회(id로 검색 name 리턴)
    public String getTagName(Long tagId) {
        Optional<Tag> tag = tagRepository.findById(tagId); // 수정된 부분
        if (tag.isEmpty()) { // 수정된 부분
            throw new RuntimeException("Tag not found");
        }
        return tag.get().getTagName(); // 수정된 부분
    }

    // tag 조회 (tag 리턴)
    public TagResponseDTO getTag(String tagName) {
        Tag tag = tagRepository.findTagByTagName(tagName);
        if (tag == null) {
            throw new RuntimeException("No tags found");
        }
        return new TagResponseDTO(tag);
    }

    //  tag 전체 조회
    public List<TagResponseDTO> getAllTag() {
        List<Tag> tags = tagRepository.findAll();
        if(tags.isEmpty()){
            throw new RuntimeException("No tags found");
        }
        List<TagResponseDTO> tagResponseDTOS = new ArrayList<>();
        for (Tag t : tags) { // 수정된 부분
            tagResponseDTOS.add(new TagResponseDTO(t));
        }
        return tagResponseDTOS;
    }

    //tagId -> tagDTO 리턴
    public TagResponseDTO getTagById(Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElse(null);
        return new TagResponseDTO(tag);
    }

    //////////////////////////////////////////////////////////////////////
    // 마지막 인덱스 찾기 *
    public Long findLastTagId(){
        List<TagResponseDTO> allTag = getAllTag();
        Long max = 0L;
        for (TagResponseDTO t : allTag) {
            if (max < t.getTagId()) {
                max = t.getTagId();
            }
        }
        return max;
    }

}
