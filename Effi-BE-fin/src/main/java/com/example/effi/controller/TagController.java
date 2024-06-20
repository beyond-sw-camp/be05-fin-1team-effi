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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public ResponseEntity<?> findTagByScheduleId(@PathVariable("scheduleId") Long scheduleId) {
        List<Long> tagList = tagScheduleService.findTagIdList(scheduleId);
        List<TagResponseDTO> rtn = new ArrayList<>();
        for (Long tag : tagList) {
            rtn.add(tagService.getTagById(tag));
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

    //////////////////////////////////////////////////////////////////////
    // 내 태그 탑5 찾기
    @GetMapping("/find/top5Tag")
    public ResponseEntity<?> findTop5Tag() {
        // 탑 5 여기서 해야하나? 분석은?
        try {
            List<Long> myTagList = tagScheduleService.findMyTagList();
            Collections.sort(myTagList);
            Long last = tagService.findLastTagId();
            List<Long> counts = new ArrayList<>(Collections.nCopies(last.intValue() + 1, 0L)); // TagId : 사용 갯수
            for (Long a : myTagList){
                Long l = counts.get(a.intValue());
                counts.set(a.intValue(), ++l);
            }

            List<Integer> indices = IntStream.range(0, counts.size())
                    .boxed()
                    .filter(index -> index != 0) // 인덱스 0 제거
                    .collect(Collectors.toList());

            // 인덱스 리스트를 counts 값에 따라 정렬
            indices.sort(Comparator.comparing(counts::get).reversed());

            List<String> tagList = new ArrayList<>();
            for (Integer idx = 0 ; idx < 5 ; idx++){
                Long l = Long.valueOf(indices.get(idx));
                tagList.add(tagService.getTagName(l));
            }

            return ResponseEntity.ok(tagList);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // 기타 예외로 인한 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to find my Tag: " + e.getMessage());
        }
    }

    @GetMapping("/find/tagRatio")
    public ResponseEntity<?> findTagRatio() {
        // 비율을 리턴 -> 어떻게?
        try {
            List<Long> myTagList = tagScheduleService.findMyTagList();
            Collections.sort(myTagList);
            Long last = tagService.findLastTagId();
            List<Long> counts = new ArrayList<>(Collections.nCopies(last.intValue() + 1, 0L)); // TagId : 사용 갯수
            for (Long a : myTagList){
                Long l = counts.get(a.intValue());
                counts.set(a.intValue(), ++l);
            }

            Long sum = 0L;
            for (Integer idx = 0; idx < counts.size(); idx++){
                sum += counts.get(idx);
            } // 총 개수

            return ResponseEntity.ok(null);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // 기타 예외로 인한 실패
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to find my Tag: " + e.getMessage());
        }
    }

}
