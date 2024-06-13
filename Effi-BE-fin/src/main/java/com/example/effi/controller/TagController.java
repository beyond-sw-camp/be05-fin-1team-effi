package com.example.effi.controller;

import com.example.effi.domain.DTO.ScheduleResponseDTO;
import com.example.effi.domain.DTO.TagResponseDTO;
import com.example.effi.domain.DTO.TagScheduleResponseDTO;
import com.example.effi.domain.Entity.Tag;
import com.example.effi.repository.TagRepository;
import com.example.effi.service.ScheduleService;
import com.example.effi.service.TagScheduleService;
import com.example.effi.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tag")
@AllArgsConstructor
public class TagController {
    private final TagService tagService;
    private final TagScheduleService tagScheduleService;
    private final ScheduleService scheduleService;
    private final TagRepository tagRepository;

    // add -> 있는 경우 기존 responseDTO 리턴
    @PostMapping("/add")
    public TagResponseDTO addTag(@RequestBody String inputString) {
        Tag tagByTagName = tagRepository.findTagByTagName(inputString);
        if (tagByTagName == null)
            tagService.addTag(inputString);
        TagResponseDTO tag = tagService.getTag(inputString);
        return (tagService.getTag(inputString));
    }

    // add with schedule
    @PostMapping("/add/{scheduleId}")
    public TagResponseDTO addTagWithSchedule(@PathVariable("scheduleId") Long scheduleId, @RequestBody String inputString) {
        tagScheduleService.addTagSchedule(scheduleId, inputString);
        return tagService.getTag(inputString);
    }

    // select (예은)
    // 아래는 tagname으로 tag 찾기
    @GetMapping("/find")
    public ResponseEntity<TagResponseDTO> findTag(@RequestParam String inputString) {
        return ResponseEntity.ok(tagService.getTag(inputString));
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<TagResponseDTO>> findAllTag() {
        return ResponseEntity.ok(tagService.getAllTag());
    }

    // tagId로 schedule 찾기
    @GetMapping("/findtag/{tagId}")
    public ResponseEntity<List<ScheduleResponseDTO>> findTagById(@PathVariable("tagId") Long tagId) {
        List<TagScheduleResponseDTO> tagScheduleResponseDTOList = tagScheduleService.findByTagId(tagId);
        List<ScheduleResponseDTO> rtn = new ArrayList<>();
        for (TagScheduleResponseDTO tagScheduleResponseDTO : tagScheduleResponseDTOList) {
            Long scheduleId = tagScheduleResponseDTO.getScheduleId();
            rtn.add(scheduleService.getSchedule(scheduleId));
        }
        return ResponseEntity.ok(rtn);
    }

    //scheduleId로 tag 찾기
    @GetMapping("/find/schedule/{scheduleId}")
    public ResponseEntity<List<TagResponseDTO>> findTagByScheduleId(@PathVariable("scheduleId") Long scheduleId) {
        List<Long> tagIdList = tagScheduleService.findTagIdList(scheduleId);
        List<TagResponseDTO> rtn = new ArrayList<>();
        for (Long tagId : tagIdList) {
            rtn.add(tagService.getTagById(tagId));
        }
        return ResponseEntity.ok(rtn);
    }

    // update
    // t-s에서 tag id & scheduleid로 t-s 찾아서 지우고 (deleteYn)
    // 새로운 태그 찾아서 t-s에 저장
    @PostMapping("/update/{scheduleId}/{tagId}")
    public ResponseEntity<TagScheduleResponseDTO> updateTag(@PathVariable("scheduleId") Long scheduleId,
                                                    @PathVariable("tagId") Long tagId,
                                                    @RequestParam String newTag) {
        TagScheduleResponseDTO res = tagScheduleService.findByScheduleIdAndTagId(scheduleId, tagId);
        tagScheduleService.deleteTag(res.getTagScheduleId());
        Long id = tagScheduleService.addTagSchedule(scheduleId, newTag);
        return ResponseEntity.ok(tagScheduleService.findTagSchedule(id));
    }


    // delete
    // t-s에서 tagid & scheduleId 찾아서 deleteYn
    @PostMapping("/delete/{scheduleId}/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable("scheduleId") Long scheduleId,
                                                    @PathVariable("tagId") Long tagId) {
        TagScheduleResponseDTO res = tagScheduleService.findByScheduleIdAndTagId(scheduleId, tagId);
        tagScheduleService.deleteTag(res.getTagScheduleId());
        return ResponseEntity.ok(null);
    }
}
