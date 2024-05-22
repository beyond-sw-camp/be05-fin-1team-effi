package com.example.effi.service;

import com.example.effi.domain.DTO.TagResponseDTO;
import com.example.effi.domain.DTO.TagScheduleResponseDTO;
import com.example.effi.domain.Entitiy.Tag;
import com.example.effi.domain.Entitiy.TagSchedule;
import com.example.effi.repository.ScheduleRepository;
import com.example.effi.repository.TagRepository;
import com.example.effi.repository.TagScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Transactional
@Service
public class TagScheduleService {
    private final TagScheduleRepository tagScheduleRepository;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ScheduleRepository scheduleRepository;

    // tagId & scheduleId로 tagSchedule return
    public TagScheduleResponseDTO findByScheduleIdAndTagId(Long scheduleId, Long tagId) {
        List<TagSchedule> lst = tagScheduleRepository.findAllBySchedule_ScheduleId(scheduleId);
        for (TagSchedule tagSchedule : lst) {
            if (tagSchedule.getTag().getTagId() == tagId)
                return new TagScheduleResponseDTO(tagSchedule);
        }
        return null;
    }

    //t-sId로 tag 리턴
    public TagResponseDTO findTag(Long tagScheduleId){
        TagSchedule tagSchedule = tagScheduleRepository.findById(tagScheduleId).orElse(null);
        if(tagSchedule == null)
            return null;
        Tag tag = tagSchedule.getTag();
        return new TagResponseDTO(tag);
    }

    //t-sId로 t-s 리턴
    public TagScheduleResponseDTO findSchedule(Long tagScheduleId){
        TagSchedule tagSchedule = tagScheduleRepository.findById(tagScheduleId).orElse(null);
        return new TagScheduleResponseDTO(tagSchedule);
    }

    // schedule에 tag 추가했을 경우 (scheduleid 입력받았다고 가정) -> tagschedule 칼럼 추가
    // 이름으로 tag 찾아서 있는지 확인
    // (없을경우 tag 추가 후) tagschedule id 리턴
    public Long addTagSchedule(Long schduleId, String tagName) {
        Long tagId = tagService.getTagId(tagName);
        if (tagId == null)
            tagId = tagService.addTag(tagName);
        return tagScheduleRepository.save(new TagSchedule(tagRepository.findById(tagId).get(),
                scheduleRepository.findById(schduleId).get(), false)).getTagScheduleId();
    }

    // schedule에서 tag 삭제 -> deleteYn 수정
    // tagName으로 id 찾아서 scheduleTagId 찾아서 dto 찾아서 저장하기.
    // return tagscheduleId
    public Long deleteTag(Long tagScheduleId) {
        TagSchedule tagSchedule = tagScheduleRepository.findById(tagScheduleId).orElse(null);
        if (tagSchedule != null) {
            tagSchedule.delete();
            return tagSchedule.getTagScheduleId();
        }
        return null;
    }

}
