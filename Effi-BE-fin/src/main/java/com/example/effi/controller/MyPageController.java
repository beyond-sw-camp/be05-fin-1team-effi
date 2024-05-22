package com.example.effi.controller;


import com.example.effi.domain.DTO.MyPageResponseDTO;
import com.example.effi.service.MyPageService;
import com.example.effi.service.MyPageServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MyPageServiceImpl myPageService;

    @GetMapping(value = "/{empNo}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MyPageResponseDTO> mypageView(@PathVariable("empNo") Long empNo){
        MyPageResponseDTO mypage = myPageService.getEmployee(empNo);
        return ResponseEntity.ok(mypage);
    }

}
