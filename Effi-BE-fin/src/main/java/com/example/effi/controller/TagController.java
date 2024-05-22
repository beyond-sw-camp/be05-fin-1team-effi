package com.example.effi.controller;

import com.example.effi.domain.DTO.TagResponseDTO;
import com.example.effi.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
@AllArgsConstructor
public class TagController {
    private final TagService tagService;

    // add
    @PostMapping("/add")
    public TagResponseDTO addTag(@RequestBody String inputString) {
        Long tagId = tagService.addTag(inputString);
        return tagService.getTag(inputString);
    }

    // select (예은)
    // 아래는 tagname으로 tag 찾기
    @GetMapping("/find")
    public TagResponseDTO findTag(@RequestParam String inputString) {
        return tagService.getTag(inputString);
    }

    @GetMapping("/findAll")
    public List<TagResponseDTO> findAllTag() {
        return tagService.getAllTag();
    }


    // update
    // t-s에서 tag id & scheduleid로 t-s 찾아서 지우고 (deleteYn)
    // 새로운 태그 찾아서 t-s에 저장


    // delete
    // t-s에서 tagid & scheduleId 찾아서 deleteYn
}
