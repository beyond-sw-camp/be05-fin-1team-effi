package com.example.effi.controller;


import com.example.effi.domain.DTO.MyPageResponseDTO;
import com.example.effi.domain.DTO.MyPageUpdateDTO;
import com.example.effi.service.MyPageService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping(value = "/{empNo}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MyPageResponseDTO> mypageView(@PathVariable("empNo") Long empNo){
        MyPageResponseDTO mypage = myPageService.getEmployee(empNo);
        return ResponseEntity.ok(mypage);
    }

    @PutMapping(value = "/update/{empNo}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> updateTimezone(@RequestBody MyPageUpdateDTO myPageUpdateDTO) {
        myPageService.updateEmployeeTimezone(myPageUpdateDTO);
        return ResponseEntity.ok("Timezone updated successfully");
    }

}
