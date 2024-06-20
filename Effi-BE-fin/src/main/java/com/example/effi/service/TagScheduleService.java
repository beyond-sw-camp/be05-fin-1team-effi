package com.example.effi.service;

import com.example.effi.domain.DTO.ParticipantResponseDTO;
import com.example.effi.domain.DTO.TagResponseDTO;
import com.example.effi.domain.DTO.TagScheduleResponseDTO;
import com.example.effi.domain.Entity.Schedule;
import com.example.effi.domain.Entity.Tag;
import com.example.effi.domain.Entity.TagSchedule;
import com.example.effi.repository.ScheduleRepository;
import com.example.effi.repository.TagRepository;
import com.example.effi.repository.TagScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Transactional
@Service
public class TagScheduleService {
    private final TagScheduleRepository tagScheduleRepository;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ScheduleRepository scheduleRepository;
    private final EmployeeService employeeService;
    private final ParticipantService participantService;
    private final ScheduleService scheduleService;

    // tagId로 scheduleId List rtn
    public List<TagScheduleResponseDTO> findByTagId(Long tagId) {
        List<TagSchedule> tagScheduleList = tagScheduleRepository.findAllByTag_TagId(tagId);
        List<TagScheduleResponseDTO> rtn = new ArrayList<>();
        for (TagSchedule tagSchedule : tagScheduleList) {
            if (tagSchedule.getDeleteYn() == false)
                rtn.add(new TagScheduleResponseDTO(tagSchedule));
        }
        return rtn;
    }

    // tagId & scheduleId로 tagSchedule return
    public TagScheduleResponseDTO findByScheduleIdAndTagId(Long scheduleId, Long tagId) {
        List<TagSchedule> lst = tagScheduleRepository.findAllBySchedule_ScheduleId(scheduleId);
        for (TagSchedule tagSchedule : lst) {
            if (tagSchedule.getTag() != null && tagSchedule.getTag().getTagId().equals(tagId)) { // 수정된 부분
                return new TagScheduleResponseDTO(tagSchedule);
            }
        }
        return null;
    }

    //t-sId로 tag 리턴
    public TagResponseDTO findTag(Long tagScheduleId){
        TagSchedule tagSchedule = tagScheduleRepository.findById(tagScheduleId).orElse(null);
        if(tagSchedule == null || tagSchedule.getTag() == null) { // 수정된 부분
            return null;
        }
        Tag tag = tagSchedule.getTag();
        return new TagResponseDTO(tag);
    }

    //t-sId로 t-s 리턴
    public TagScheduleResponseDTO findTagSchedule(Long tagScheduleId){
        TagSchedule tagSchedule = tagScheduleRepository.findById(tagScheduleId).orElse(null);
        if(tagSchedule == null) { // 수정된 부분
            return null;
        }
        return new TagScheduleResponseDTO(tagSchedule);
    }

    // schedule에 tag 추가했을 경우 (scheduleid 입력받았다고 가정) -> tagschedule 칼럼 추가
    // 이름으로 tag 찾아서 있는지 확인
    // (없을경우 tag 추가 후) tagschedule id 리턴
    public Long addTagSchedule(Long scheduleId, String tagName) {
        Long tagId = tagService.getTagId(tagName);
        if (tagId == null) {
            tagId = tagService.addTag(tagName);
        }
        Optional<Tag> tagOpt = tagRepository.findById(tagId); // 수정된 부분
        Optional<Schedule> scheduleOpt = scheduleRepository.findById(scheduleId); // 수정된 부분

        if (tagOpt.isPresent() && scheduleOpt.isPresent()) { // 수정된 부분
            TagSchedule tagSchedule = new TagSchedule(tagOpt.get(), scheduleOpt.get(), false); // 수정된 부분
            return tagScheduleRepository.save(tagSchedule).getTagScheduleId(); // 수정된 부분
        }
        return null; // 수정된 부분
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

    // scheduleId로 tagId List 찾기
    public List<Long> findTagIdList(Long scheduleId){
        List<TagSchedule> allByScheduleScheduleId = tagScheduleRepository.findAllBySchedule_ScheduleId(scheduleId);
        List<Long> tagIdList = new ArrayList<>();
        for (TagSchedule tagSchedule : allByScheduleScheduleId) {
            tagIdList.add(tagSchedule.getTag().getTagId());
        }
        return tagIdList;
    }

    ////////////////////////////////////////////////////////////////
    // 내 태그 찾기 -> 내 스케줄 찾고 -> schdeuleId로 넘겨주자. -> Tag Id List 리턴 *
    public List<Long> findMyTagList(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorEmpNo = Long.valueOf(authentication.getName());
        Long empId = employeeService.findEmpIdByEmpNo(creatorEmpNo);

        List<ParticipantResponseDTO> allByEmpId = participantService.findAllByEmpId(empId);
        if (allByEmpId == null)
            throw new IllegalArgumentException("Tag not found");
        List<Long> scheduleIdList = new ArrayList<>();
        for (ParticipantResponseDTO dto : allByEmpId) {
            Long scheduleId = dto.getScheduleId();
            if (scheduleService.getSchedule(scheduleId)!= null
                    && scheduleService.getSchedule(scheduleId).getDeleteYn() == false)
                scheduleIdList.add(scheduleId);
        }
        if (scheduleIdList == null)
            throw new IllegalArgumentException("Tag not found");

        List<Long> tagIdList = new ArrayList<>();
        for(Long scheduleId : scheduleIdList){
            List<Long> tmpList = findTagIdList(scheduleId);
            for (Long tagId : tmpList) {
                tagIdList.add(tagId);
            }
        }
        if (tagIdList == null)
            throw new IllegalArgumentException("Tag not found");

        return tagIdList;
    }


}
