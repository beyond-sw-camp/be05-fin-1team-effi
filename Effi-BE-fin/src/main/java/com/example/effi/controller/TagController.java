package com.example.effi.controller;

import com.example.effi.domain.DTO.TagResponseDTO;
import com.example.effi.domain.DTO.TagScheduleResponseDTO;
import com.example.effi.service.TagScheduleService;
import com.example.effi.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
@AllArgsConstructor
public class TagController {
    private final TagService tagService;
    private final TagScheduleService tagScheduleService;

    // add
    @PostMapping("/add")
    public TagResponseDTO addTag(@RequestBody String inputString) {
        Long tagId = tagService.addTag(inputString);
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


    // update
    // t-s에서 tag id & scheduleid로 t-s 찾아서 지우고 (deleteYn)
    // 새로운 태그 찾아서 t-s에 저장
    @PostMapping("/update/{scheduleId}/{tagId}")
    public ResponseEntity<TagResponseDTO> updateTag(@PathVariable("scheduleId") Long scheduleId,
                                                    @PathVariable("tagId") Long tagId,
                                                    @RequestParam String newTag) {
        return new ResponseEntity<TagResponseDTO>(null);
    }


    // delete
    // t-s에서 tagid & scheduleId 찾아서 deleteYn
    @PostMapping("/delete/{scheduleId}/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable("scheduleId") Long scheduleId,
                                                    @PathVariable("tagId") Long tagId,
                                                    @RequestParam String newTag) {
        TagScheduleResponseDTO res = tagScheduleService.findByScheduleIdAndTagId(scheduleId, tagId);
        tagScheduleService.deleteTag(res.getTagScheduleId());
        return ResponseEntity.ok(null);
    }
}
