package com.example.effi.service;

import com.example.effi.domain.DTO.MyPageResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Transactional
@Service
public interface MyPageService {

    MyPageResponseDTO getEmployee(Long empId);


}
